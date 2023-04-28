package com.bongladesch.api;

import com.bongladesch.api.json.FileMetaDataJSON;
import com.bongladesch.service.FileDataDTO;
import com.bongladesch.service.FileService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

@Path("/api/files")
public class FileResource {

  @Inject
  FileService fileService;

  @POST
  @Path("/upload")
  public Uni<Response> upload(@RestForm("file") FileUpload fileUpload,
      @RestForm("metaData") @PartType(MediaType.APPLICATION_JSON) FileMetaDataJSON fileMetaDataJSON)
      throws FileNotFoundException {
    return fileService.uploadFile(
            new FileDataDTO(fileMetaDataJSON.name(), fileMetaDataJSON.mimeType(),
                new FileInputStream(fileUpload.uploadedFile().toFile()))).onItem()
        .transform(item -> Response.ok(item).status(201).build());
  }

  @GET
  @Path("/download/{id}")
  public Uni<Response> download(@PathParam("id") String id) {
    return fileService.downloadFile(id).onItem().transform(item -> Response.ok(item.fileStream())
        .header("Content-Disposition", "inline; filename=%s".formatted(item.name()))
        .header("Content-Type", item.mimeType()).build());
  }

  @GET
  @Path("{id}")
  public Uni<Response> getFileMetaData(@PathParam("id") String id) {
    return fileService.getFileById(id).onItem().transform(item -> Response.ok(item).build());
  }

  @GET
  public Uni<Response> listFilesMetaData(@QueryParam("mimeType") String mimeType) {
    if (Objects.isNull(mimeType) || mimeType.isBlank()) {
      return fileService.listAllFiles().onItem().transform(item -> Response.ok(item).build());
    }
    return fileService.listFilesByMimeType(mimeType).onItem()
        .transform(item -> Response.ok(item).build());
  }
}
