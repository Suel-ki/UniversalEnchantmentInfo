package io.github.suel_ki.uei.client.render;

import io.github.suel_ki.uei.Uei;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class ScrollbarRenderer {
    private static final ResourceLocation SCROLLBAR_TRACK = ResourceLocation.fromNamespaceAndPath(Uei.MOD_ID, "scrollbar_track");
    private static final ResourceLocation SCROLLBAR_THUMB = ResourceLocation.fromNamespaceAndPath(Uei.MOD_ID, "scrollbar_thumb");

    public static void renderScrollbar(GuiGraphics g, int trackX, int trackY, int trackW, int trackH, int thumbX, int thumbY, int thumbW, int thumbH) {
        g.blitSprite(RenderType::guiTextured, SCROLLBAR_TRACK, trackX, trackY, trackW, trackH);

        g.blitSprite(RenderType::guiTextured, SCROLLBAR_THUMB, thumbX, thumbY, thumbW, thumbH);
    }
}
