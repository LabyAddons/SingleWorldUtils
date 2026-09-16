package de.jardateien.worldicon;

import net.labymod.api.addon.LabyAddon;
import net.labymod.api.models.addon.annotation.AddonMain;

@AddonMain
public class WorldUtils extends LabyAddon<WorldIconConfiguration> {

  public static WorldUtils instance;

  @Override
  protected void enable() {
    this.registerSettingCategory();

    instance = this;

    //Laby.labyAPI().labyNetController().loadUniqueIdByNameSync("WorldIcon").ifPresent(uuid -> );
  }

  @Override
  protected Class<WorldIconConfiguration> configurationClass() {
    return WorldIconConfiguration.class;
  }
}
