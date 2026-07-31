package io.github.suel_ki.uei.client.render;

import net.minecraft.client.gui.GuiGraphics;

public class ScissorHelper {

    public interface CloseableScissor extends AutoCloseable {
        @Override
        void close();
    }

    public static CloseableScissor scissor(GuiGraphics g, int minX, int minY, int maxX, int maxY) {
        g.enableScissor(minX, minY, maxX, maxY);

        return g::disableScissor;
    }
}
