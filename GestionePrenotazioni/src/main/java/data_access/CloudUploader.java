package data_access;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
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
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CloudUploader {
    private static final String APPLICATION_NAME = "ProvaCampeggio";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "/prova_credenziali_drive.json";
    private static final String TOKENS_DIRECTORY_PATH = System.getProperty("user.home") + FileSystems.getDefault().getSeparator() + "GestionePrenotazioni" + FileSystems.getDefault().getSeparator() + "tokens";    //FIXME: crea cartella da qualche parte!
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);

    //FIXME: rimuovi quando funziona e chiama direttamente uploadFile
    public static void main(String[] args) {
        try {
            //uploadFile();
            String provaFile = "201712041856511000.jpg";
            importFileFromDrive(provaFile);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    //TODO: se il file è già presente sostituiscilo oppure fai una cartella con il giorno del caricamento?
    private static void uploadFile() throws IOException, GeneralSecurityException {
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
    }

    // Metodo per importare un file specifico dal Drive e inserirlo nella cartella delle risorse del progetto
    private static void importFileFromDrive(String fileName) throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT)).setApplicationName(APPLICATION_NAME).build();

        // Recupera l'elenco dei file dal Drive
        FileList result = service.files().list().setQ("name='" + fileName + "'").setFields("files(id, name)").execute();
        List<File> files = result.getFiles();

        if (files.isEmpty()) {
            System.out.println("File not found on Drive: " + fileName);
            return;
        }

        String fileId = files.get(0).getId();

        // Scarica il file dal Drive
        java.io.File tempFile = new java.io.File(System.getProperty("java.io.tmpdir"), fileName);
        try (OutputStream outputStream = new FileOutputStream(tempFile)) {
            service.files().get(fileId).executeMediaAndDownloadTo(outputStream);
        }

        // Copia il file nella cartella delle risorse del progetto
        java.io.File resourcesFolder = new java.io.File(CloudUploader.class.getResource("/").getFile());
        java.io.File importedFile = new java.io.File(resourcesFolder, fileName);

        try (InputStream inputStream = new FileInputStream(tempFile);
             OutputStream outputStream = new FileOutputStream(importedFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        // Elimina il file temporaneo
        tempFile.delete();

        System.out.println("File " + fileName + " imported successfully.");
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
