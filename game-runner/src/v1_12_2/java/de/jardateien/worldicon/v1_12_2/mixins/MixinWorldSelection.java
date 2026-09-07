package de.jardateien.worldicon.v1_12_2.mixins;

import de.jardateien.worldicon.WorldIconAddon;
import de.jardateien.worldicon.utils.TimeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListWorldSelectionEntry;
import net.minecraft.world.storage.WorldSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiListWorldSelectionEntry.class)
public class MixinWorldSelection {

  @Shadow
  @Final
  private WorldSummary worldSummary;

  @Inject(method = "drawEntry", at = @At("TAIL"))
  private void drawEntry(int lvt_1_1_, int lvt_2_1_, int lvt_3_1_, int lvt_4_1_, int lvt_5_1_, int lvt_6_1_, int lvt_7_1_, boolean lvt_8_1_, float lvt_9_1_, CallbackInfo ci) {
    if(!WorldIconAddon.instance.configuration().enabled().get() || !WorldIconAddon.instance.configuration().playTime().get())
      return;
    Minecraft.getMinecraft().fontRenderer.drawString(TimeUtils.formatPlaytime(this.worldSummary.getLastTimePlayed()), lvt_2_1_ + 150, lvt_3_1_, 0xFFAAAAAA);
  }

}
