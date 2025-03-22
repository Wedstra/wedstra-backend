package com.wedstra.app.wedstra.backend.config.AmazonS3Config;

import io.github.cdimascio.dotenv.Dotenv;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.regions.Region;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmazonConfig {
    Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();
    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(dotenv.get("AWS_ACCESS_KEY_ID"), dotenv.get("AWS_SECRET_ACCESS_KEY"));
        return S3Client.builder()
                .region(Region.EU_NORTH_1)
                .credentialsProvider(() -> awsBasicCredentials)
                .build();
    }
}
