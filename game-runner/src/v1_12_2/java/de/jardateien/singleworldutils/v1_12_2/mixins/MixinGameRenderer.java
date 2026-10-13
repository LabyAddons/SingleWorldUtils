package de.jardateien.singleworldutils.v1_12_2.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.jardateien.singleworldutils.SingleWorldUtils;
import de.jardateien.singleworldutils.SingleWorldUtilsConfiguration;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinGameRenderer {

  @Unique
  private boolean updateWorldIcon$screenshot = false;

  @ModifyExpressionValue(
      method = "updateCameraAndRender",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;isWorldIconSet()Z")
  )
  private boolean tryTakeScreenshotIfNeeded(boolean original) {
    if(this.singleWorldUtils$updateWorldIcon()) {
      return this.updateWorldIcon$screenshot;
    }

    return original;
  }

  @ModifyExpressionValue(
      method = "createWorldIcon",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;isWorldIconSet()Z")
  )
  private boolean hasWorldScreenshot(boolean original) {
    if(this.singleWorldUtils$updateWorldIcon()) {
      return this.updateWorldIcon$screenshot;
    }

    return original;
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

  @Unique
  private boolean singleWorldUtils$updateWorldIcon() {
    SingleWorldUtilsConfiguration configuration = SingleWorldUtils.instance.configuration();
    return configuration.enabled().get() && configuration.worldIcon().get();
  }

}
