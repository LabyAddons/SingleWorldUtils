package de.jardateien.worldicon.utils;

public class TimeUtils {

  public static String formatPlaytime(long millis) {
    long totalSeconds = millis / 1000;

    long hours = totalSeconds / 3600;
    long minutes = (totalSeconds % 3600) / 60;
    long seconds = totalSeconds % 60;

    if (hours > 0) {
      return String.format("%02d:%02d:%02d⌚", hours, minutes, seconds);
    }

    return String.format("%02d:%02d⌚", minutes, seconds);
  }

}
