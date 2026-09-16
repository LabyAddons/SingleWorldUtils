package de.jardateien.singleworldutils.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class TimeUtils {

  public static String formatPlaytime(long ticks) {
    long totalSeconds = ticks / 20;

    long days = totalSeconds / 86400;
    long hours = (totalSeconds % 86400) / 3600;
    long minutes = (totalSeconds % 3600) / 60;
    long seconds = totalSeconds % 60;

    if (days > 0) {
      return String.format("%dd %02d:%02d:%02d", days, hours, minutes, seconds);
    }

    if (hours > 0) {
      return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    return String.format("%02d:%02d", minutes, seconds);
  }

  public static long getPlaytime(Path path, UUID uuid) {
    Path stats = path.resolve("stats").resolve(uuid + ".json");
    if(Files.notExists(stats)) {
      return 0;
    }

    try {
      String json = Files.readString(stats);
      JsonObject root = JsonParser.parseString(json).getAsJsonObject();

      // 1.8 - 1.12
      if (root.has("stat.playOneMinute")) {
        return root.get("stat.playOneMinute").getAsLong();
      }

      // 1.13+
      if (root.has("stats")) {
        JsonObject custom = root
            .getAsJsonObject("stats")
            .getAsJsonObject("minecraft:custom");

        if (custom.has("minecraft:play_time")) {
          return custom.get("minecraft:play_time").getAsLong();
        }
      }

    } catch (IOException | RuntimeException ignored) {}
    return 0;
  }

}
