package de.jardateien.singleworldutils.v1_16_5.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import de.jardateien.singleworldutils.SingleWorldUtils;
import de.jardateien.singleworldutils.utils.TimeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
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
  private void render(PoseStack lvt_1_1_, int lvt_2_1_, int lvt_3_1_, int lvt_4_1_, int lvt_5_1_, int lvt_6_1_, int lvt_7_1_, int lvt_8_1_, boolean lvt_9_1_, float lvt_10_1_, CallbackInfo ci) {
    if(!SingleWorldUtils.instance.configuration().enabled().get() || !SingleWorldUtils.instance.configuration().playTime().get())
      return;

    try {
      User user = this.minecraft.getUser();
      long playtime = TimeUtils.getPlaytime(this.minecraft.getLevelSource().createAccess(this.summary.getLevelId()).getLevelPath(
          LevelResource.PLAYER_STATS_DIR), user.getGameProfile().getId()
      );

      this.minecraft.font.drawShadow(lvt_1_1_, TimeUtils.formatPlaytime(playtime), lvt_2_1_ + 150, lvt_3_1_, 0xFFAAAAAA);
    } catch (IOException ignored) {}

  }

}
