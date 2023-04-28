package com.bongladesch.adapter.minio;

import com.bongladesch.adapter.minio.util.Listener;
import io.smallrye.mutiny.subscription.MultiEmitter;

public class ProgressListener implements Listener {

  MultiEmitter<? super Double> emitter;

  public ProgressListener(MultiEmitter<? super Double> emitter) {
    this.emitter = emitter;
  }

  @Override
  public void progress(double percent) {
    emitter.emit(percent);
  }
}
