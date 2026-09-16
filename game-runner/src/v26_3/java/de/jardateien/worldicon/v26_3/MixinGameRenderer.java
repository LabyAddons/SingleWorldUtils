package de.jardateien.worldicon.v26_3;

import de.jardateien.worldicon.WorldUtils;
import java.nio.file.Path;
import java.util.Optional;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.server.IntegratedServer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

  @Shadow
  protected abstract void takeAutoScreenshot(Path $$0);

  @Shadow
  private boolean hasWorldScreenshot;
  @Unique
  private boolean updateWorldIcon$screenshot;

  @Redirect(
      method = "tryTakeScreenshotIfNeeded",
      at = @At(value = "FIELD",
          target = "Lnet/minecraft/client/renderer/GameRenderer;hasWorldScreenshot:Z",
          opcode = Opcodes.GETFIELD)
  )
  private boolean hasWorldScreenshot(GameRenderer instance) {
    if(WorldUtils.instance.configuration().enabled().get()&& WorldUtils.instance.configuration().worldIcon().get()) {
      return this.updateWorldIcon$screenshot;
    }

    return this.hasWorldScreenshot;
  }

  @Redirect(
      method = "tryTakeScreenshotIfNeeded",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;getWorldScreenshotFile()Ljava/util/Optional;")
  )
  private Optional<Path> tryTakeScreenshotIfNeeded(IntegratedServer server) {
    server.getWorldScreenshotFile().ifPresent(this::takeAutoScreenshot);
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


}