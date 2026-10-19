package de.jardateien.singleworldutils.v1_20_1.mixins;

import de.jardateien.singleworldutils.utils.WorldBackupUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
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
  protected LevelStorageAccess storageSource;

  @Inject(
      method = "stopServer",
      at = @At("TAIL")
  )
  private void singleWorldUtils$backupWorld(CallbackInfo ci) {
    WorldBackupUtils.backupWorld(
        this.storageSource.getLevelPath(LevelResource.ROOT),
        Minecraft.getInstance().gameDirectory.toPath()
    );
  }

}
