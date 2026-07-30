package io.github.suel_ki.uei.client.render;

import io.github.suel_ki.uei.Uei;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class ScrollbarRenderer {
    private static final Identifier SCROLLBAR_TRACK = Identifier.fromNamespaceAndPath(Uei.MOD_ID, "scrollbar_track");
    private static final Identifier SCROLLBAR_THUMB = Identifier.fromNamespaceAndPath(Uei.MOD_ID, "scrollbar_thumb");

    public static void renderScrollbar(GuiGraphicsExtractor g, int trackX, int trackY, int trackW, int trackH, int thumbX, int thumbY, int thumbW, int thumbH) {
        g.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLBAR_TRACK, trackX, trackY, trackW, trackH);

        g.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLBAR_THUMB, thumbX, thumbY, thumbW, thumbH);
    }
}
