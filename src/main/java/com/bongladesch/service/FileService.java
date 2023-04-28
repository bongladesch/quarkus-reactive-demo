package com.bongladesch.service;

import com.bongladesch.entity.FileMetaData;
import com.bongladesch.service.exceptions.DataAccessException;
import com.bongladesch.service.exceptions.DataDuplicationException;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

@WithTransaction
@ApplicationScoped
public class FileService {

  @Inject
  StorageService storageService;

  public Uni<FileMetaData> uploadFile(FileDataDTO fileDataDTO) {
    String id = UUID.randomUUID().toString();
    storageService.uploadFile(id, fileDataDTO.file(), fileDataDTO.mimeType());
    FileMetaData file = new FileMetaData();
    file.id = id;
    file.name = fileDataDTO.name();
    file.mimeType = fileDataDTO.mimeType();
    return file.persistFile().onFailure()
        .transform(throwable -> new DataDuplicationException(throwable.getMessage()));
  }

  public Multi<String> uploadFileWithProgress(FileDataDTO fileDataDTO) {
    String id = UUID.randomUUID().toString();
    DecimalFormat df = new DecimalFormat("#%");
    return storageService.uploadFileWithProgress(id, fileDataDTO.file(), fileDataDTO.mimeType())
        .onItem().transform(df::format);
  }

  public Uni<FileStreamDataDTO> downloadFile(String objectId) {
    return getFileById(objectId).onItem().transform(
        item -> new FileStreamDataDTO(item.name, item.mimeType,
            storageService.downloadFile(item.id)));
  }

  public Uni<FileMetaData> getFileById(String id) {
    return FileMetaData.findFileById(id).onFailure(NoResultException.class).transform(
        throwable -> new DataAccessException("Cannot find file with id %s".formatted(id)));
  }

  public Uni<List<FileMetaData>> listFilesByMimeType(String mimeType) {
    return FileMetaData.listByMimeType(mimeType);
  }

  public Uni<List<FileMetaData>> listAllFiles() {
    return FileMetaData.listAllFiles();
  }
}
