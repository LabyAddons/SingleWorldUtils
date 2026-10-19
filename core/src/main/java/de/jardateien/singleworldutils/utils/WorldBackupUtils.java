package de.jardateien.singleworldutils.utils;

import de.jardateien.singleworldutils.SingleWorldUtils;
import de.jardateien.singleworldutils.SingleWorldUtilsConfiguration;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class WorldBackupUtils {

  // No ":" in the pattern, Windows doesn't allow it in file names
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
  private static final String DEFAULT_BACKUP_FOLDER = "backups";
  private static final String SESSION_LOCK = "session.lock";

  private WorldBackupUtils() {
  }

  /**
   * Creates a backup of the given world if backups are enabled in the settings.
   * Never throws, so it is safe to call while the server is stopping.
   *
   * @param worldDirectory the folder of the world that was just closed
   * @param gameDirectory  the Minecraft game directory, used for the default backup folder
   */
  public static void backupWorld(Path worldDirectory, Path gameDirectory) {
    if (!SingleWorldUtils.isBackupEnabled())
      return;

    SingleWorldUtils addon = SingleWorldUtils.instance;
    SingleWorldUtilsConfiguration configuration = addon.configuration();

    try {
      if (worldDirectory == null || !Files.isDirectory(worldDirectory))
        return;

      Path worldPath = worldDirectory.toAbsolutePath().normalize();
      Path backupPath = resolveBackupFolder(configuration, gameDirectory)
          .resolve(worldPath.getFileName().toString())
          .resolve(LocalDateTime.now().format(DATE_FORMAT));

      copyDirectory(worldPath, backupPath);
      addon.logger().info("Backup created: " + backupPath);
    } catch (Exception exception) {
      addon.logger().error("Failed to create world backup!", exception);
    }
  }

  private static Path resolveBackupFolder(SingleWorldUtilsConfiguration configuration, Path gameDirectory) {
    String customFolder = configuration.backupFolder().get();
    if (configuration.customBackupFolder().get() && customFolder != null && !customFolder.isBlank()) {
      // resolve() keeps absolute paths and makes relative ones relative to the game directory
      return gameDirectory.resolve(customFolder.trim());
    }

    return gameDirectory.resolve(DEFAULT_BACKUP_FOLDER);
  }

  private static void copyDirectory(Path source, Path target) throws IOException {
    Files.walkFileTree(source, new SimpleFileVisitor<>() {
      @Override
      public @NotNull FileVisitResult preVisitDirectory(@NotNull Path directory, @NotNull BasicFileAttributes attributes) throws IOException {
        Files.createDirectories(target.resolve(source.relativize(directory).toString()));
        return FileVisitResult.CONTINUE;
      }

      @Override
      public @NotNull FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attributes) throws IOException {
        if (SESSION_LOCK.equals(file.getFileName().toString()))
          return FileVisitResult.CONTINUE;

        Files.copy(
            file,
            target.resolve(source.relativize(file).toString()),
            StandardCopyOption.REPLACE_EXISTING,
            StandardCopyOption.COPY_ATTRIBUTES
        );
        return FileVisitResult.CONTINUE;
      }
    });
  }
}
