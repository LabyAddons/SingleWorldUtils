package de.jardateien.singleworldutils.v1_12_2.mixins;

import de.jardateien.singleworldutils.SingleWorldUtils;
import net.minecraft.client.Minecraft;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public abstract class MixinScreenPaused {

  @Shadow
  public abstract boolean isSingleplayer();

  @Redirect(
      method = "runTick",
      at = @At(
          value = "FIELD",
          target = "Lnet/minecraft/client/Minecraft;isGamePaused:Z",
          opcode = Opcodes.GETFIELD)
  )
  private boolean singleplayer$disablePause(Minecraft instance) {
    if(this.isSingleplayer() && SingleWorldUtils.instance.configuration().unpaused().get()) {
      return false;
    }

    return instance.isGamePaused();
  }

}
