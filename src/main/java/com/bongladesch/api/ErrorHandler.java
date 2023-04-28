package com.bongladesch.api;

import com.bongladesch.api.json.ErrorJSON;
import com.bongladesch.service.exceptions.DataAccessException;
import com.bongladesch.service.exceptions.DataDuplicationException;
import com.bongladesch.service.exceptions.ObjectStoreException;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class ErrorHandler {

  private static final Logger LOG = Logger.getLogger(ErrorHandler.class);

  @ServerExceptionMapper
  public Uni<RestResponse<ErrorJSON>> mapException(DataAccessException e) {
    LOG.warn(e.getMessage());
    return Uni.createFrom()
        .item(RestResponse.status(Response.Status.NOT_FOUND, new ErrorJSON(e.getMessage())));
  }

  @ServerExceptionMapper
  public Uni<RestResponse<ErrorJSON>> mapException(DataDuplicationException e) {
    LOG.warn(e.getMessage());
    return Uni.createFrom()
        .item(RestResponse.status(Response.Status.CONFLICT, new ErrorJSON(e.getMessage())));
  }

  @ServerExceptionMapper
  public Uni<RestResponse<ErrorJSON>> mapException(ObjectStoreException e) {
    LOG.warn(e.getMessage());
    return Uni.createFrom().item(RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR,
        new ErrorJSON(e.getMessage())));
  }

  @ServerExceptionMapper
  public Uni<RestResponse<ErrorJSON>> mapException(Exception e) {
    LOG.errorf("An unexpected error occurred: %s", e.getMessage());
    return Uni.createFrom().item(
        RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR, new ErrorJSON(e.getMessage())));
  }
}
