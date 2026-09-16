package de.jardateien.singleworldutils.v1_21_11.mixins;

import de.jardateien.singleworldutils.SingleWorldUtils;
import de.jardateien.singleworldutils.utils.TimeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.GuiGraphics;
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
  LevelSummary summary;

  @Shadow
  @Final
  private Minecraft minecraft;

  @Inject(method = "renderContent", at = @At("TAIL"))
  private void render(GuiGraphics $$0, int $$1, int $$2, boolean $$3, float $$4, CallbackInfo ci) {
    if(!SingleWorldUtils.instance.configuration().enabled().get() || !SingleWorldUtils.instance.configuration().playTime().get())
      return;

    WorldSelectionList.WorldListEntry entry = (WorldSelectionList.WorldListEntry) (Object) this;
    if(entry == null)
      return;

    User user = this.minecraft.getUser();
    if(user == null)
      return;

    long playtime = TimeUtils.getPlaytime(
        this.minecraft.getLevelSource().getLevelPath(
            this.summary.getLevelId()
        ),user.getProfileId()
    );

    //TODO CONTENT + 210
    $$0.drawString(this.minecraft.font, TimeUtils.formatPlaytime(playtime), entry.getContentX() + 210, entry.getContentY(), 0xFFAAAAAA);
  }

}
