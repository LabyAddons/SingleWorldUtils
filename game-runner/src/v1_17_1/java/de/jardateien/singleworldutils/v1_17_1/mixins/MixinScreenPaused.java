package de.jardateien.singleworldutils.v1_17_1.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.jardateien.singleworldutils.SingleWorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public class MixinScreenPaused {

  @ModifyExpressionValue(
      method = "runTick",
      at = @At(
          value = "INVOKE",
          target = "Lnet/minecraft/client/gui/screens/Screen;isPauseScreen()Z"
      ))
  private boolean singleplayer$disablePause(boolean original) {
    if(original && SingleWorldUtils.isUnpausedEnabled() && this.singleWorld$isSingleplayer()) {
      return false;
    }

    return original;
  }

  @Unique
  private boolean singleWorld$isSingleplayer() {
    IntegratedServer singleplayerServer = Minecraft.getInstance().getSingleplayerServer();
    return singleplayerServer != null && !singleplayerServer.isPublished();
  }

}
