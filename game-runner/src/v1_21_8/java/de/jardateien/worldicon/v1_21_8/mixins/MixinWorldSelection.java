package de.jardateien.worldicon.v1_21_8.mixins;

import de.jardateien.worldicon.WorldIconAddon;
import de.jardateien.worldicon.utils.TimeUtils;
import net.minecraft.client.Minecraft;
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

  @Inject(method = "render", at = @At("TAIL"))
  private void render(GuiGraphics $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6,
      int $$7, boolean $$8, float $$9, CallbackInfo ci) {
    if(!WorldIconAddon.instance.configuration().enabled().get() || !WorldIconAddon.instance.configuration().playTime().get())
      return;

    $$0.drawString(Minecraft.getInstance().font, TimeUtils.formatPlaytime(this.summary.getLastPlayed()), $$1+ 150, $$2, 0xFFAAAAAA);
  }

}
