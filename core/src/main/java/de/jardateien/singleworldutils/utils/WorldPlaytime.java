package de.jardateien.singleworldutils.utils;

import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class WorldPlaytime {

  private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
    Thread thread = new Thread(runnable, "SingleWorldUtils Playtime");
    thread.setDaemon(true);
    return thread;
  });

  private volatile String formatted;
  private boolean requested;

  public boolean isRequested() {
    return this.requested;
  }

  public void request(Path worldDirectory, UUID uuid) {
    if (this.requested)
      return;

    this.requested = true;
    EXECUTOR.execute(() -> this.formatted = TimeUtils.formatPlaytime(TimeUtils.getPlaytime(worldDirectory, uuid)));
  }

  public String get() {
    return this.formatted;
  }
}
