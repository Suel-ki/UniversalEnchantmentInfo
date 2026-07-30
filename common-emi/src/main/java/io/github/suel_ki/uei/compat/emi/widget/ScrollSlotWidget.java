package io.github.suel_ki.uei.compat.emi.widget;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import io.github.suel_ki.uei.client.render.ScissorHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.function.Supplier;

public class ScrollSlotWidget extends SlotWidget {
    private final int baseX;
    private final int baseY;
    private final Supplier<Float> scrollAmount;
    private final Bounds scrollArea;

    private final Bounds outOfBounds = new Bounds(0, 0, 0, 0);

    public ScrollSlotWidget(EmiIngredient ingredient, int x, int y,
                            Supplier<Float> scrollAmount, Bounds scrollArea) {
        super(ingredient, x, y);
        this.baseX = x;
        this.baseY = y;
        this.scrollAmount = scrollAmount;
        this.scrollArea = scrollArea;
    }

    @Override
    public Bounds getBounds() {
        int currentY = baseY - Math.round(scrollAmount.get());
        if (currentY + 18 <= scrollArea.y() || currentY >= scrollArea.y() + scrollArea.height()) {
            return outOfBounds;
        }

        return new Bounds(baseX, currentY, 18, 18);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        Bounds currentBounds = getBounds();

        if (currentBounds == outOfBounds) {
            return;
        }

        try (var ignored = ScissorHelper.scissor(g,
                scrollArea.x(), scrollArea.y(),
                scrollArea.x() + scrollArea.width(), scrollArea.y() + scrollArea.height())) {
            super.extractRenderState(g, mouseX, mouseY, delta);
        }
    }
}
