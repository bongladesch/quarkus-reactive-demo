package com.bongladesch.adapter.minio.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.jboss.logging.Logger;

public class ProgressInputStream extends InputStream {

  private static final Logger LOG = Logger.getLogger(ProgressInputStream.class);

  private final InputStream in;
  private final long length;
  private int sumRead;
  private final List<Listener> listeners;
  private double percent;

  public ProgressInputStream(InputStream inputStream, long length) {
    this.in = inputStream;
    this.listeners = new ArrayList<>();
    this.sumRead = 0;
    this.length = length;
  }

  @Override
  public int read(byte[] b) throws IOException {
    int readCount = in.read(b);
    evaluatePercent(readCount);
    return readCount;
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    int readCount = in.read(b, off, len);
    evaluatePercent(readCount);
    return readCount;
  }

  @Override
  public long skip(long n) throws IOException {
    long skip = in.skip(n);
    evaluatePercent(skip);
    return skip;
  }

  @Override
  public int read() throws IOException {
    int read = in.read();
    if (read != -1) {
      evaluatePercent(1);
    }
    return read;
  }

  public ProgressInputStream withListener(Listener listener) {
    this.listeners.add(listener);
    return this;
  }

  private void evaluatePercent(long readCount) {
    if (readCount != -1) {
      sumRead += readCount;
      percent = sumRead * 1.0 / length;
      LOG.info(percent);
    }
    notifyListener();
  }

  private void notifyListener() {
    for (Listener listener : listeners) {
      listener.progress(percent);
    }
  }
}
