package com.chak.E_Commerce_Back_End.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageService1{

//    private final S3Client s3Client;
//
//    @Value("${aws.s3.bucket-name}")
//    private String bucketName;
//
//    @Value("${aws.region}")
//    private String region;
//
//    public FileStorageService1(
//            @Value("${aws.access-key-id}") String accessKey,
//            @Value("${aws.secret-access-key}") String secretKey,
//            @Value("${aws.region}") String region
//    ) {
//        this.s3Client = S3Client.builder()
//                .region(Region.of(region))
//                .credentialsProvider(
//                        StaticCredentialsProvider.create(
//                                AwsBasicCredentials.create(accessKey, secretKey)
//                        )
//                )
//                .build();
//        this.region = region;
//    }
//
//    public String uploadFile(MultipartFile file) throws IOException {
//        if (file == null || file.isEmpty()) {
//            throw new IOException("File is empty");
//        }
//
//        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//
//        try {
//            // Build PutObjectRequest WITHOUT ACL
//            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                    .bucket(bucketName)
//                    .key(fileName)
//                    .build();
//
//            // Upload file
//            s3Client.putObject(
//                    putObjectRequest,
//                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
//            );
//
//            // Return public URL
//            return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new IOException("Failed to upload file to S3: " + e.getMessage());
//        }
//    }
}
