package de.jardateien.singleworldutils.v1_16_5.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import de.jardateien.singleworldutils.SingleWorldUtils;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

  @Unique
  private boolean updateWorldIcon$screenshot;

  @ModifyExpressionValue(
      method = "render",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;hasWorldScreenshot()Z")
  )
  private boolean tryTakeScreenshotIfNeeded(boolean original) {
    if(this.singleWorldUtils$updateWorldIcon()) {
      return this.updateWorldIcon$screenshot;
    }

    return original;
  }

  @ModifyExpressionValue(
      method = "takeAutoScreenshot",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;hasWorldScreenshot()Z")
  )
  private boolean hasWorldScreenshot(boolean original) {
    if(this.singleWorldUtils$updateWorldIcon()) {
      return this.updateWorldIcon$screenshot;
    }

    return original;
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

  @Unique
  private boolean singleWorldUtils$updateWorldIcon() {
    return SingleWorldUtils.isWorldIconEnabled();
  }

}
