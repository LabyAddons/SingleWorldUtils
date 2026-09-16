package de.jardateien.worldicon.v1_18_2.mixins;

import de.jardateien.worldicon.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.server.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MixinScreenPaused {

  @Redirect(
      method = "runTick",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/client/gui/screens/Screen;isPauseScreen()Z"
      ))
  private boolean singleplayer$disablePause(Screen screen) {
    if(this.singleWorld$isSingleplayer() && WorldUtils.instance.configuration().unpaused().get()) {
      return false;
    }

    return screen.isPauseScreen();
  }

  @Unique
  private boolean singleWorld$isSingleplayer() {
    IntegratedServer singleplayerServer = Minecraft.getInstance().getSingleplayerServer();
    return singleplayerServer != null && !singleplayerServer.isPublished();
  }

}
