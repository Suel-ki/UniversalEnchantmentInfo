package io.github.suel_ki.uei.client.scroll;

import io.github.suel_ki.uei.client.render.ScrollbarRenderer;
import io.github.suel_ki.uei.config.Config;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.util.Mth;

import java.util.function.IntSupplier;

public class ScrollContext {
    private final IntSupplier maxScrollSupplier;
    private final Rect2i bounds;

    private float scrollOffset;
    private float targetScrollOffset;

    private boolean dragging;
    private double thumbDragOffset;

    public ScrollContext(int x, int y, int width, int height, IntSupplier maxScrollSupplier) {
        this.bounds = new Rect2i(x, y, width, height);
        this.maxScrollSupplier = maxScrollSupplier;
    }

    // region Layout helpers
    public int trackX() {
        return bounds.getX() + bounds.getWidth() - EnchantmentScrollContent.TRACK_WIDTH;
    }

    public int contentRight() {
        return trackX();
    }

    public int contentWidth() {
        return bounds.getWidth() - EnchantmentScrollContent.TRACK_WIDTH;
    }

    public boolean isOnTrack(double mouseX, double mouseY) {
        int sbX = trackX();
        return mouseX >= sbX && mouseX <= sbX + EnchantmentScrollContent.TRACK_WIDTH
                && mouseY >= bounds.getY() && mouseY <= bounds.getY() + bounds.getHeight();
    }
    // endregion

    // region Scroll interpolation
    public void tick() {
        if (Math.abs(targetScrollOffset - scrollOffset) > 0.0001f) {
            scrollOffset = Mth.lerp(0.25f, scrollOffset, targetScrollOffset);
        } else {
            scrollOffset = targetScrollOffset;
        }
    }

    public float scrollAmount() {
        return maxScrollSupplier.getAsInt() * scrollOffset;
    }

    public int scrollAmountInt() {
        return Math.round(scrollAmount());
    }

    public float scrollOffset() {
        int max = maxScrollSupplier.getAsInt();
        return max <= 0 ? 0f : scrollOffset;
    }

    public void scrollTo(float value) {
        targetScrollOffset = Mth.clamp(value, 0f, 1f);
    }
    // endregion

    // region Drag state
    public void resetDrag() {
        dragging = false;
    }
    // endregion

    // region Input
    public boolean mouseScrolled(double amount) {
        int max = maxScrollSupplier.getAsInt();
        if (max > 0) {
            targetScrollOffset = Mth.clamp(
                    targetScrollOffset - (float) (amount * 20 * Config.get().scrollSpeed / max), 0f, 1f);
            return true;
        }
        return false;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        dragging = false;
        int max = maxScrollSupplier.getAsInt();
        if (button != 0 || max <= 0) {
            return false;
        }
        if (!isOnTrack(mouseX, mouseY)) {
            return false;
        }

        int y = bounds.getY();
        int trackH = bounds.getHeight();
        int thumbH = getThumbHeight(trackH, max);
        int thumbOffset = getThumbOffset(trackH, max);
        int thumbY = y + 1 + thumbOffset;

        if (mouseY >= thumbY && mouseY <= thumbY + thumbH) {
            thumbDragOffset = mouseY - thumbY;
            dragging = true;
        } else {
            int scrollableSpace = trackH - 2 - thumbH;
            if (scrollableSpace > 0) {
                scrollTo((float) ((mouseY - y - 1 - thumbH / 2.0) / scrollableSpace));
            }
        }
        return true;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        if (!dragging || button != 0) {
            return false;
        }

        int y = bounds.getY();
        int max = maxScrollSupplier.getAsInt();
        int trackH = bounds.getHeight();
        int thumbH = getThumbHeight(trackH, max);
        int scrollableSpace = trackH - 2 - thumbH;
        if (scrollableSpace > 0) {
            scrollTo((float) ((mouseY - y - 1 - thumbDragOffset) / scrollableSpace));
        }
        return true;
    }
    // endregion

    // region Rendering
    public void drawScrollbar(GuiGraphics g) {
        int max = maxScrollSupplier.getAsInt();
        if (max <= 0) {
            return;
        }

        int y = bounds.getY();
        int sbX = trackX();
        int trackH = bounds.getHeight();
        int thumbH = getThumbHeight(trackH, max);
        int thumbOffset = getThumbOffset(trackH, max);

        ScrollbarRenderer.renderScrollbar(
                g,
                sbX, y, EnchantmentScrollContent.TRACK_WIDTH, trackH,
                sbX + 1, y + 1 + thumbOffset,
                EnchantmentScrollContent.TRACK_WIDTH - 2, thumbH
        );
    }
    // endregion

    // region Thumb geometry
    private int getThumbHeight(int trackHeight, float maxScroll) {
        if (maxScroll <= 0) {
            return trackHeight - 2;
        }
        int totalSpace = trackHeight - 2;
        float ratio = (float) trackHeight / (trackHeight + maxScroll);
        return Math.max(Math.round(totalSpace * ratio), 8);
    }

    private int getThumbOffset(int trackHeight, float maxScroll) {
        int totalSpace = trackHeight - 2;
        int thumbH = getThumbHeight(trackHeight, maxScroll);
        return Math.round((totalSpace - thumbH) * scrollOffset);
    }
    // endregion
}
