package de.jardateien.singleworldutils.v1_12_2.mixins;

import de.jardateien.singleworldutils.SingleWorldUtils;
import de.jardateien.singleworldutils.SingleWorldUtilsConfiguration;
import de.jardateien.singleworldutils.utils.WorldBackupUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.nio.file.Path;
import java.nio.file.Paths;

@Mixin(MinecraftServer.class)
public class MixinIntegratedServer {
  @Shadow
  @Final
  private String worldName;

  @Inject(
      method = "stopServer",
      at = @At("TAIL")
  )
  private void singleWorldUtils$backupWorld(CallbackInfo ci) {
    Path path = this.pathSaves();
    WorldBackupUtils.createBackup(path.resolve(this.worldName), path.resolve("backups"));
  }

  @Unique
  private Path pathSaves() {
    SingleWorldUtilsConfiguration configuration = SingleWorldUtils.instance.configuration();
    String customPath = configuration.customSavesPath().get();
    if(configuration.enabled().get() && configuration.customSaves().get() && !customPath.isBlank()) {
      return Paths.get(customPath).resolve("saves");
    } else {
      return Paths.get(Minecraft.getMinecraft().gameDir.getPath()).resolve("saves");
    }
  }

}
