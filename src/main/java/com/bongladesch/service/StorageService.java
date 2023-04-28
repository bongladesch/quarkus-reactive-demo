package com.bongladesch.service;

import io.smallrye.mutiny.Multi;
import java.io.File;
import java.io.InputStream;

public interface StorageService {

  void uploadFile(String objectId, File file, String mimeType);

  Multi<Double> uploadFileWithProgress(String objectId, File file, String mimeType);

  InputStream downloadFile(String objectId);
}
