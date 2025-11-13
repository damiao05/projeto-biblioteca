package com.bibliotecaproject.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3;
    private final String bucketName = "capas-biblioteca";

    public S3Service() {
        this.s3 = S3Client.builder()
                .region(Region.of("us-east-2"))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public String uploadCapa(MultipartFile file, String isbn) throws IOException {
        String fileName = isbn + "-" + UUID.randomUUID() + ".jpg";

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .acl("public-read")
                .contentType(file.getContentType())
                .build();

        s3.putObject(request, RequestBody.fromBytes(file.getBytes()));

        return "https://" + bucketName + ".s3.us-east-2.amazonaws.com/" + fileName;
    }

}
