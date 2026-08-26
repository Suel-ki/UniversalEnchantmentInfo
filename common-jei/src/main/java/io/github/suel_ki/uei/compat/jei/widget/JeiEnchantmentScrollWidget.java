package io.github.suel_ki.uei.compat.jei.widget;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.suel_ki.uei.client.render.EnchantmentUIRenderer;
import io.github.suel_ki.uei.client.render.ScissorHelper;
import io.github.suel_ki.uei.client.scroll.EnchantmentScrollContent;
import io.github.suel_ki.uei.client.scroll.ScrollContext;
import io.github.suel_ki.uei.ench.EnchantmentRecipeData;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.inputs.IJeiInputHandler;
import mezz.jei.api.gui.inputs.IJeiUserInput;
import mezz.jei.api.gui.inputs.RecipeSlotUnderMouse;
import mezz.jei.api.gui.widgets.ISlottedRecipeWidget;
import mezz.jei.common.Internal;
import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.common.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class JeiEnchantmentScrollWidget implements ISlottedRecipeWidget, IJeiInputHandler {
    private final EnchantmentRecipeData recipe;
    private final List<FormattedCharSequence> descLines;
    private final List<List<ItemStack>> exclusiveBooks;
    private final List<IRecipeSlotDrawable> exclusiveSlots;
    private final List<IRecipeSlotDrawable> applicableSlots;
    private final int exclusiveSlotsPerRow;
    private final int applicableSlotCount;
    private final int applicableSlotsPerRow;

    private final ImmutableRect2i area;
    private final ImmutableRect2i contentsArea;

    private final ScrollContext scrollContext;

    public JeiEnchantmentScrollWidget(EnchantmentRecipeData recipe, int x, int y, int width, int height,
                                      List<IRecipeSlotDrawable> exclusiveSlots,
                                      List<IRecipeSlotDrawable> applicableSlots,
                                      int applicableSlotCount, int applicableSlotsPerRow) {
        this.recipe = recipe;
        this.descLines = recipe.descriptionLines(Minecraft.getInstance().font);
        this.exclusiveBooks = recipe.exclusiveStacks();
        this.exclusiveSlots = exclusiveSlots;
        this.applicableSlots = applicableSlots;
        this.area = new ImmutableRect2i(x, y, width, height);

        this.contentsArea = new ImmutableRect2i(0, 0, width - EnchantmentScrollContent.TRACK_WIDTH, height);

        int spacing = EnchantmentUIRenderer.EXCLUSIVE_SLOT_SPACING;
        int areaW = contentsArea.width() - EnchantmentUIRenderer.PADDING;
        this.exclusiveSlotsPerRow = Math.max(areaW / spacing, 1);
        this.applicableSlotCount = applicableSlotCount;
        this.applicableSlotsPerRow = applicableSlotsPerRow;

        this.scrollContext = new ScrollContext(0, 0, width, height, this::maxScroll);
    }

    private int applicableRows() {
        return EnchantmentScrollContent.calculateRows(applicableSlotCount, applicableSlotsPerRow);
    }

    private int maxScroll() {
        return EnchantmentScrollContent.maxScroll(
                recipe, applicableRows(), exclusiveSlots.size(), exclusiveSlotsPerRow, contentsArea.height());
    }

    @Override
    public void drawWidget(GuiGraphics g, double mouseX, double mouseY) {
        scrollContext.tick();

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;
        int pad = EnchantmentUIRenderer.PADDING;
        int contentWidth = scrollContext.contentWidth();

        float totalScroll = scrollContext.scrollAmount();
        double adjY = mouseY + totalScroll;

        PoseStack poseStack = g.pose();
        ScreenRectangle scissorBounds = MathUtil.transform(contentsArea, poseStack.last().pose());
        try (var ignored = ScissorHelper.scissorScreen(g, scissorBounds.left(), scissorBounds.top(), scissorBounds.right(), scissorBounds.bottom())) {

            poseStack.pushPose();
            poseStack.translate(0, -scrollContext.scrollAmount(), 0);

            EnchantmentScrollContent.drawDescription(g, font, descLines, 0, 0, pad);

            int aiy = EnchantmentScrollContent.drawInfoLines(g, font, recipe, descLines, 0, 0, pad,
                    contentWidth - pad, EnchantmentScrollContent.UNIVERSAL_SCISSOR);

            // applicable items
            int aspacing = EnchantmentUIRenderer.APPLICABLE_SLOT_SPACING;
            for (int i = 0; i < applicableSlots.size(); i++) {
                int x = EnchantmentScrollContent.gridX(i, applicableSlotsPerRow, pad + 1, aspacing);
                int y = EnchantmentScrollContent.gridY(i, applicableSlotsPerRow, aiy + 1, aspacing);
                IRecipeSlotDrawable slot = applicableSlots.get(i);
                slot.setPosition(x, y);
                slot.draw(g, slot.isMouseOver(mouseX, adjY));
            }

            // exclusive items
            int chy = EnchantmentScrollContent.exclusiveHeaderStartY(aiy, applicableRows());

            if (!exclusiveBooks.isEmpty()) {
                EnchantmentScrollContent.drawExclusiveHeader(g, font, pad, chy,
                        contentWidth, EnchantmentScrollContent.EXCLUSIVE_HEADER, exclusiveBooks.size(), EnchantmentScrollContent.UNIVERSAL_SCISSOR);

                int ciy = chy + font.lineHeight + 1;
                int spacing = EnchantmentUIRenderer.EXCLUSIVE_SLOT_SPACING;
                for (int i = 0; i < exclusiveSlots.size(); i++) {
                    int x = EnchantmentScrollContent.gridX(i, exclusiveSlotsPerRow, pad + 1, spacing);
                    int y = EnchantmentScrollContent.gridY(i, exclusiveSlotsPerRow, ciy + 1, spacing);
                    IRecipeSlotDrawable slot = exclusiveSlots.get(i);
                    slot.setPosition(x, y);
                    slot.draw(g, slot.isMouseOver(mouseX, adjY));
                }
            } else {
                EnchantmentScrollContent.renderScrollingString(g, font,
                        EnchantmentScrollContent.NO_EXCLUSIVES,
                        pad, chy, contentWidth, chy + font.lineHeight, -1, EnchantmentScrollContent.UNIVERSAL_SCISSOR);
            }

            poseStack.popPose();
        }

        scrollContext.drawScrollbar(g);
    }

    @Override
    public Optional<RecipeSlotUnderMouse> getSlotUnderMouse(double mouseX, double mouseY) {
        float totalScroll = scrollContext.scrollAmount();
        double adjY = mouseY + totalScroll;

        int yOffset = area.y() - Math.round(totalScroll);

        for (IRecipeSlotDrawable slot : exclusiveSlots) {
            if (slot.isMouseOver(mouseX, adjY)) {
                return Optional.of(new RecipeSlotUnderMouse(slot,
                        area.x(), yOffset));
            }
        }

        for (IRecipeSlotDrawable slot : applicableSlots) {
            if (slot.isMouseOver(mouseX, adjY)) {
                return Optional.of(new RecipeSlotUnderMouse(slot,
                        area.x(), yOffset));
            }
        }

        return Optional.empty();
    }

    @Override
    public boolean handleInput(double mouseX, double mouseY, IJeiUserInput userInput) {
        if (!userInput.is(Internal.getKeyMappings().getLeftClick())) {
            return false;
        }
        scrollContext.resetDrag();
        if (!scrollContext.isOnTrack(mouseX, mouseY) || maxScroll() == 0) {
            return false;
        }
        if (userInput.isSimulate()) {
            return scrollContext.mouseClicked(mouseX, mouseY, 0);
        }
        return false;
    }

    @Override
    public boolean handleMouseScrolled(double mouseX, double mouseY, double scrollDeltaX, double scrollDeltaY) {
        return scrollContext.mouseScrolled(scrollDeltaY);
    }

    @Override
    public boolean handleMouseDragged(double mouseX, double mouseY, InputConstants.Key key, double dx, double dy) {
        if (key.getValue() == InputConstants.MOUSE_BUTTON_LEFT) {
            return scrollContext.mouseDragged(mouseX, mouseY, 0);
        }
        return false;
    }

    @Override
    public @NotNull ScreenPosition getPosition() {
        return area.getScreenPosition();
    }

    @Override
    public @NotNull ScreenRectangle getArea() {
        return area.toScreenRectangle();
    }
}
