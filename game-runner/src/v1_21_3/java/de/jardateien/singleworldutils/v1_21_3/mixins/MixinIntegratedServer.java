package de.jardateien.singleworldutils.v1_21_3.mixins;

import de.jardateien.singleworldutils.SingleWorldUtils;
import de.jardateien.singleworldutils.SingleWorldUtilsConfiguration;
import de.jardateien.singleworldutils.utils.WorldBackupUtils;
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
import java.nio.file.Path;
import java.nio.file.Paths;

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
    WorldBackupUtils.createBackup(path.resolve(this.storageSource.getLevelId()), path.resolve("backups"));
  }

  @Unique
  private Path singleWorldUtils$pathSaves() {
    SingleWorldUtilsConfiguration configuration = SingleWorldUtils.instance.configuration();
    String customPath = configuration.customSavesPath().get();
    if(configuration.enabled().get() && configuration.customSaves().get() && !customPath.isBlank()) {
      return Paths.get(customPath).resolve("saves");
    } else {
      return Paths.get(Minecraft.getInstance().gameDirectory.getPath()).resolve("saves");
    }
  }

}
