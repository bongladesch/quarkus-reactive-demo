package com.bongladesch.service;

import java.io.InputStream;

public interface StorageService {

  void uploadFile(String objectId, InputStream fileStream, String mimeType);

  InputStream downloadFile(String objectId);
}
