package de.jardateien.singleworldutils.v1_20_6.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.jardateien.singleworldutils.SingleWorldUtils;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
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
    if(original && SingleWorldUtils.isUnpausedEnabled() && Minecraft.getInstance().isSingleplayer()) {
      return false;
    }

    return original;
  }

}
