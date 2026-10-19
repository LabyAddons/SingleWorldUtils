package de.jardateien.singleworldutils;

import net.labymod.api.addon.LabyAddon;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class SingleWorldUtils extends LabyAddon<SingleWorldUtilsConfiguration> {

  public static SingleWorldUtils instance;

  @Override
  protected void enable() {
    instance = this;

    this.registerSettingCategory();
  }

  @Override
  protected Class<SingleWorldUtilsConfiguration> configurationClass() {
    return SingleWorldUtilsConfiguration.class;
  }

  /*
   * The mixins call these checks. They return false until the addon is enabled, so a mixin that
   * runs early (e.g. in Minecraft#runTick) never touches a missing instance.
   */

  public static boolean isWorldIconEnabled() {
    return isFeatureEnabled(instance == null ? null : instance.configuration().worldIcon());
  }

  public static boolean isPlaytimeEnabled() {
    return isFeatureEnabled(instance == null ? null : instance.configuration().playTime());
  }

  public static boolean isUnpausedEnabled() {
    return isFeatureEnabled(instance == null ? null : instance.configuration().unpaused());
  }

  public static boolean isBackupEnabled() {
    return isFeatureEnabled(instance == null ? null : instance.configuration().backup());
  }

  private static boolean isFeatureEnabled(ConfigProperty<Boolean> feature) {
    return feature != null && instance.configuration().enabled().get() && feature.get();
  }
}
