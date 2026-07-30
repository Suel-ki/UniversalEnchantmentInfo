package io.github.suel_ki.uei.compat.rei.widget;

import io.github.suel_ki.uei.client.render.EnchantmentUIRenderer;
import io.github.suel_ki.uei.client.scroll.EnchantmentScrollContent;
import io.github.suel_ki.uei.client.scroll.ScrollContext;
import io.github.suel_ki.uei.ench.EnchantmentRecipeData;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.WidgetWithBounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class ReiEnchantmentScrollWidget extends WidgetWithBounds {
    private final EnchantmentRecipeData recipe;
    private final List<FormattedCharSequence> descLines;
    private final List<Slot> exclusiveSlots;
    private final List<Slot> applicableSlots;
    private final List<Widget> children = new ArrayList<>();
    private final int exclusiveSlotsPerRow;
    private final int applicableSlotCount;
    private final int applicableSlotsPerRow;

    private final Rectangle bounds;
    private final ScrollContext scrollContext;

    public ReiEnchantmentScrollWidget(EnchantmentRecipeData recipe, Rectangle bounds,
                                      List<Slot> exclusiveSlots, List<Slot> applicableSlots,
                                      int applicableSlotCount, int applicableSlotsPerRow) {
        this.recipe = recipe;
        this.descLines = recipe.descriptionLines(Minecraft.getInstance().font);
        this.exclusiveSlots = exclusiveSlots;
        this.applicableSlots = applicableSlots;
        this.bounds = bounds;

        this.children.addAll(applicableSlots);
        this.children.addAll(exclusiveSlots);

        this.exclusiveSlotsPerRow = EnchantmentScrollContent.exclusiveSlotsPerRow(bounds.width, 0);
        this.applicableSlotCount = applicableSlotCount;
        this.applicableSlotsPerRow = applicableSlotsPerRow;

        this.scrollContext = new ScrollContext(bounds.x, bounds.y, bounds.width, bounds.height, this::maxScroll);
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return children;
    }

    private int applicableRows() {
        return EnchantmentScrollContent.calculateRows(applicableSlotCount, applicableSlotsPerRow);
    }

    private int maxScroll() {
        return EnchantmentScrollContent.maxScroll(
                recipe, applicableRows(), exclusiveSlots.size(), exclusiveSlotsPerRow, bounds.height);
    }

    private float scrollAmount() {
        return scrollContext.scrollAmount();
    }

    private Rectangle getScissorBounds() {
        if (maxScroll() > 0) {
            return new Rectangle(bounds.x, bounds.y, bounds.width - EnchantmentScrollContent.TRACK_WIDTH, bounds.height);
        }
        return bounds;
    }

    private void updateSlotPositions(int scroll) {
        Font font = Minecraft.getInstance().font;
        int pad = EnchantmentUIRenderer.PADDING;
        Rectangle innerBounds = getScissorBounds();

        int minY = innerBounds.y;
        int maxY = innerBounds.y + innerBounds.height;
        int baseX = innerBounds.x + EnchantmentUIRenderer.PADDING + 1;
        int cy = innerBounds.y - scroll;
        int aiy = cy + EnchantmentScrollContent.applicableItemStartY(descLines);

        int aspacing = EnchantmentUIRenderer.APPLICABLE_SLOT_SPACING;
        for (int i = 0; i < applicableSlots.size(); i++) {
            Slot slot = applicableSlots.get(i);
            int sx = EnchantmentScrollContent.gridX(i, applicableSlotsPerRow, baseX, aspacing);
            int sy = EnchantmentScrollContent.gridY(i, applicableSlotsPerRow, aiy + 1, aspacing);
            if (sy + 18 > minY && sy < maxY) {
                slot.getBounds().setLocation(sx, sy);
            } else {
                slot.getBounds().setLocation(-9999, -9999);
            }
        }

        if (!exclusiveSlots.isEmpty()) {
            int chy = EnchantmentScrollContent.exclusiveHeaderStartY(aiy, applicableRows());
            int ciy = chy + font.lineHeight + 1;
            int spacing = EnchantmentUIRenderer.EXCLUSIVE_SLOT_SPACING;
            for (int i = 0; i < exclusiveSlots.size(); i++) {
                Slot slot = exclusiveSlots.get(i);
                int sx = EnchantmentScrollContent.gridX(i, exclusiveSlotsPerRow, innerBounds.x + pad + 1, spacing);
                int sy = EnchantmentScrollContent.gridY(i, exclusiveSlotsPerRow, ciy + 1, spacing);
                if (sy + 18 > innerBounds.y && sy < innerBounds.y + innerBounds.height) {
                    slot.getBounds().setLocation(sx, sy);
                } else {
                    slot.getBounds().setLocation(-9999, -9999);
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        scrollContext.tick();

        int scroll = Math.round(scrollAmount());
        Font font = Minecraft.getInstance().font;
        int pad = EnchantmentUIRenderer.PADDING;

        Rectangle innerBounds = getScissorBounds();

        updateSlotPositions(scroll);

        try (var ignored = scissor(g, innerBounds)) {
            int cy = innerBounds.y - scroll;

            EnchantmentScrollContent.drawDescription(g, font, descLines,
                    innerBounds.x, cy, pad);

            int contentRight = scrollContext.contentRight();
            int aiyY = EnchantmentScrollContent.drawInfoLines(g, font, recipe, descLines,
                    innerBounds.x, cy, pad, contentRight - pad);

            for (Slot slot : applicableSlots) {
                slot.render(g, mouseX, mouseY, delta);
            }

            int chy = EnchantmentScrollContent.exclusiveHeaderStartY(aiyY, applicableRows());
            if (!exclusiveSlots.isEmpty()) {
                EnchantmentScrollContent.drawExclusiveHeader(g, font,
                        innerBounds.x + pad, chy, contentRight,
                        EnchantmentScrollContent.EXCLUSIVE_HEADER, exclusiveSlots.size());

                for (Slot slot : exclusiveSlots) {
                    slot.render(g, mouseX, mouseY, delta);
                }
            } else {
                EnchantmentScrollContent.renderScrollingString(g, font,
                        EnchantmentScrollContent.NO_EXCLUSIVES,
                        innerBounds.x + pad, chy, contentRight, chy + font.lineHeight, -1);
            }
        }

        scrollContext.drawScrollbar(g);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (containsMouse(mouseX, mouseY)) {
            boolean handled = scrollContext.mouseScrolled(scrollY);
            if (handled) {
                updateSlotPositions(scrollContext.scrollAmountInt());
            }
            return handled;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean handled = scrollContext.mouseClicked(event.x(), event.y(), event.button());
        if (handled) {
            updateSlotPositions(scrollContext.scrollAmountInt());
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        boolean handled = scrollContext.mouseDragged(event.x(), event.y(), event.button());
        if (handled) {
            updateSlotPositions(scrollContext.scrollAmountInt());
            return true;
        }
        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        for (GuiEventListener child : this.children()) {
            if (child.keyPressed(event)) {
                return true;
            }
        }
        return super.keyPressed(event);
    }
}
