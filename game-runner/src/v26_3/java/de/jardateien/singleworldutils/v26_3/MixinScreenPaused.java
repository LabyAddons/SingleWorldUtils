package de.jardateien.singleworldutils.v26_3;

import de.jardateien.singleworldutils.SingleWorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
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
          target = "Lnet/minecraft/client/gui/Gui;isPausing()Z"
      ))
  private boolean singleplayer$disablePause(Gui instance) {
    if(this.singleWorld$isSingleplayer() && SingleWorldUtils.instance.configuration().unpaused().get()) {
      return false;
    }

    return instance.isPausing();
  }

  @Unique
  private boolean singleWorld$isSingleplayer() {
    IntegratedServer singleplayerServer = Minecraft.getInstance().getSingleplayerServer();
    return singleplayerServer != null && !singleplayerServer.isPublished();
  }

}
