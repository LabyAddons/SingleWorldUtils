package de.jardateien.worldicon.v1_16_5.mixins;

import de.jardateien.worldicon.WorldUtils;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.server.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

  @Unique
  private boolean updateWorldIcon$screenshot;

  @Redirect(
      method = "render",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;hasWorldScreenshot()Z")
  )
  private boolean tryTakeScreenshotIfNeeded(IntegratedServer server) {
    if(WorldUtils.instance.configuration().enabled().get() && WorldUtils.instance.configuration().worldIcon().get()) {
      return this.updateWorldIcon$screenshot;
    }

    return server.hasWorldScreenshot();
  }

  @Redirect(
      method = "takeAutoScreenshot",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;hasWorldScreenshot()Z")
  )
  private boolean hasWorldScreenshot(IntegratedServer server) {
    if(WorldUtils.instance.configuration().enabled().get() && WorldUtils.instance.configuration().worldIcon().get()) {
      return this.updateWorldIcon$screenshot;
    }

    return server.hasWorldScreenshot();
  }

  @Inject(
      method = "takeAutoScreenshot",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Screenshot;takeScreenshot(IILcom/mojang/blaze3d/pipeline/RenderTarget;)Lcom/mojang/blaze3d/platform/NativeImage;")
  )
  private void takeAutoScreenshot(CallbackInfo ci) {
    this.updateWorldIcon$screenshot = true;
  }

  @Inject(
      method = "resetData",
      at = @At("TAIL")
  )
  private void resetData(CallbackInfo ci) {
    this.updateWorldIcon$screenshot = false;
  }


}