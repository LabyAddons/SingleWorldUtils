package de.jardateien.worldicon;

import net.labymod.api.Laby;
import net.labymod.api.addon.AddonConfig;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget.ButtonSetting;
import net.labymod.api.client.gui.screen.widget.widgets.input.SwitchWidget.SwitchSetting;
import net.labymod.api.configuration.loader.annotation.ConfigName;
import net.labymod.api.configuration.loader.annotation.SpriteSlot;
import net.labymod.api.configuration.loader.annotation.SpriteTexture;
import net.labymod.api.configuration.loader.property.ConfigProperty;
import net.labymod.api.configuration.settings.annotation.SettingSection;
import net.labymod.api.util.MethodOrder;

@SpriteTexture("settings.png")
@ConfigName("settings")
public class WorldIconConfiguration extends AddonConfig {

  @SpriteSlot(x = 1)
  @SettingSection(value = "general")
  @SwitchSetting
  private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);

  @SpriteSlot()
  @MethodOrder(after = "enabled")
  @ButtonSetting
  public void joinDiscord() {
    Laby.references().chatExecutor().openUrl("https://discord.gg/Mf7HtkqPZZ");
  }

  @SettingSection(value = "settings")
  @SpriteSlot(x = 3)
  @SwitchSetting
  private final ConfigProperty<Boolean> worldIcon = new ConfigProperty<>(true);
  @SpriteSlot(x = 2)
  @SwitchSetting
  private final ConfigProperty<Boolean> playTime = new ConfigProperty<>(true);

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
}
