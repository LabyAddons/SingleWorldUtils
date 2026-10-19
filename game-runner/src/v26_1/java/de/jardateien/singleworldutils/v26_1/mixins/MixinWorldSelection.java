package de.jardateien.singleworldutils.v26_1.mixins;

import de.jardateien.singleworldutils.SingleWorldUtils;
import de.jardateien.singleworldutils.utils.WorldPlaytime;
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

  @Inject(method = "extractContent", at = @At("TAIL"))
  private void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered,
      float a, CallbackInfo ci) {
    if(!SingleWorldUtils.isPlaytimeEnabled())
      return;

    WorldSelectionList.WorldListEntry entry = (WorldSelectionList.WorldListEntry) (Object) this;
    if(entry == null)
      return;

    if(!this.singleWorldUtils$playtime.isRequested()) {
      User user = this.minecraft.getUser();
      if(user == null)
        return;

      this.singleWorldUtils$playtime.request(
          this.minecraft.getLevelSource().getLevelPath(this.summary.getLevelId()), user.getProfileId()
      );
    }

    String playtime = this.singleWorldUtils$playtime.get();
    if(playtime == null)
      return;
    graphics.text(this.minecraft.font, playtime, entry.getContentX() + 150, entry.getContentY(), 0xFFAAAAAA);
  }

}
