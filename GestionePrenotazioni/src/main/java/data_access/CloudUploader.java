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

import java.io.*;
import java.nio.file.FileSystems;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CloudUploader {
    private static final String APPLICATION_NAME = "AgricampingSpineta";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    private static final String TOKENS_DIRECTORY_PATH = System.getProperty("user.home") + FileSystems.getDefault().getSeparator() + "GestionePrenotazioni" + FileSystems.getDefault().getSeparator() + "tokens";    //FIXME: crea cartella da qualche parte!
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);

    //TODO: se il file è già presente sostituiscilo oppure fai una cartella con il giorno del caricamento?
    public static boolean uploadDatabaseFile() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT)).setApplicationName(APPLICATION_NAME).build();

        // Create a new file on Drive
        InputStream fileStream = Objects.requireNonNull(CloudUploader.class.getResourceAsStream("/database.db"));
        java.io.File tempFile = createTempFile(fileStream, "database", ".db");
        File fileMetadata = new File();
        fileMetadata.setName("database.db");

        FileContent mediaContent = new FileContent("application/sqlite", tempFile);
        File uploadedFile = service.files().create(fileMetadata, mediaContent).setFields("id").execute();

        System.out.println("File ID: " + uploadedFile.getId());

        // Delete the temporary file
        tempFile.delete();
        return true;
    }

    //TODO: fai anche una versione per database.csv?

    //TODO: quando si importa il file e lo si ricarica c'è da gestire il fatto che il file ha lo stesso nome (-> lo sostituisce automaticamente?)

    public static boolean importFileFromDrive(String fileName) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME).build();

        // Recupera l'elenco dei file dal Drive
        FileList result = service.files().list().setQ("name='" + fileName + "'").setFields("files(id, name)").execute();
        List<File> files = result.getFiles();

        if (files.isEmpty()) {
            System.out.println("File not found on Drive: " + fileName);
            return false;
        }

        String fileId = files.get(0).getId();

        // Scarica il file dal Drive
        java.io.File tempFile = new java.io.File(System.getProperty("java.io.tmpdir"), fileName);
        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            service.files().get(fileId).executeMediaAndDownloadTo(outputStream);
        }

        // Cartella di destinazione
        String destinationFolder = System.getProperty("user.home") + FileSystems.getDefault().getSeparator()
                + "GestionePrenotazioni" + FileSystems.getDefault().getSeparator() + "backup";

        // Crea la cartella di destinazione se non esiste
        java.io.File destinationDir = new java.io.File(destinationFolder);
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }

        // Rinomina il file scaricato in "scaricato.db"
        java.io.File renamedFile = new java.io.File(destinationDir, fileName);
        if (tempFile.renameTo(renamedFile)) {
            System.out.println("File renamed to scaricato.db and moved to the destination folder.");
        } else {
            System.out.println("Failed to rename and move the file.");
            return false;
        }

        System.out.println("File " + fileName + " imported successfully.");
        return true;
    }


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

    private static java.io.File createTempFile(InputStream inputStream, String prefix, String suffix) throws IOException {
        java.io.File tempFile = java.io.File.createTempFile(prefix, suffix);
        tempFile.deleteOnExit();

        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return tempFile;
    }
}
