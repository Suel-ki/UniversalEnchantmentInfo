package io.github.suel_ki.uei.compat.rrv.widget;

import cc.cassian.rrv.common.overlay.ItemSlot;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import io.github.suel_ki.uei.client.render.EnchantmentUIRenderer;
import io.github.suel_ki.uei.client.scroll.EnchantmentScrollContent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentItemGrid {

    public static final int CONTENT_INSET = 4;
    public static final int BODY_INSET = 2;

    private static final Identifier SLOT_HIGHLIGHT_BACK = Identifier.withDefaultNamespace("container/slot_highlight_back");
    private static final Identifier SLOT_HIGHLIGHT_FRONT = Identifier.withDefaultNamespace("container/slot_highlight_front");

    private final List<SlotContent> applicableSlots;
    private final List<SlotContent> exclusiveSlots;
    private final int applicableSlotsPerRow;
    private final int exclusiveSlotsPerRow;
    private int applicableStartY;
    private int exclusiveStartY;
    private final int x;
    private final int y;
    private final int height;

    private int panelLeft;
    private int panelTop;

    public EnchantmentItemGrid(List<SlotContent> applicableSlots, List<SlotContent> exclusiveSlots,
                               int applicableSlotsPerRow, int exclusiveSlotsPerRow,
                               int applicableStartY, int exclusiveStartY,
                               int x, int y, int height) {
        this.applicableSlots = applicableSlots;
        this.exclusiveSlots = exclusiveSlots;
        this.applicableSlotsPerRow = applicableSlotsPerRow;
        this.exclusiveSlotsPerRow = exclusiveSlotsPerRow;
        this.applicableStartY = applicableStartY;
        this.exclusiveStartY = exclusiveStartY;
        this.x = x;
        this.y = y;
        this.height = height;
    }

    public void setOrigin(int panelLeft, int panelTop) {
        this.panelLeft = panelLeft;
        this.panelTop = panelTop;
    }

    public void setStartY(int applicableStartY, int exclusiveStartY) {
        this.applicableStartY = applicableStartY;
        this.exclusiveStartY = exclusiveStartY;
    }

    public int exclusiveCount() {
        return exclusiveSlots.size();
    }

    public SlotContent itemAt(double absoluteMouseX, double absoluteMouseY, int scroll) {
        SlotContent item = applicableStartY == -1
                ? null
                : hit(absoluteMouseX, absoluteMouseY, applicableSlots, applicableSlotsPerRow,
                        EnchantmentUIRenderer.APPLICABLE_SLOT_SPACING, applicableStartY, scroll);

        if (item == null && exclusiveStartY != -1) {
            item = hit(absoluteMouseX, absoluteMouseY, exclusiveSlots, exclusiveSlotsPerRow,
                    EnchantmentUIRenderer.EXCLUSIVE_SLOT_SPACING, exclusiveStartY, scroll);
        }

        return item;
    }

    public void render(GuiGraphicsExtractor g, int absoluteMouseX, int absoluteMouseY, int scroll) {
        if (applicableStartY == -1 && exclusiveStartY == -1) {
            return;
        }

        g.pose().pushMatrix();
        g.pose().translate((float) -panelLeft, (float) -panelTop);

        for (ItemSlot slot : itemSlots(scroll)) {
            boolean hovered = isCellHovered(slot.getX(), slot.getY(), absoluteMouseX, absoluteMouseY);
            int highlightX = slot.getX() - 2;
            int highlightY = slot.getY() - 2;

            if (hovered) {
                g.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_BACK, highlightX, highlightY, 24, 24);
            }

            g.pose().pushMatrix();
            g.pose().translate((float) (slot.getX() + 1), (float) (slot.getY() + 1));
            RecipeViewMenu.OptionalSlotRenderer.DEFAULT.extractRenderState(g, absoluteMouseX, absoluteMouseY, 0.0F);
            g.pose().popMatrix();

            slot.extractRenderState(g, absoluteMouseX, absoluteMouseY, 0.0F);

            if (hovered) {
                g.blitSprite(RenderPipelines.GUI_TEXTURED, SLOT_HIGHLIGHT_FRONT, highlightX, highlightY, 24, 24);
            }
        }

        g.pose().popMatrix();
    }

    private List<ItemSlot> itemSlots(int scroll) {
        List<ItemSlot> slots = new ArrayList<>();

        if (applicableStartY != -1) {
            collect(slots, applicableSlots, applicableSlotsPerRow,
                    EnchantmentUIRenderer.APPLICABLE_SLOT_SPACING, applicableStartY, scroll);
        }

        if (exclusiveStartY != -1) {
            collect(slots, exclusiveSlots, exclusiveSlotsPerRow,
                    EnchantmentUIRenderer.EXCLUSIVE_SLOT_SPACING, exclusiveStartY, scroll);
        }

        return slots;
    }

    private void collect(List<ItemSlot> out, List<SlotContent> contents, int perRow, int spacing, int startY, int scroll) {
        for (int i = 0; i < contents.size(); i++) {
            ItemStack stack = contents.get(i).current();

            if (stack.isEmpty()) {
                continue;
            }

            int slotY = slotY(i, perRow, spacing, startY, scroll);

            if (isRowVisible(slotY)) {
                out.add(new ItemSlot(stack, slotX(i, perRow, spacing), slotY, true));
            }
        }
    }

    private SlotContent hit(double mouseX, double mouseY, List<SlotContent> contents, int perRow, int spacing, int startY, int scroll) {
        for (int i = 0; i < contents.size(); i++) {
            int slotX = slotX(i, perRow, spacing);
            int slotY = slotY(i, perRow, spacing, startY, scroll);

            if (!isRowVisible(slotY)) {
                continue;
            }

            if (isCellHovered(slotX, slotY, mouseX, mouseY)) {
                return contents.get(i);
            }
        }

        return null;
    }

    private int slotX(int index, int perRow, int spacing) {
        return panelLeft + EnchantmentScrollContent.gridX(index, perRow, x + EnchantmentUIRenderer.PADDING + 1, spacing);
    }

    private int slotY(int index, int perRow, int spacing, int startY, int scroll) {
        return panelTop + EnchantmentScrollContent.gridY(index, perRow, startY - scroll, spacing);
    }

    private boolean isRowVisible(int slotY) {
        return slotY + ItemSlot.ITEM_ENTRY_SIZE > panelTop + y && slotY < panelTop + y + height;
    }

    private boolean isCellHovered(int cellX, int cellY, double mouseX, double mouseY) {
        return mouseX >= cellX && mouseX < cellX + ItemSlot.ITEM_ENTRY_SIZE
                && mouseY >= cellY && mouseY < cellY + ItemSlot.ITEM_ENTRY_SIZE;
    }
}
