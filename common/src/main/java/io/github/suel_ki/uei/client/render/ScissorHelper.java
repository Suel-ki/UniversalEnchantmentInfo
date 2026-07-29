package io.github.suel_ki.uei.client.render;

import net.minecraft.client.gui.GuiGraphics;

public class ScissorHelper {

    public interface CloseableScissor extends AutoCloseable {
        @Override
        void close();
    }

    public static CloseableScissor scissor(GuiGraphics g, int minX, int minY, int maxX, int maxY) {
        var matrix = g.pose().last().pose();
        int sx1 = Math.round(minX * matrix.m00() + matrix.m30());
        int sy1 = Math.round(minY * matrix.m11() + matrix.m31());
        int sx2 = Math.round(maxX * matrix.m00() + matrix.m30());
        int sy2 = Math.round(maxY * matrix.m11() + matrix.m31());

        g.enableScissor(sx1, sy1, sx2, sy2);

        return g::disableScissor;
    }

    public static CloseableScissor scissorScreen(GuiGraphics g, int minX, int minY, int maxX, int maxY) {
        g.enableScissor(minX, minY, maxX, maxY);

        return g::disableScissor;
    }
}
