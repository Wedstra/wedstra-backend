package com.wedstra.app.wedstra.backend.config.AmazonS3Config.bucket;

public enum BucketName {
    PROFILE_IMAGE("wedstra25");

    private final String bucketName;

    BucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}
