package de.jardateien.singleworldutils.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public final class WorldBackupUtils {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public static void createBackup(Path worldDirectory, Path backupDirectory) {
        if (worldDirectory == null || !Files.exists(worldDirectory)) {
            return;
        }

        try {
            Files.createDirectories(backupDirectory);

            String worldName = worldDirectory.getFileName().toString();
            String timestamp = LocalDateTime.now().format(DATE_FORMAT);

            Path backupWorldDirectory = backupDirectory
                    .resolve(worldName)
                    .resolve(timestamp);

            Files.createDirectories(backupWorldDirectory);

            copyDirectory(worldDirectory, backupWorldDirectory);

            System.out.println(
                    "[SingleWorldUtils] Backup created: "
                            + backupWorldDirectory
            );

        } catch (IOException exception) {
            System.err.println(
                    "[SingleWorldUtils] Failed to create world backup!"
            );

            exception.printStackTrace();
        }
    }

    private static void copyDirectory(Path source, Path target)
            throws IOException {

        try (Stream<Path> paths = Files.walk(source)) {
            paths.forEach(sourcePath -> {

                try {
                    Path relativePath = source.relativize(sourcePath);
                    Path targetPath = target.resolve(relativePath);

                    if (Files.isDirectory(sourcePath)) {
                        Files.createDirectories(targetPath);
                    } else {
                        Files.copy(
                                sourcePath,
                                targetPath,
                                StandardCopyOption.REPLACE_EXISTING,
                                StandardCopyOption.COPY_ATTRIBUTES
                        );
                    }

                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            });
        }
    }
}