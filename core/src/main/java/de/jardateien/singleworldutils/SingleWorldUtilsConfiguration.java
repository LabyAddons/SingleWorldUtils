package de.jardateien.singleworldutils;

import net.labymod.api.Laby;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget.ButtonSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SliderWidget.SliderSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.TextFieldWidget.TextFieldSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.annotation.SpriteSlot;
import net.labymod.api.configuration.loader.annotation.SpriteTexture;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingRequires;
import net.labymod.api.configuration.settings.annotation.SettingSection;
import net.labymod.api.util.MethodOrder;

@SpriteTexture("settings.png")
@ConfigName("settings")
public class SingleWorldUtilsConfiguration extends AddonConfig {

  @SpriteSlot(x = 1)
  @SettingSection(value = "general")
  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @SpriteSlot()
  @MethodOrder(after = "enabled")
  @ButtonSetting
  public void joinDiscord() {
    Laby.references().chatExecutor().openUrl("https://discord.gg/NQPQGRkFYG");
  }

  @SettingSection(value = "settings")
  @SpriteSlot(x = 3)
  @SwitchSetting
  private final ConfigProperty<Boolean> worldIcon = new ConfigProperty<>(true);
  @SpriteSlot(x = 5)
  @SwitchSetting
  private final ConfigProperty<Boolean> playTime = new ConfigProperty<>(true);
  @SpriteSlot(y = 1)
  @SwitchSetting
  private final ConfigProperty<Boolean> worldSize = new ConfigProperty<>(true);
  @SpriteSlot(x = 6)
  @SwitchSetting
  private final ConfigProperty<Boolean> unpaused = new ConfigProperty<>(true);
  @SpriteSlot(x = 2)
  @SwitchSetting
  private final ConfigProperty<Boolean> backup = new ConfigProperty<>(true);

  @SettingSection(value = "backups")
  @SettingRequires(value = "backup")
  @SpriteSlot(x = 7)
  @SwitchSetting
  private final ConfigProperty<Boolean> customBackupFolder = new ConfigProperty<>(false);
  @SettingRequires(value = "customBackupFolder")
  @SpriteSlot(x = 4)
  @TextFieldSetting
  private final ConfigProperty<String> backupFolder = new ConfigProperty<>("backups");
  @SpriteSlot(x = 2)
  @SliderSetting(min = 0, max = 50)
  private final ConfigProperty<Integer> maxBackups = new ConfigProperty<>(5);

  @Override
  public ConfigProperty<Boolean> enabled() {
    return this.enabled;
  }
  public ConfigProperty<Boolean> worldIcon() {
    return this.worldIcon;
  }
  public ConfigProperty<Boolean> playTime() {
    return this.playTime;
  }
  public ConfigProperty<Boolean> worldSize() {
    return this.worldSize;
  }
  public ConfigProperty<Boolean> unpaused() {
    return this.unpaused;
  }

  public ConfigProperty<Boolean> backup() {
    return this.backup;
  }

  public ConfigProperty<Boolean> customBackupFolder() {
    return this.customBackupFolder;
  }
  public ConfigProperty<String> backupFolder() {
    return this.backupFolder;
  }
  public ConfigProperty<Integer> maxBackups() {
    return this.maxBackups;
  }
}
