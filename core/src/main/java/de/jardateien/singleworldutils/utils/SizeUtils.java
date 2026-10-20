package de.jardateien.singleworldutils.utils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Locale;
import net.labymod.api.util.I18n;
import org.jetbrains.annotations.NotNull;

public final class SizeUtils {

  private static final String[] UNITS = {"b", "kb", "mb", "gb", "tb"};
  private static final String TRANSLATION_PREFIX = "singleworldutils.size.";

  public static String formatSize(long bytes) {
    if (bytes < 1024)
      return I18n.translate(TRANSLATION_PREFIX + UNITS[0], bytes);

    double size = bytes;
    int unit = 0;
    while (size >= 1024 && unit < UNITS.length - 1) {
      size /= 1024;
      unit++;
    }

    String number = String.format(Locale.ROOT, "%.1f", size)
        .replace(".", I18n.translate(TRANSLATION_PREFIX + "decimal"));
    return I18n.translate(TRANSLATION_PREFIX + UNITS[unit], number);
  }

  public static long getDirectorySize(Path directory) {
    if (directory == null || !Files.isDirectory(directory))
      return 0;

    long[] total = {0};
    try {
      Files.walkFileTree(directory, new SimpleFileVisitor<>() {
        @Override
        public @NotNull FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attributes) {
          total[0] += attributes.size();
          return FileVisitResult.CONTINUE;
        }

        @Override
        public @NotNull FileVisitResult visitFileFailed(@NotNull Path file, @NotNull IOException exception) {
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException | RuntimeException ignored) {}

    return total[0];
  }
}
