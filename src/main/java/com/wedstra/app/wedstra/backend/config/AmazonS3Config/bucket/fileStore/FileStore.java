package com.wedstra.app.wedstra.backend.config.AmazonS3Config.bucket.fileStore;

import com.wedstra.app.wedstra.backend.config.AmazonS3Config.bucket.BucketName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

@Service
public class FileStore {
    private final S3Client s3;

    @Autowired
    public FileStore(S3Client s3) {
        this.s3 = s3;
    }

    public String save(String fileName ,String fileType, String vendor_id ,Optional<Map<String, String>> optionalMetadata, InputStream inputStream, String key) {
//        key = vendor_id + "/documents/" + "/"+fileType+"_" + fileName;
        try {
            // Build the PutObjectRequest with metadata if provided
            PutObjectRequest.Builder putObjectRequestBuilder = PutObjectRequest.builder()
                    .bucket(BucketName.PROFILE_IMAGE.getBucketName())
                    .key(key);
//                    .acl("public-read");

            // Add user metadata if present
            optionalMetadata.ifPresent(metadataMap -> putObjectRequestBuilder.metadata(metadataMap));

            // Upload the file
            PutObjectResponse response = s3.putObject(
                    putObjectRequestBuilder.build(),
                    RequestBody.fromInputStream(inputStream, inputStream.available())
            );
            return "https://" + BucketName.PROFILE_IMAGE.getBucketName() + ".s3.eu-north-1.amazonaws.com/" + key;

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to upload file: " + e.getMessage();
        }
    }


    public String saveRealWeddings(String fileName, Optional<Map<String, String>> optionalMetadata, InputStream inputStream, String key) {
        try {
            // Build the PutObjectRequest with metadata if provided
            PutObjectRequest.Builder putObjectRequestBuilder = PutObjectRequest.builder()
                    .bucket(BucketName.PROFILE_IMAGE.getBucketName())
                    .key(key);

            // Add user metadata if present
            optionalMetadata.ifPresent(metadataMap -> putObjectRequestBuilder.metadata(metadataMap));

            // Upload the file
            PutObjectResponse response = s3.putObject(
                    putObjectRequestBuilder.build(),
                    RequestBody.fromInputStream(inputStream, inputStream.available())
            );
            return "https://" + BucketName.PROFILE_IMAGE.getBucketName() + ".s3.eu-north-1.amazonaws.com/" + key;

        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to upload file: " + e.getMessage();
        }
    }
}
