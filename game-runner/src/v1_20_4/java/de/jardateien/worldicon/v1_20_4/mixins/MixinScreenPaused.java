package de.jardateien.worldicon.v1_20_4.mixins;

import de.jardateien.worldicon.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
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
    if(Minecraft.getInstance().isSingleplayer() && WorldUtils.instance.configuration().unpaused().get()) {
      return false;
    }

    return screen.isPauseScreen();
  }

}
