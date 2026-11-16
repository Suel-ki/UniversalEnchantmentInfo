package io.github.suel_ki.uei.client.render;

import io.github.suel_ki.uei.Uei;
import net.minecraft.client.gui.GuiGraphicsExtractor;
//? if >=1.21.4 <1.21.8 {
//import net.minecraft.client.renderer.RenderType;
//?}
//? if >=1.21.8 {
import net.minecraft.client.renderer.RenderPipelines;
//?}
import net.minecraft.resources.Identifier;

public class ScrollbarRenderer {
//? if <1.21 {
    //private static final Identifier SCROLLBAR_TEXTURE = new Identifier(Uei.MOD_ID, "textures/gui/scrollbar.png");
//?} else {
    private static final Identifier SCROLLBAR_TRACK = Uei.id(Uei.MOD_ID, "scrollbar_track");
    private static final Identifier SCROLLBAR_THUMB = Uei.id(Uei.MOD_ID, "scrollbar_thumb");
//?}

    public static void renderScrollbar(GuiGraphicsExtractor g, int trackX, int trackY, int trackW, int trackH, int thumbX, int thumbY, int thumbW, int thumbH) {
//? if <1.21 {
        /*g.blitNineSliced(
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
*///?} elif <1.21.4 {
        /*g.blitSprite(SCROLLBAR_TRACK, trackX, trackY, trackW, trackH);

        g.blitSprite(SCROLLBAR_THUMB, thumbX, thumbY, thumbW, thumbH);
*///?} elif <1.21.8 {
        /*g.blitSprite(RenderType::guiTextured, SCROLLBAR_TRACK, trackX, trackY, trackW, trackH);

        g.blitSprite(RenderType::guiTextured, SCROLLBAR_THUMB, thumbX, thumbY, thumbW, thumbH);
*///?} else {
        g.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLBAR_TRACK, trackX, trackY, trackW, trackH);

        g.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLLBAR_THUMB, thumbX, thumbY, thumbW, thumbH);
//?}
    }
}
