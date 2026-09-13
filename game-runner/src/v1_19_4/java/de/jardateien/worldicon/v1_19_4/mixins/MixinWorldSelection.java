package de.jardateien.worldicon.v1_19_4.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import de.jardateien.worldicon.WorldIconAddon;
import de.jardateien.worldicon.utils.TimeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

  @Inject(method = "render", at = @At("TAIL"))
  private void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9, CallbackInfo ci) {
    if(!WorldIconAddon.instance.configuration().enabled().get() || !WorldIconAddon.instance.configuration().playTime().get())
      return;

    User user = this.minecraft.getUser();
    long playtime = TimeUtils.getPlaytime(this.minecraft.gameDirectory, this.summary.getLevelId(), user.getProfileId());

    this.minecraft.font.drawShadow($$0, TimeUtils.formatPlaytime(playtime), $$1 + 150, $$2, 0xFFAAAAAA);
  }

}
