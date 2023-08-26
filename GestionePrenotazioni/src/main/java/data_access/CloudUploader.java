package data_access;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.file.FileSystems;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CloudUploader {
    private static final String APPLICATION_NAME = "AgricampingSpineta";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String TOKENS_DIRECTORY_PATH = System.getProperty("user.home") + FileSystems.getDefault().getSeparator() + "GestionePrenotazioni" + FileSystems.getDefault().getSeparator() + "tokens";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String BACKUP_FOLDER = System.getProperty("user.home") + FileSystems.getDefault().getSeparator()
            + "GestionePrenotazioni" + FileSystems.getDefault().getSeparator() + "backup";

    // Carica il file database.db su Google Drive
    public static boolean uploadDatabaseFile() {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT)).setApplicationName(APPLICATION_NAME).build();

            java.io.File backupFile = new java.io.File(BACKUP_FOLDER + FileSystems.getDefault().getSeparator() + "database.db");

            File fileMetadata = new File();
            fileMetadata.setName("database.db");

            // Crea un oggetto FileContent con il file da caricare
            FileContent mediaContent = new FileContent("application/sqlite", backupFile);

            // Esegui l'upload del file nella radice di Google Drive
            File uploadedFile = service.files().create(fileMetadata, mediaContent).setFields("id").execute();

            System.out.println("File ID: " + uploadedFile.getId());
            return true;
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Importa il file database.db in locale dal drive e lo sostituisce al db utilizzato attualmente
    public static boolean importFileFromDrive(String fileName) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME).build();

        // Recupera l'elenco dei file dal Drive
        FileList result = service.files().list().setQ("name='" + fileName + "'").setFields("files(id, name)").execute();
        List<File> files = result.getFiles();

        if (files.isEmpty()) {
            System.err.println("File non trovato su Drive: " + fileName);
            return false;
        }

        String fileId = files.get(0).getId();

        // Scarica il file dal Drive
        java.io.File tempFile = new java.io.File(System.getProperty("java.io.tmpdir"), fileName);
        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            service.files().get(fileId).executeMediaAndDownloadTo(outputStream);
        }

        // Crea la cartella di destinazione se non esiste
        java.io.File destinationDir = new java.io.File(BACKUP_FOLDER);
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }

        // Copia il file temporaneo nella cartella di destinazione
        java.io.File destinationFile = new java.io.File(destinationDir, fileName);
        try (InputStream inputStream = new FileInputStream(tempFile);
             OutputStream outputStream = new FileOutputStream(destinationFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        System.out.println("File " + fileName + " importato correttamente.");
        return true;
    }

    // Nel caso del primo avvio viene copiato in locale in file database.db
    public static void copyResourceDBtoLocal() {
        java.io.File backupFile = new java.io.File(BACKUP_FOLDER + FileSystems.getDefault().getSeparator() + "database.db");

        // Verifica se la cartella di destinazione esiste, altrimenti crea la cartella
        java.io.File backupFolder = new java.io.File(BACKUP_FOLDER);
        if (!backupFolder.exists()) {
            boolean created = backupFolder.mkdirs();
            if (!created) {
                System.err.println("Impossibile creare la cartella di backup");
                return;
            }
        }

        if (!backupFile.exists()) {
            URL resourceURL = CloudUploader.class.getResource("/database.db");
            try {
                // Crea il nuovo file backupFile
                boolean created = backupFile.createNewFile();
                if (created) {
                    // Copia il contenuto del file resourceURL nel nuovo file backupFile
                    try (InputStream inputStream = resourceURL.openStream();
                         OutputStream outputStream = new FileOutputStream(backupFile)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    System.out.println("Database di risorsa copiato nella cartella di backup");
                } else {
                    System.err.println("Impossibile copiare il database di risorse nella cartella di backup");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Errore durante la creazione del file database.db");
            }
        }
    }

    // Funzione che periodicamente cancella i vecchi backup dal drive (cancella i file prima di dateToDeleteBefore)
    public static void deleteFilesBeforeDate(LocalDate dateToDeleteBefore, JProgressBar progressBar) {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT)).setApplicationName(APPLICATION_NAME).build();

            // Recupera l'elenco dei file dal Drive
            List<File> files = service.files().list()
                    .setQ("name contains 'database'")  // Filtro per i file con nomi che iniziano con "database"
                    .setFields("files(id, name, createdTime)")
                    .execute()
                    .getFiles();

            int totalFiles = files.size();
            int deletedFiles = 0;

            if(totalFiles == 0){
                int progressValue = 100;
                progressBar.setValue(progressValue);

                return;
            }

            // Scansiona l'elenco dei file e cancella quelli con data precedente a quella specificata
            for (File file : files) {
                LocalDate fileCreationDate = LocalDate.parse(file.getCreatedTime().toStringRfc3339().substring(0, 10));
                if (fileCreationDate.isBefore(dateToDeleteBefore)) {
                    try {
                        service.files().delete(file.getId()).execute();
                        deletedFiles++;
                        System.out.println("File " + file.getName() + " rimosso correttamente.");
                    } catch (IOException e) {
                        System.out.println("Errore cancellazione file " + file.getName() + ": " + e.getMessage());
                    }
                }
                int progressValue = (int) ((double) deletedFiles / totalFiles * 100);
                progressBar.setValue(progressValue);
            }

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    // Ricava le credenziali salvata nelle risorse
    private static Credential getCredentials(final HttpTransport httpTransport) throws IOException {
        InputStream in = Objects.requireNonNull(CloudUploader.class.getResource(CREDENTIALS_FILE_PATH)).openStream();
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
