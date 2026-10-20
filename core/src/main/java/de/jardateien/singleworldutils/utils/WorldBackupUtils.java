package de.jardateien.singleworldutils.utils;

import de.jardateien.singleworldutils.SingleWorldUtils;
import de.jardateien.singleworldutils.SingleWorldUtilsConfiguration;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class WorldBackupUtils {

  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
  private static final String DEFAULT_BACKUP_FOLDER = "backups";
  private static final String SESSION_LOCK = "session.lock";
  private static final String ZIP_EXTENSION = ".zip";
  private static final String TEMP_EXTENSION = ".zip.tmp";
  private static final long SHUTDOWN_TIMEOUT_SECONDS = 60;

  private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(runnable -> {
    Thread thread = new Thread(runnable, "SingleWorldUtils Backup");
    thread.setDaemon(true);
    return thread;
  });

  static {
    Runtime.getRuntime().addShutdownHook(new Thread(WorldBackupUtils::awaitRunningBackups, "SingleWorldUtils Backup Shutdown"));
  }

  public static void backupWorld(Path worldDirectory, Path gameDirectory) {
    if (!SingleWorldUtils.isBackupEnabled())
      return;

    SingleWorldUtils addon = SingleWorldUtils.instance;
    SingleWorldUtilsConfiguration configuration = addon.configuration();

    if (worldDirectory == null || !Files.isDirectory(worldDirectory))
      return;

    Path worldPath = worldDirectory.toAbsolutePath().normalize();
    String worldName = worldPath.getFileName().toString();
    Path worldBackupFolder = resolveBackupFolder(configuration, gameDirectory).resolve(worldName);
    int maxBackups = configuration.maxBackups().get();

    Runnable backup = () -> {
      try {
        Files.createDirectories(worldBackupFolder);
        deleteTempFiles(worldBackupFolder);

        String fileName = LocalDateTime.now().format(DATE_FORMAT);
        Path tempFile = worldBackupFolder.resolve(fileName + TEMP_EXTENSION);
        Path backupFile = worldBackupFolder.resolve(fileName + ZIP_EXTENSION);

        zipDirectory(worldPath, worldName, tempFile);
        moveFile(tempFile, backupFile);
        addon.logger().info("Backup created: " + backupFile);

        deleteOldBackups(worldBackupFolder, maxBackups);
      } catch (Exception exception) {
        addon.logger().error("Failed to create world backup!", exception);
      }
    };

    try {
      EXECUTOR.execute(backup);
    } catch (RejectedExecutionException exception) {
      backup.run();
    }
  }

  private static Path resolveBackupFolder(SingleWorldUtilsConfiguration configuration, Path gameDirectory) {
    String customFolder = configuration.backupFolder().get();
    if (configuration.customBackupFolder().get() && customFolder != null && !customFolder.isBlank()) {
      return gameDirectory.resolve(customFolder.trim());
    }

    return gameDirectory.resolve(DEFAULT_BACKUP_FOLDER);
  }

  private static void zipDirectory(Path source, String rootName, Path target) throws IOException {
    try (OutputStream outputStream = Files.newOutputStream(target);
        ZipOutputStream zip = new ZipOutputStream(outputStream)) {
      Files.walkFileTree(source, new SimpleFileVisitor<>() {
        @Override
        public @NotNull FileVisitResult preVisitDirectory(@NotNull Path directory, @NotNull BasicFileAttributes attributes) throws IOException {
          zip.putNextEntry(new ZipEntry(entryName(source, rootName, directory) + "/"));
          zip.closeEntry();
          return FileVisitResult.CONTINUE;
        }

        @Override
        public @NotNull FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attributes) throws IOException {
          if (SESSION_LOCK.equals(file.getFileName().toString()))
            return FileVisitResult.CONTINUE;

          ZipEntry entry = new ZipEntry(entryName(source, rootName, file));
          entry.setLastModifiedTime(attributes.lastModifiedTime());
          zip.putNextEntry(entry);
          Files.copy(file, zip);
          zip.closeEntry();
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException exception) {
      Files.deleteIfExists(target);
      throw exception;
    }
  }

  private static String entryName(Path source, String rootName, Path path) {
    String relative = source.relativize(path).toString().replace('\\', '/');
    return relative.isEmpty() ? rootName : rootName + "/" + relative;
  }

  private static void moveFile(Path source, Path target) throws IOException {
    try {
      Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
    } catch (AtomicMoveNotSupportedException exception) {
      Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  private static void deleteTempFiles(Path folder) throws IOException {
    try (DirectoryStream<Path> files = Files.newDirectoryStream(folder, "*" + TEMP_EXTENSION)) {
      for (Path file : files) {
        Files.deleteIfExists(file);
      }
    }
  }

  private static void deleteOldBackups(Path folder, int maxBackups) throws IOException {
    if (maxBackups <= 0)
      return;

    List<Path> backups = new ArrayList<>();
    try (DirectoryStream<Path> files = Files.newDirectoryStream(folder, "*" + ZIP_EXTENSION)) {
      for (Path file : files) {
        if (Files.isRegularFile(file)) {
          backups.add(file);
        }
      }
    }

    Collections.sort(backups);
    for (int i = 0; i < backups.size() - maxBackups; i++) {
      Files.deleteIfExists(backups.get(i));
    }
  }

  private static void awaitRunningBackups() {
    EXECUTOR.shutdown();
    try {
      EXECUTOR.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    } catch (InterruptedException exception) {
      Thread.currentThread().interrupt();
    }
  }
}
