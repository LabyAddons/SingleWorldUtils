package de.jardateien.singleworldutils.v26_1_1.mixins;

import de.jardateien.singleworldutils.SingleWorldUtils;
import de.jardateien.singleworldutils.utils.WorldPlaytime;
import de.jardateien.singleworldutils.utils.WorldSize;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSelectionList.WorldListEntry.class)
public class MixinWorldSelection {

  @Shadow
  @Final
  private LevelSummary summary;

  @Shadow
  @Final
  private Minecraft minecraft;

  @Unique
  private final WorldPlaytime singleWorldUtils$playtime = new WorldPlaytime();

  @Unique
  private final WorldSize singleWorldUtils$worldSize = new WorldSize();

  @Inject(method = "extractContent", at = @At("TAIL"))
  private void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick,
      CallbackInfo ci) {
    boolean playtimeEnabled = SingleWorldUtils.isPlaytimeEnabled();
    boolean worldSizeEnabled = SingleWorldUtils.isWorldSizeEnabled();
    if(!playtimeEnabled && !worldSizeEnabled)
      return;

    WorldSelectionList.WorldListEntry entry = (WorldSelectionList.WorldListEntry) (Object) this;

    if(playtimeEnabled) {
      if(!this.singleWorldUtils$playtime.isRequested()) {
        User user = this.minecraft.getUser();
        if(user != null)
          this.singleWorldUtils$playtime.request(this.singleWorldUtils$worldDirectory(), user.getProfileId());
      }

      String playtime = this.singleWorldUtils$playtime.get();
      if(playtime != null)
        graphics.text(this.minecraft.font, playtime, entry.getContentX() + 150, entry.getContentY(), 0xFFAAAAAA);
    }

    if(worldSizeEnabled) {
      if(!this.singleWorldUtils$worldSize.isRequested())
        this.singleWorldUtils$worldSize.request(this.singleWorldUtils$worldDirectory());

      String size = this.singleWorldUtils$worldSize.get();
      if(size != null)
        graphics.text(this.minecraft.font, size, entry.getContentRight() - 2 - this.minecraft.font.width(size), entry.getContentY() + this.minecraft.font.lineHeight + 3, 0xFFAAAAAA);
    }
  }

  @Unique
  private Path singleWorldUtils$worldDirectory() {
    return this.minecraft.getLevelSource().getLevelPath(this.summary.getLevelId());
  }

}
