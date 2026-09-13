package de.jardateien.worldicon.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

  public static long getPlaytime(File gameDirectory, String worldName, UUID playerUuid) {
    File statsFile = new File(
        gameDirectory,
        "saves/" + worldName + "/stats/" + playerUuid + ".json"
    );

    if (!statsFile.exists()) {
      return 0L;
    }

    try {
      String json = Files.readString(statsFile.toPath());
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

    return 0L;
  }

}
