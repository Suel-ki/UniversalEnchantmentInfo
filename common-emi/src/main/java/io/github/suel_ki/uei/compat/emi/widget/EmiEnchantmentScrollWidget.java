package io.github.suel_ki.uei.compat.emi.widget;

import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import io.github.suel_ki.uei.client.render.EnchantmentUIRenderer;
import io.github.suel_ki.uei.client.render.ScissorHelper;
import io.github.suel_ki.uei.client.scroll.EnchantmentScrollContent;
import io.github.suel_ki.uei.client.scroll.ScrollContext;
import io.github.suel_ki.uei.compat.emi.compat.IMouseEvents;
import io.github.suel_ki.uei.ench.EnchantmentRecipeData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Supplier;

public class EmiEnchantmentScrollWidget extends Widget implements IMouseEvents {
    private final EnchantmentRecipeData recipe;
    private final Bounds bounds;
    private final int applicableSlotCount;
    private final int applicableSlotsPerRow;
    private final int exclusiveSlotsPerRow;
    private final List<FormattedCharSequence> descLines;

    private final ScrollContext scrollContext;

    public EmiEnchantmentScrollWidget(EnchantmentRecipeData recipe, int x, int y, int width, int height,
                                      int applicableSlotCount, int applicableSlotsPerRow) {
        this.recipe = recipe;
        this.bounds = new Bounds(x, y, width, height);
        this.applicableSlotCount = applicableSlotCount;
        this.applicableSlotsPerRow = applicableSlotsPerRow;
        this.exclusiveSlotsPerRow = EnchantmentScrollContent.exclusiveSlotsPerRow(width, EnchantmentScrollContent.TRACK_WIDTH);
        this.descLines = recipe.descriptionLines(Minecraft.getInstance().font);
        this.scrollContext = new ScrollContext(x, y, width, height, this::maxScroll);
    }

    @Override
    public Bounds getBounds() {
        return bounds;
    }

    private int applicableRows() {
        return EnchantmentScrollContent.calculateRows(applicableSlotCount, applicableSlotsPerRow);
    }

    private int maxScroll() {
        return EnchantmentScrollContent.maxScroll(
                recipe, applicableRows(), recipe.exclusiveStacks().size(), exclusiveSlotsPerRow, bounds.height());
    }

    private float getScrollAmount() {
        return scrollContext.scrollAmount();
    }

    public Supplier<Float> scrollAmountSupplier() {
        return this::getScrollAmount;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        scrollContext.tick();

        float scrollAmount = getScrollAmount();
        Font font = Minecraft.getInstance().font;
        int pad = EnchantmentUIRenderer.PADDING;

        int left = bounds.x();
        int top = bounds.y();

        try (var ignored = ScissorHelper.scissor(g, bounds.x(), bounds.y(), bounds.x() + bounds.width(), bounds.y() + bounds.height())) {

            g.pose().pushPose();
            g.pose().translate(0, -Math.round(scrollAmount), 0);

            EnchantmentScrollContent.drawDescription(g, font, descLines, left, top, pad);

            int contentRight = scrollContext.contentRight();
            int aiy = EnchantmentScrollContent.drawInfoLines(g, font, recipe, descLines, left, top, pad,
                    contentRight - pad, EnchantmentScrollContent.UNIVERSAL_SCISSOR);

            // exclusive header
            int chy = EnchantmentScrollContent.exclusiveHeaderStartY(aiy, applicableRows());
            List<ItemStack> exclusiveBooks = recipe.exclusiveStacks();
            if (!exclusiveBooks.isEmpty()) {
                EnchantmentScrollContent.drawExclusiveHeader(g, font,
                        left + pad, chy, contentRight,
                        EnchantmentScrollContent.EXCLUSIVE_HEADER, exclusiveBooks.size(), EnchantmentScrollContent.UNIVERSAL_SCISSOR);
            } else {
                EnchantmentScrollContent.renderScrollingString(g, font,
                        EnchantmentScrollContent.NO_EXCLUSIVES,
                        left + pad, chy, contentRight, chy + font.lineHeight, -1, EnchantmentScrollContent.UNIVERSAL_SCISSOR);
            }

            g.pose().popPose();
        }

        scrollContext.drawScrollbar(g);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        return scrollContext.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean onMouseScrolled(double mouseX, double mouseY, double amount) {
        if (bounds.contains((int) mouseX, (int) mouseY)) {
            return scrollContext.mouseScrolled(amount);
        }
        return false;
    }

    @Override
    public boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return scrollContext.mouseDragged(mouseX, mouseY, button);
    }
}
