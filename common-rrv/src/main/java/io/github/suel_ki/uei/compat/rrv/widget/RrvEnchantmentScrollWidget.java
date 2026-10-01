package io.github.suel_ki.uei.compat.rrv.widget;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.client.ReliableRecipeViewerClient;
import cc.cassian.rrv.common.overlay.ItemSlot;
import cc.cassian.rrv.common.overlay.itemlist.view.ItemViewOverlay;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import io.github.suel_ki.uei.client.render.EnchantmentUIRenderer;
import io.github.suel_ki.uei.client.render.ScissorHelper;
import io.github.suel_ki.uei.client.scroll.EnchantmentScrollContent;
import io.github.suel_ki.uei.client.scroll.ScrollContext;
import io.github.suel_ki.uei.ench.EnchantmentRecipeData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class RrvEnchantmentScrollWidget implements GuiEventListener, Renderable, NarratableEntry {

    private final EnchantmentRecipeData recipe;
    private final Font font;
    private final int applicableRows;
    private final int exclusiveRows;
    private final EnchantmentItemGrid grid;
    private final ScrollContext scrollContext;

    private final List<FormattedCharSequence> descLines;
    private EnchantmentScrollContent.LayoutMetrics layoutMetrics;

    private final int x = EnchantmentItemGrid.BODY_INSET;
    private final int y = EnchantmentUIRenderer.LOWER_START_Y + EnchantmentItemGrid.BODY_INSET;
    private final int width = EnchantmentUIRenderer.PANEL_WIDTH - 2 * EnchantmentItemGrid.BODY_INSET;
    private final int height = EnchantmentUIRenderer.PANEL_HEIGHT - EnchantmentUIRenderer.LOWER_START_Y
            - 2 * EnchantmentItemGrid.BODY_INSET;

    private int panelLeft;
    private int panelTop;
    private int lastMouseX;
    private int lastMouseY;
    private boolean focused;

    public RrvEnchantmentScrollWidget(EnchantmentRecipeData recipe, Font font, List<SlotContent> applicableSlots,
                                      List<SlotContent> exclusiveSlots, int applicableSlotsPerRow,
                                      int exclusiveSlotsPerRow, int applicableRows, int exclusiveRows) {
        this.recipe = recipe;
        this.font = font;
        this.applicableRows = applicableRows;
        this.exclusiveRows = exclusiveRows;
        this.descLines = recipe.descriptionLines(font,
                width - EnchantmentScrollContent.TRACK_WIDTH - EnchantmentUIRenderer.PADDING);
        this.grid = new EnchantmentItemGrid(applicableSlots, exclusiveSlots, applicableSlotsPerRow, exclusiveSlotsPerRow,
                0, 0, x, y, height);
        updateLayoutMetrics();
        this.scrollContext = new ScrollContext(x, y, width, height, this::maxScroll);
    }

    public void updateLayoutMetrics() {
        this.layoutMetrics = new EnchantmentScrollContent.LayoutMetrics(recipe, font, applicableRows, exclusiveRows,
                width - EnchantmentScrollContent.TRACK_WIDTH - EnchantmentUIRenderer.PADDING);
        this.grid.setStartY(gridStartY(layoutMetrics.appliesToY), gridStartY(layoutMetrics.exclusivesY));
    }

    public void setPanelOrigin(int panelLeft, int panelTop) {
        this.panelLeft = panelLeft;
        this.panelTop = panelTop;
        this.grid.setOrigin(panelLeft, panelTop);
    }

    public boolean handleMouseScrolled(double absoluteMouseX, double absoluteMouseY, double amount) {
        if (!isInPanel(toLocalX(absoluteMouseX), toLocalY(absoluteMouseY))) {
            return false;
        }

        return scrollContext.mouseScrolled(amount);
    }

    public boolean handleKeyPressed(KeyEvent event) {
        if (ReliableRecipeViewerClient.USAGE_KEYBIND.matches(event)) {
            return openHoveredItem(ActionType.INPUT);
        }

        if (ReliableRecipeViewerClient.RECIPE_KEYBIND.matches(event)) {
            return openHoveredItem(ActionType.RESULT);
        }

        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTicks) {
        this.lastMouseX = mouseX;
        this.lastMouseY = mouseY;

        scrollContext.tick();

        int scroll = scrollContext.scrollAmountInt();
        int pad = EnchantmentUIRenderer.PADDING;
        int right = scrollContext.contentRight();

        g.pose().pushMatrix();
        g.pose().translate((float) panelLeft, (float) panelTop);

        try (var ignored = ScissorHelper.scissor(g, x, y, right, y + height)) {
            g.pose().pushMatrix();
            g.pose().translate(0f, (float) -scroll);

            if (layoutMetrics.descY != -1) {
                EnchantmentScrollContent.drawDescription(g, font, descLines, x, y + layoutMetrics.descY, pad);
            }

            if (layoutMetrics.infoY != -1) {
                EnchantmentScrollContent.drawAttributes(g, font, recipe, x + pad, y + layoutMetrics.infoY,
                        right - pad, EnchantmentScrollContent.UNIVERSAL_SCISSOR);
            }

            if (layoutMetrics.appliesToY != -1) {
                int headerY = applicableHeaderY();

                EnchantmentScrollContent.renderScrollingString(g, font, EnchantmentScrollContent.APPLIES_TO,
                        x + pad, headerY, right, headerY + font.lineHeight, -1,
                        EnchantmentScrollContent.UNIVERSAL_SCISSOR);
            }

            if (layoutMetrics.exclusivesY != -1) {
                int headerY = exclusiveHeaderY();

                if (grid.exclusiveCount() == 0) {
                    EnchantmentScrollContent.renderScrollingString(g, font, EnchantmentScrollContent.NO_EXCLUSIVES,
                            x + pad, headerY, right, headerY + font.lineHeight, -1,
                            EnchantmentScrollContent.UNIVERSAL_SCISSOR);
                } else {
                    EnchantmentScrollContent.drawExclusiveHeader(g, font, x + pad, headerY, right,
                            EnchantmentScrollContent.EXCLUSIVE_HEADER, grid.exclusiveCount(),
                            EnchantmentScrollContent.UNIVERSAL_SCISSOR);
                }
            }

            g.pose().popMatrix();

            grid.render(g, mouseX, mouseY, scroll);
        }

        scrollContext.drawScrollbar(g);

        g.pose().popMatrix();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return isInPanel(toLocalX(mouseX), toLocalY(mouseY));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        int scroll = scrollContext.scrollAmountInt();
        SlotContent item = grid.itemAt(event.x(), event.y(), scroll);

        if (item != null && !item.current().isEmpty()) {
            new ItemSlot(item.current(), 0, 0, true).onClicked(event);
            return true;
        }

        double localX = toLocalX(event.x());
        double localY = toLocalY(event.y());

        if (!isInPanel(localX, localY)) {
            return false;
        }

        return scrollContext.mouseClicked(localX, localY, event.button());
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        return scrollContext.mouseDragged(toLocalX(event.x()), toLocalY(event.y()), event.button());
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        scrollContext.resetDrag();
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        return handleKeyPressed(event);
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
    }

    private int gridStartY(int sectionY) {
        return sectionY == -1 ? -1 : y + sectionY + font.lineHeight;
    }

    private int maxScroll() {
        return layoutMetrics.maxScroll(height);
    }

    private int applicableHeaderY() {
        return y + layoutMetrics.appliesToY;
    }

    private int exclusiveHeaderY() {
        return y + layoutMetrics.exclusivesY;
    }

    private double toLocalX(double absoluteX) {
        return absoluteX - panelLeft;
    }

    private double toLocalY(double absoluteY) {
        return absoluteY - panelTop;
    }

    private boolean isInPanel(double localX, double localY) {
        return localX >= x && localX < x + width && localY >= y && localY < y + height;
    }

    private boolean openHoveredItem(ActionType type) {
        SlotContent item = grid.itemAt(lastMouseX, lastMouseY, scrollContext.scrollAmountInt());

        if (item == null || item.current().isEmpty()) {
            return false;
        }

        ItemViewOverlay.INSTANCE.openRecipeView(item.current(), type);
        return true;
    }
}
