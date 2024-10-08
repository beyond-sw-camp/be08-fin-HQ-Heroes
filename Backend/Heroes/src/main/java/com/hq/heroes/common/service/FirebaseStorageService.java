package com.hq.heroes.common.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FirebaseStorageService {

    public String uploadFile(MultipartFile file, String fileName) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.create(fileName, file.getInputStream(), file.getContentType());

        // URL 형식으로 변환하여 반환 (token 없이)
        return String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucket.getName(), fileName.replace("/", "%2F"));
    }
}
