package de.jardateien.singleworldutils.v1_12_2.mixins;

import com.mojang.authlib.GameProfile;
import de.jardateien.singleworldutils.SingleWorldUtils;
import de.jardateien.singleworldutils.utils.WorldPlaytime;
import de.jardateien.singleworldutils.utils.WorldSize;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListWorldSelectionEntry;
import net.minecraft.world.storage.WorldSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiListWorldSelectionEntry.class)
public class MixinWorldSelection {

  @Shadow
  @Final
  private WorldSummary worldSummary;

  @Shadow
  @Final
  private Minecraft client;

  @Unique
  private final WorldPlaytime singleWorldUtils$playtime = new WorldPlaytime();

  @Unique
  private final WorldSize singleWorldUtils$worldSize = new WorldSize();

  @Inject(method = "drawEntry", at = @At("TAIL"))
  private void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks, CallbackInfo ci) {
    boolean playtimeEnabled = SingleWorldUtils.isPlaytimeEnabled();
    boolean worldSizeEnabled = SingleWorldUtils.isWorldSizeEnabled();
    if(!playtimeEnabled && !worldSizeEnabled)
      return;

    if(playtimeEnabled) {
      if(!this.singleWorldUtils$playtime.isRequested()) {
        GameProfile profile = this.client.getSession().getProfile();
        if(profile != null)
          this.singleWorldUtils$playtime.request(this.singleWorldUtils$worldDirectory(), profile.getId());
      }

      String playtime = this.singleWorldUtils$playtime.get();
      if(playtime != null)
        this.client.fontRenderer.drawString(playtime, x + 150, y, 0xFFAAAAAA);
    }

    if(worldSizeEnabled) {
      if(!this.singleWorldUtils$worldSize.isRequested())
        this.singleWorldUtils$worldSize.request(this.singleWorldUtils$worldDirectory());

      String size = this.singleWorldUtils$worldSize.get();
      if(size != null) {
        int width = this.client.fontRenderer.getStringWidth(size);
        this.client.fontRenderer.drawString(size, x + listWidth - 4 - width, y + this.client.fontRenderer.FONT_HEIGHT + 3, 0xFFAAAAAA);
      }
    }
  }

  @Unique
  private Path singleWorldUtils$worldDirectory() {
    return this.client.gameDir.toPath().resolve("saves").resolve(this.worldSummary.getFileName());
  }

}
