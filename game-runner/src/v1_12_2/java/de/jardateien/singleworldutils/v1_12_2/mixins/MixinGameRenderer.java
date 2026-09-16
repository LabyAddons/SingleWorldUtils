package de.jardateien.singleworldutils.v1_12_2.mixins;

import de.jardateien.singleworldutils.SingleWorldUtils;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinGameRenderer {

  @Unique
  private boolean updateWorldIcon$screenshot = false;

  @Redirect(
      method = "updateCameraAndRender",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;isWorldIconSet()Z")
  )
  private boolean tryTakeScreenshotIfNeeded(IntegratedServer instance) {
    if(SingleWorldUtils.instance.configuration().enabled().get() && SingleWorldUtils.instance.configuration().worldIcon().get()) {
      return this.updateWorldIcon$screenshot;
    }

    return instance.isWorldIconSet();
  }

  @Redirect(
      method = "createWorldIcon",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;isWorldIconSet()Z")
  )
  private boolean hasWorldScreenshot(IntegratedServer instance) {
    if(SingleWorldUtils.instance.configuration().enabled().get() && SingleWorldUtils.instance.configuration().worldIcon().get()) {
      return this.updateWorldIcon$screenshot;
    }

    return instance.isWorldIconSet();
  }

  @Inject(
      method = "createWorldIcon",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ScreenShotHelper;createScreenshot(IILnet/minecraft/client/shader/Framebuffer;)Ljava/awt/image/BufferedImage;")
  )
  private void takeAutoScreenshot(CallbackInfo ci) {
    this.updateWorldIcon$screenshot = true;
  }

  @Inject(
      method = "resetData",
      at = @At("HEAD")
  )
  private void resetData(CallbackInfo ci) {
    this.updateWorldIcon$screenshot = false;
  }


}