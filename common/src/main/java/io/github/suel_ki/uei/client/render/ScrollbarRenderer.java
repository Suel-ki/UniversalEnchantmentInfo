package io.github.suel_ki.uei.client.render;

import io.github.suel_ki.uei.Uei;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class ScrollbarRenderer {
    private static final ResourceLocation SCROLLBAR_TEXTURE = new ResourceLocation(Uei.MOD_ID, "textures/gui/scrollbar.png");

    public static void renderScrollbar(GuiGraphics g, int trackX, int trackY, int trackW, int trackH, int thumbX, int thumbY, int thumbW, int thumbH) {
        g.blitNineSliced(
                SCROLLBAR_TEXTURE,
                trackX, trackY,
                trackW, trackH,
                6, 6,
                6, 6,
                14, 50,
                0, 0
        );

        g.blitNineSliced(
                SCROLLBAR_TEXTURE,
                thumbX, thumbY,
                thumbW, thumbH,
                2, 2,
                2, 1,
                12, 15,
                14, 0
        );
    }
}
