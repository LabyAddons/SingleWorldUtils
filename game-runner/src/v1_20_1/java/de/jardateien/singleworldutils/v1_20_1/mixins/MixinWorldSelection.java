package de.jardateien.singleworldutils.v1_20_1.mixins;

import de.jardateien.singleworldutils.SingleWorldUtils;
import de.jardateien.singleworldutils.utils.WorldPlaytime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.GuiGraphics;
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
  private void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6,
      int $$7, boolean $$8, float $$9, CallbackInfo ci) {
    if(!SingleWorldUtils.instance.configuration().enabled().get() || !SingleWorldUtils.instance.configuration().playTime().get())
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

    $$0.drawString(this.minecraft.font, playtime, $$1 + 150, $$2, 0xFFAAAAAA);
  }

}
