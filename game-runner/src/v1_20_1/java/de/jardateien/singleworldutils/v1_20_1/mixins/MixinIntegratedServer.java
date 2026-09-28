package de.jardateien.singleworldutils.v1_20_1.mixins;

import de.jardateien.singleworldutils.SingleWorldUtils;
import de.jardateien.singleworldutils.SingleWorldUtilsConfiguration;
import de.jardateien.singleworldutils.utils.WorldBackupUtils;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MixinIntegratedServer {

  @Shadow
  @Final
  protected LevelStorageAccess storageSource;

  @Inject(
      method = "stopServer",
      at = @At("TAIL")
  )
  private void singleWorldUtils$backupWorld(CallbackInfo ci) {
    Path path = this.singleWorldUtils$pathSaves();
    if (path == null)
      return;

    WorldBackupUtils.createBackup(path.resolve(this.storageSource.getLevelId()), path.resolve("backups"));
  }

  @Unique
  private Path singleWorldUtils$pathSaves() {
    SingleWorldUtilsConfiguration configuration = SingleWorldUtils.instance.configuration();

    if(configuration.enabled().get() && configuration.backup().get()) {
      String customPath = configuration.customSavesPath().get();
      if(configuration.customSaves().get() && !customPath.isBlank()) {
        return Paths.get(customPath).resolve("saves");
      } else {
        return Paths.get(Minecraft.getInstance().gameDirectory.getPath()).resolve("saves");
      }
    }

    return null;
  }

}
