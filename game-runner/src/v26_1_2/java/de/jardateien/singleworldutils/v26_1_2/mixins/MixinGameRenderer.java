package de.jardateien.singleworldutils.v26_1_2.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.jardateien.singleworldutils.SingleWorldUtils;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.server.IntegratedServer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.Optional;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

  @Shadow
  protected abstract void takeAutoScreenshot(Path $$0);

  @Unique
  private boolean updateWorldIcon$screenshot;

  @ModifyExpressionValue(
      method = "tryTakeScreenshotIfNeeded",
      at = @At(value = "FIELD",
          target = "Lnet/minecraft/client/renderer/GameRenderer;hasWorldScreenshot:Z",
          opcode = Opcodes.GETFIELD)
  )
  private boolean hasWorldScreenshot(boolean original) {
    if(this.singleWorldUtils$updateWorldIcon()) {
      return this.updateWorldIcon$screenshot;
    }

    return original;
  }

  @WrapOperation(
      method = "tryTakeScreenshotIfNeeded",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;getWorldScreenshotFile()Ljava/util/Optional;")
  )
  private Optional<Path> tryTakeScreenshotIfNeeded(IntegratedServer server, Operation<Optional<Path>> original) {
    Optional<Path> worldScreenshotFile = original.call(server);
    if(!this.singleWorldUtils$updateWorldIcon()) {
      return worldScreenshotFile;
    }

    worldScreenshotFile.ifPresent(this::takeAutoScreenshot);
    return Optional.empty();
  }

  @Inject(
      method = "takeAutoScreenshot",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Screenshot;takeScreenshot(Lcom/mojang/blaze3d/pipeline/RenderTarget;Ljava/util/function/Consumer;)V")
  )
  private void takeAutoScreenshot(Path $$0, CallbackInfo ci) {
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
    return SingleWorldUtils.isWorldIconEnabled();
  }

}
