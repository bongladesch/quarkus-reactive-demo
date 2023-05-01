package com.bongladesch.service;

import com.bongladesch.entity.FileMetaData;
import com.bongladesch.service.exceptions.DataAccessException;
import com.bongladesch.service.exceptions.DataDuplicationException;
import com.bongladesch.service.exceptions.ObjectStoreException;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
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
    return file.persistFile().onFailure(PersistenceException.class).transform(
        throwable -> new DataDuplicationException(
            "File with name %s already exists".formatted(fileDataDTO.name())));
  }

  public Uni<FileMetaData> storeFileMetaData(String uuid, FileDataDTO fileDataDTO) {
    FileMetaData file = new FileMetaData();
    file.id = uuid;
    file.name = fileDataDTO.name();
    file.mimeType = fileDataDTO.mimeType();
    return file.persistFile();
  }

  public Multi<String> uploadFileWithProgress(FileDataDTO fileDataDTO) {
    String id = UUID.randomUUID().toString();
    DecimalFormat df = new DecimalFormat("#%");
    Uni<FileMetaData> persist = storeFileMetaData(id, fileDataDTO);
    Multi<String> upload = storageService.uploadFileWithProgress(id, fileDataDTO.file(),
        fileDataDTO.mimeType()).map(df::format);
    return Multi.createBy().concatenating().streams(upload, persist.toMulti().map(item -> item.id))
        .onFailure(PersistenceException.class).recoverWithItem(() -> {
          storageService.deleteFile(id);
          return "ERROR: File with name %s already exists".formatted(fileDataDTO.name());
        })
        .onFailure(ObjectStoreException.class).retry().atMost(3);
  }

  public Uni<FileStreamDataDTO> downloadFile(String objectId) {
    return getFileById(objectId).map(item -> new FileStreamDataDTO(item.name, item.mimeType,
        storageService.downloadFile(item.id)));
  }

  public Uni<FileMetaData> getFileById(String id) {
    return FileMetaData.findFileById(id).onItem().ifNull()
        .failWith(new DataAccessException("Cannot find file with id %s".formatted(id)));
  }

  public Uni<List<FileMetaData>> listFilesByMimeType(String mimeType) {
    return FileMetaData.listByMimeType(mimeType);
  }

  public Uni<List<FileMetaData>> listAllFiles() {
    return FileMetaData.listAllFiles();
  }
}
