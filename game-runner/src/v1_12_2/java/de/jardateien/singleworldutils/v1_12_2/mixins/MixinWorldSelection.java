package de.jardateien.singleworldutils.v1_12_2.mixins;

import com.mojang.authlib.GameProfile;
import de.jardateien.singleworldutils.SingleWorldUtils;
import de.jardateien.singleworldutils.utils.WorldPlaytime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListWorldSelectionEntry;
import net.minecraft.world.storage.WorldSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiListWorldSelectionEntry.class)
public class MixinWorldSelection {

  @Shadow
  @Final
  private WorldSummary worldSummary;

  @Shadow
  @Final
  private Minecraft client;

  @Unique
  private final WorldPlaytime singleWorldUtils$playtime = new WorldPlaytime();

  @Inject(method = "drawEntry", at = @At("TAIL"))
  private void drawEntry(int lvt_1_1_, int lvt_2_1_, int lvt_3_1_, int lvt_4_1_, int lvt_5_1_, int lvt_6_1_, int lvt_7_1_, boolean lvt_8_1_, float lvt_9_1_, CallbackInfo ci) {
    if(!SingleWorldUtils.isPlaytimeEnabled())
      return;

    if(!this.singleWorldUtils$playtime.isRequested()) {
      GameProfile profile = this.client.getSession().getProfile();
      if(profile == null)
        return;

      // Same directory vanilla passes to its save loader; no SaveHandler (it would write session.lock)
      this.singleWorldUtils$playtime.request(
          this.client.gameDir.toPath().resolve("saves").resolve(this.worldSummary.getFileName()),
          profile.getId()
      );
    }

    String playtime = this.singleWorldUtils$playtime.get();
    if(playtime == null)
      return;

    this.client.fontRenderer.drawString(playtime, lvt_2_1_ + 150, lvt_3_1_, 0xFFAAAAAA);
  }

}
