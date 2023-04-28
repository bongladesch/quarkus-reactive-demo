package com.bongladesch.adapter.minio;

import com.bongladesch.service.StorageService;
import com.bongladesch.service.exceptions.ObjectStoreException;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@ApplicationScoped
public class MinioStorageService implements StorageService {

  private static final String BUCKET_NAME = "my-bucket";

  @Inject
  MinioClient minioClient;

  @Override
  public void uploadFile(String objectId, InputStream fileStream, String mimeType) {
    try {
      if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET_NAME).build())) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
      }
      minioClient.putObject(
          PutObjectArgs.builder()
              .bucket(BUCKET_NAME)
              .object(objectId)
              .contentType(mimeType)
              .stream(fileStream, -1, 50 * 1024 * 1024L)
              .build());
    } catch (MinioException | IOException | InvalidKeyException | NoSuchAlgorithmException e) {
      throw new ObjectStoreException(e.getMessage());
    }
  }

  @Override
  public InputStream downloadFile(String objectId) {
    try {
      return minioClient.getObject(
          GetObjectArgs.builder().bucket(BUCKET_NAME).object(objectId).build());
    } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
      throw new ObjectStoreException(e.getMessage());
    }
  }
}
