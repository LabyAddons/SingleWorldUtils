package de.jardateien.singleworldutils.v1_12_2.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.jardateien.singleworldutils.SingleWorldUtils;
import de.jardateien.singleworldutils.SingleWorldUtilsConfiguration;
import net.minecraft.client.Minecraft;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public abstract class MixinScreenPaused {

  @Shadow
  public abstract boolean isSingleplayer();

  @ModifyExpressionValue(
      method = "runTick",
      at = @At(
          value = "FIELD",
          target = "Lnet/minecraft/client/Minecraft;isGamePaused:Z",
          opcode = Opcodes.GETFIELD)
  )
  private boolean singleplayer$disablePause(boolean original) {
    SingleWorldUtilsConfiguration configuration = SingleWorldUtils.instance.configuration();
    if(original && configuration.enabled().get() && configuration.unpaused().get() && this.isSingleplayer()) {
      return false;
    }

    return original;
  }

}
