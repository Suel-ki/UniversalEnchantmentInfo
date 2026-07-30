package io.github.suel_ki.uei.client.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;

public class ScissorHelper {

    public interface CloseableScissor extends AutoCloseable {
        @Override
        void close();
    }

    public static CloseableScissor scissor(GuiGraphicsExtractor g, int minX, int minY, int maxX, int maxY) {
        g.enableScissor(minX, minY, maxX, maxY);

        return g::disableScissor;
    }
}
