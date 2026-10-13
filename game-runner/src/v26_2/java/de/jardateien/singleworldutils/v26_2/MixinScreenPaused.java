package de.jardateien.singleworldutils.v26_2;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.jardateien.singleworldutils.SingleWorldUtils;
import de.jardateien.singleworldutils.SingleWorldUtilsConfiguration;
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
          target = "Lnet/minecraft/client/gui/Gui;isPausing()Z"
      ))
  private boolean singleplayer$disablePause(boolean original) {
    SingleWorldUtilsConfiguration configuration = SingleWorldUtils.instance.configuration();
    if(original && configuration.enabled().get() && configuration.unpaused().get() && this.singleWorld$isSingleplayer()) {
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
