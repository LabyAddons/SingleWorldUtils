package de.jardateien.singleworldutils.v1_12_2.mixins;

import de.jardateien.singleworldutils.utils.WorldBackupUtils;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MixinIntegratedServer {

  @Shadow
  @Final
  private File anvilFile;

  @Shadow
  private String worldName;

  @Shadow
  private String folderName;

  @Inject(
      method = "stopServer",
      at = @At("TAIL")
  )
  private void singleWorldUtils$backupWorld(CallbackInfo ci) {
    if (this.anvilFile == null || this.folderName == null)
      return;

    WorldBackupUtils.backupWorld(
        new File(this.anvilFile, this.folderName).toPath(),
        Minecraft.getMinecraft().gameDir.toPath()
    );
  }

}
