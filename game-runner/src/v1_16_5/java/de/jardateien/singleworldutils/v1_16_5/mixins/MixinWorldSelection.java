package de.jardateien.singleworldutils.v1_16_5.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import de.jardateien.singleworldutils.SingleWorldUtils;
import de.jardateien.singleworldutils.utils.WorldPlaytime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
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

  @Inject(method = "render", at = @At("TAIL"))
  private void render(PoseStack lvt_1_1_, int lvt_2_1_, int lvt_3_1_, int lvt_4_1_, int lvt_5_1_, int lvt_6_1_, int lvt_7_1_, int lvt_8_1_, boolean lvt_9_1_, float lvt_10_1_, CallbackInfo ci) {
    if(!SingleWorldUtils.isPlaytimeEnabled())
      return;

    if(!this.singleWorldUtils$playtime.isRequested()) {
      User user = this.minecraft.getUser();
      if(user == null)
        return;

      this.singleWorldUtils$playtime.request(
          this.minecraft.getLevelSource().getBaseDir().resolve(this.summary.getLevelId()),
          user.getGameProfile().getId()
      );
    }

    String playtime = this.singleWorldUtils$playtime.get();
    if(playtime == null)
      return;

    this.minecraft.font.drawShadow(lvt_1_1_, playtime, lvt_2_1_ + 150, lvt_3_1_, 0xFFAAAAAA);
  }

}
