package de.jardateien.singleworldutils.v1_20_1.mixins;

import de.jardateien.singleworldutils.SingleWorldUtils;
import de.jardateien.singleworldutils.utils.TimeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.io.IOException;

@Mixin(WorldSelectionList.WorldListEntry.class)
public class MixinWorldSelection {

  @Shadow
  @Final
  private LevelSummary summary;

  @Shadow
  @Final
  private Minecraft minecraft;

  @Inject(method = "render", at = @At("TAIL"))
  private void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6,
      int $$7, boolean $$8, float $$9, CallbackInfo ci) {
    if(!SingleWorldUtils.instance.configuration().enabled().get() || !SingleWorldUtils.instance.configuration().playTime().get())
      return;

    try {
      User user = this.minecraft.getUser();
      long playtime = TimeUtils.getPlaytime(this.minecraft.getLevelSource().createAccess(this.summary.getLevelId()).getLevelPath(
          LevelResource.PLAYER_STATS_DIR), user.getGameProfile().getId()
      );

      $$0.drawString(this.minecraft.font, TimeUtils.formatPlaytime(playtime), $$1 + 150, $$2, 0xFFAAAAAA);
    } catch (IOException ignored) {}
  }

}
