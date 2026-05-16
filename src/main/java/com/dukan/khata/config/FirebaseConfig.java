package com.dukan.khata.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account-path:}")
    private String serviceAccountPath;

    @Value("${FIREBASE_SERVICE_ACCOUNT_JSON:}")
    private String serviceAccountJson;

    @PostConstruct
    public void initFirebase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream credentialsStream;

            if (serviceAccountJson != null && !serviceAccountJson.isEmpty()) {
                // Production: read from environment variable
                credentialsStream = new ByteArrayInputStream(
                    serviceAccountJson.getBytes(StandardCharsets.UTF_8));
            } else {
                // Local development: read from file
                credentialsStream = new FileInputStream(serviceAccountPath);
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                    .build();
            FirebaseApp.initializeApp(options);
        }
    }

    @Bean
    public Firestore firestore() {
        return FirestoreClient.getFirestore();
    }
}