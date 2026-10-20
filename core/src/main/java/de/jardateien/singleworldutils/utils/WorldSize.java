package de.jardateien.singleworldutils.utils;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class WorldSize {

  private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
    Thread thread = new Thread(runnable, "SingleWorldUtils WorldSize");
    thread.setDaemon(true);
    return thread;
  });

  private volatile long bytes = -1;
  private String formatted;
  private boolean requested;

  public boolean isRequested() {
    return this.requested;
  }

  public void request(Path worldDirectory) {
    if (this.requested)
      return;

    this.requested = true;
    EXECUTOR.execute(() -> this.bytes = SizeUtils.getDirectorySize(worldDirectory));
  }

  public String get() {
    if (this.formatted == null && this.bytes >= 0) {
      this.formatted = SizeUtils.formatSize(this.bytes);
    }

    return this.formatted;
  }
}
