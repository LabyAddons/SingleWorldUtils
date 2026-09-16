package de.jardateien.singleworldutils;

import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class SingleWorldUtils extends LabyAddon<SingleWorldUtilsConfiguration> {

  public static SingleWorldUtils instance;

  @Override
  protected void enable() {
    this.registerSettingCategory();

    instance = this;

    //Laby.labyAPI().labyNetController().loadUniqueIdByNameSync("WorldIcon").ifPresent(uuid -> );
  }

  @Override
  protected Class<SingleWorldUtilsConfiguration> configurationClass() {
    return SingleWorldUtilsConfiguration.class;
  }
}
