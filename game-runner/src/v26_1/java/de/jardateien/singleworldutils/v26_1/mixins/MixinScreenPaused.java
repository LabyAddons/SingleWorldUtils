package de.jardateien.singleworldutils.v26_1.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.jardateien.singleworldutils.SingleWorldUtils;
import de.jardateien.singleworldutils.SingleWorldUtilsConfiguration;
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
    SingleWorldUtilsConfiguration configuration = SingleWorldUtils.instance.configuration();
    if(original && configuration.enabled().get() && configuration.unpaused().get() && Minecraft.getInstance().isSingleplayer()) {
      return false;
    }

    return original;
  }

}
