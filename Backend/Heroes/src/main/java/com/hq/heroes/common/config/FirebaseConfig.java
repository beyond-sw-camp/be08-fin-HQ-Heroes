package com.hq.heroes.common.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_JSON_NAME}")
    private String firebaseJsonPath;

    @Value("${FIREBASE_BUCKET_NAME}")
    private String firebaseBucketName;

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        // Load the Firebase service account key file
        FileInputStream serviceAccount = new FileInputStream(firebaseJsonPath);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket(firebaseBucketName)
                .build();

        // Initialize Firebase and return the instance
        return FirebaseApp.initializeApp(options);
    }
}
