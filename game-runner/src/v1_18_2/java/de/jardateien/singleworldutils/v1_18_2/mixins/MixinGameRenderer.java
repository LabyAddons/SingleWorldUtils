package de.jardateien.singleworldutils.v1_18_2.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.nio.file.Path;
import java.util.Optional;
import de.jardateien.singleworldutils.SingleWorldUtils;
import de.jardateien.singleworldutils.SingleWorldUtilsConfiguration;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.server.IntegratedServer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    // Skip vanilla's "icon already exists" check so the icon is replaced once per session
    worldScreenshotFile.ifPresent(this::takeAutoScreenshot);
    return Optional.empty();
  }

  @Inject(
      method = "takeAutoScreenshot",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Screenshot;takeScreenshot(Lcom/mojang/blaze3d/pipeline/RenderTarget;)Lcom/mojang/blaze3d/platform/NativeImage;")
  )
  private void takeAutoScreenshot(Path $$0, CallbackInfo ci) {
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
    SingleWorldUtilsConfiguration configuration = SingleWorldUtils.instance.configuration();
    return configuration.enabled().get() && configuration.worldIcon().get();
  }

}
