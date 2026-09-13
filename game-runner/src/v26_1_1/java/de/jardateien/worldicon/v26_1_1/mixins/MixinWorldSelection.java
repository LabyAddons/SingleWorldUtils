package de.jardateien.worldicon.v26_1_1.mixins;

import de.jardateien.worldicon.WorldIconAddon;
import de.jardateien.worldicon.utils.TimeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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

  @Inject(method = "extractContent", at = @At("TAIL"))
  private void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered,
      float a, CallbackInfo ci) {
    if(!WorldIconAddon.instance.configuration().enabled().get() || !WorldIconAddon.instance.configuration().playTime().get())
      return;

    WorldSelectionList.WorldListEntry entry = (WorldSelectionList.WorldListEntry) (Object) this;
    if(entry == null)
      return;

    User user = this.minecraft.getUser();
    if(user == null)
      return;

    long playtime = TimeUtils.getPlaytime(this.minecraft.gameDirectory, this.summary.getLevelId(), user.getProfileId());
    graphics.text(this.minecraft.font, TimeUtils.formatPlaytime(playtime), entry.getContentX() + 150, entry.getContentY(), 0xFFAAAAAA);
  }

}
