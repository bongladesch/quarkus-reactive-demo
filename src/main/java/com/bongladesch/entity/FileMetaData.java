package com.bongladesch.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.List;

@Entity
public class FileMetaData extends PanacheEntityBase {

  @Id
  public String id;
  public String name;
  public String mimeType;


  public Uni<FileMetaData> persistFile() {
    return persistAndFlush();
  }

  public static Uni<List<FileMetaData>> listByMimeType(String mimeType) {
    return list("mimeType", mimeType);
  }

  public static Uni<FileMetaData> findFileById(String id) {
    return findById(id);
  }

  public static Uni<List<FileMetaData>> listAllFiles() {
    return listAll();
  }
}
