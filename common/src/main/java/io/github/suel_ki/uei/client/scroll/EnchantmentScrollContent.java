package io.github.suel_ki.uei.client.scroll;

import io.github.suel_ki.uei.client.render.EnchantmentUIRenderer;
import io.github.suel_ki.uei.client.render.ScissorHelper;
import io.github.suel_ki.uei.config.Config;
import io.github.suel_ki.uei.ench.EnchantmentRecipeData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EnchantmentScrollContent {

    public static final int TRACK_WIDTH = 8;

    public static final Component NO_EXCLUSIVES = Component.translatable("uei.no_exclusives")
            .withStyle(ChatFormatting.DARK_GRAY);
    public static final Component DESCRIPTION = Component.translatable("uei.description")
            .withStyle(ChatFormatting.UNDERLINE, ChatFormatting.DARK_GRAY);
    public static final Component RARITY = label("uei.rarity");
    public static final Component MAX_LEVEL = label("uei.max_level");
    public static final Component TREASURE = label("uei.treasure");
    public static final Component TRADEABLE = label("uei.tradeable");
    public static final Component CURSE = label("uei.curse");
    public static final Component DISCOVERABLE = label("uei.discoverable");
    public static final Component ENCHANTING_TABLE = label("uei.enchanting_table");
    public static final Component APPLIES_TO = label("uei.applies_to");
    public static final Component EXCLUSIVE_HEADER = label("uei.exclusives");

    private static Component label(String key) {
        return Component.translatable(key).withStyle(ChatFormatting.DARK_GRAY);
    }

    private static Component boolDisplay(boolean value) {
        ChatFormatting color = value ? ChatFormatting.GREEN : ChatFormatting.RED;

        if (!Config.get().useTextForBooleans) {
            return Component.literal(value ? "✔" : "✘").withStyle(color);
        }

        return (value ? CommonComponents.GUI_YES : CommonComponents.GUI_NO)
                .copy()
                .withStyle(color);
    }

    private EnchantmentScrollContent() {}

    public static float computeScrollOffset(int textWidth, int availableWidth) {
        int overflow = textWidth - availableWidth;
        double time = (double) Util.getMillis() / 1000.0D;
        double cycle = Math.max((double) overflow * 0.15D / Config.get().textScrollSpeedMultiplier, 4.0D);
        double rawSin = Math.sin(time * Math.PI * 2 / cycle);
        double clampedSin = Math.clamp(rawSin * 1.6, -1.0, 1.0);
        double f = (clampedSin + 1.0) / 2.0;
        return (float) (f * overflow);
    }

    public static void renderScrollingString(GuiGraphicsExtractor g, Font font, Component text,
                                             int minX, int minY, int maxX, int maxY, int color) {
        renderScrollingString(g, font, text, minX, minY, maxX, maxY, color, UNIVERSAL_SCISSOR);
    }

    public static void renderScrollingString(GuiGraphicsExtractor g, Font font, Component text,
                                             int minX, int minY, int maxX, int maxY, int color,
                                             ScissorRenderer scissorRenderer) {
        int textWidth = font.width(text);
        int availableWidth = maxX - minX;
        if (textWidth > availableWidth) {
            float offset = computeScrollOffset(textWidth, availableWidth);
            scissorRenderer.render(g, font, text, minX, minY, maxX, maxY, color, offset);
        } else {
            g.text(font, text, minX, minY, color, false);
        }
    }

    @FunctionalInterface
    public interface ScissorRenderer {
        void render(GuiGraphicsExtractor g, Font font, Component text, int minX, int minY,
                    int maxX, int maxY, int color, float offset);
    }

    public static final ScissorRenderer UNIVERSAL_SCISSOR = (g, font, text, minX, minY, maxX, maxY, color, offset) -> {
        try (var ignored = ScissorHelper.scissor(g, minX, minY, maxX, maxY)) {
            g.pose().pushMatrix();
            g.pose().translate(minX - offset, (float) minY);
            g.text(font, text, 0, 0, color, false);
            g.pose().popMatrix();
        }
    };

    public static void drawInfoLine(GuiGraphicsExtractor g, Font font, int x, int y, int maxX,
                                    Component label, Component value, int valueColor,
                                    ScissorRenderer scissorRenderer) {
        g.text(font, label, x, y, -1, false);
        int startX = x + font.width(label) + 4;
        int availableWidth = maxX - startX;
        renderScrollingString(g, font, value, startX, y,
                startX + availableWidth, y + font.lineHeight, valueColor, scissorRenderer);
    }

    public static void drawExclusiveHeader(GuiGraphicsExtractor g, Font font, int x, int y, int maxX,
                                           Component header, int exclusiveCount) {
        drawExclusiveHeader(g, font, x, y, maxX, header, exclusiveCount, UNIVERSAL_SCISSOR);
    }

    public static void drawExclusiveHeader(GuiGraphicsExtractor g, Font font, int x, int y, int maxX,
                                           Component header, int exclusiveCount,
                                           ScissorRenderer scissorRenderer) {
        Component count = Component.literal(String.valueOf(exclusiveCount))
                .withStyle(ChatFormatting.DARK_GRAY);
        int countWidth = font.width(count);
        int headerMaxX = maxX - countWidth - 4;
        renderScrollingString(g, font, header, x, y, headerMaxX,
                y + font.lineHeight, -1, scissorRenderer);
        g.text(font, count, maxX - countWidth, y, -1, false);
    }

    public static void drawDescription(GuiGraphicsExtractor g, Font font, List<FormattedCharSequence> descLines,
                                       int x, int y, int pad) {
        g.text(font, DESCRIPTION, x + pad, y, -1, false);
        int lh = font.lineHeight + 1;
        int descTextY = y + 10;
        for (int i = 0; i < descLines.size(); i++) {
            g.text(font, descLines.get(i), x + pad, descTextY + i * lh, -1, false);
        }
    }
    public static void drawAttributes(GuiGraphicsExtractor g, Font font, EnchantmentRecipeData recipe,
                                      int x, int y, int maxX, ScissorRenderer scissorRenderer) {
        int lineY = y;
        int ilh = EnchantmentUIRenderer.INFO_LINE_HEIGHT;
        Config cfg = Config.get();

        for (Config.InfoField field : cfg.infoOrder) {
            switch (field) {
                case RARITY -> {
                    if (cfg.showRarity) {
                        drawInfoLine(g, font, x, lineY, maxX,
                                RARITY, recipe.rarityName(), -1, scissorRenderer);
                        lineY += ilh;
                    }
                }
                case MAX_LEVEL -> {
                    if (cfg.showMaxLevel) {
                        drawInfoLine(g, font, x, lineY, maxX,
                                MAX_LEVEL, Component.literal(String.valueOf(recipe.maxLevel())), -1, scissorRenderer);
                        lineY += ilh;
                    }
                }
                case TREASURE -> {
                    if (cfg.showTreasure) {
                        drawInfoLine(g, font, x, lineY, maxX,
                                TREASURE, boolDisplay(recipe.treasure()), -1, scissorRenderer);
                        lineY += ilh;
                    }
                }
                case TRADEABLE -> {
                    if (cfg.showTradeable) {
                        drawInfoLine(g, font, x, lineY, maxX,
                                TRADEABLE, boolDisplay(recipe.tradeable()), -1, scissorRenderer);
                        lineY += ilh;
                    }
                }
                case CURSE -> {
                    if (cfg.showCurse) {
                        drawInfoLine(g, font, x, lineY, maxX,
                                CURSE, boolDisplay(recipe.curse()), -1, scissorRenderer);
                        lineY += ilh;
                    }
                }
                case DISCOVERABLE -> {
                    if (cfg.showDiscoverable) {
                        drawInfoLine(g, font, x, lineY, maxX,
                                DISCOVERABLE, boolDisplay(recipe.discoverable()), -1, scissorRenderer);
                        lineY += ilh;
                    }
                }
                case ENCHANTING_TABLE -> {
                    if (cfg.showEnchantingTable) {
                        boolean canEnchantTable = !recipe.treasure() && !recipe.curse();
                        drawInfoLine(g, font, x, lineY, maxX,
                                ENCHANTING_TABLE, boolDisplay(canEnchantTable), -1, scissorRenderer);
                        lineY += ilh;
                    }
                }
            }
        }
    }

    public static int applicableItemStartY(EnchantmentRecipeData recipe) {
        return applicableItemStartY(recipe.descriptionLines(Minecraft.getInstance().font));
    }

    public static int applicableItemStartY(List<FormattedCharSequence> descLines) {
        int currentY = 0;
        int ilh = EnchantmentUIRenderer.INFO_LINE_HEIGHT;
        Config cfg = Config.get();
        var font = Minecraft.getInstance().font;

        for (Config.Section sec : cfg.sectionOrder) {
            if (sec == Config.Section.APPLIES_TO) {
                return currentY + font.lineHeight;
            }
            switch (sec) {
                case DESCRIPTION -> currentY += EnchantmentUIRenderer.DESC_TEXT_Y + descLines.size() * (font.lineHeight + 1) + 2;
                case INFO_LINES -> currentY += visibleInfoLineCount(cfg) * ilh + 2;
                case EXCLUSIVES -> currentY += font.lineHeight + 2;
            }
        }
        return currentY + font.lineHeight;
    }

    private static int visibleInfoLineCount(Config cfg) {
        int count = 0;
        if (cfg.showRarity) count++;
        if (cfg.showMaxLevel) count++;
        if (cfg.showTreasure) count++;
        if (cfg.showTradeable) count++;
        if (cfg.showCurse) count++;
        if (cfg.showDiscoverable) count++;
        if (cfg.showEnchantingTable) count++;
        return count;
    }

    // Compute exclusive enchant title start Y
    public static int exclusiveHeaderStartY(int aiy, int applicableRows) {
        return aiy + applicableRows * EnchantmentUIRenderer.APPLICABLE_SLOT_SPACING + 2;
    }

    // Compute exclusive slots start Y
    public static int exclusiveSlotsStartY(int aiy, int applicableRows) {
        var font = Minecraft.getInstance().font;
        return exclusiveHeaderStartY(aiy, applicableRows) + font.lineHeight;
    }

    // Batch applicable items by max slot count, evenly distributed
    public static List<List<ItemStack>> batchApplicableItems(EnchantmentRecipeData recipe) {
        List<Holder<Item>> items = recipe.applicableItems();
        int itemsSize = items.size();
        int maxSlots = Math.max(1, Config.get().maxApplicableSlots);

        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        int slots = Math.min(itemsSize, maxSlots);
        List<List<ItemStack>> batches = new ArrayList<>(slots);

        int base = itemsSize / slots;
        int remainder = itemsSize % slots;

        int idx = 0;
        for (int i = 0; i < slots; i++) {
            int currentBatchSize = base + (i < remainder ? 1 : 0);

            List<ItemStack> currentBatch = new ArrayList<>(currentBatchSize);

            for (int j = 0; j < currentBatchSize; j++) {
                currentBatch.add(new ItemStack(items.get(idx + j)));
            }

            batches.add(currentBatch);
            idx += currentBatchSize;
        }

        return batches;
    }

    // Calculate total rows required to display all items
    public static int calculateRows(int itemCount, int itemsPerRow) {
        return (itemCount + itemsPerRow - 1) / Math.max(itemsPerRow, 1);
    }

    // Applicable slots per row capacity
    public static int applicableSlotsPerRow(int contentWidth, int scrollbarWidth) {
        int availableW = contentWidth - scrollbarWidth - EnchantmentUIRenderer.PADDING;
        return Math.max(1, availableW / EnchantmentUIRenderer.APPLICABLE_SLOT_SPACING);
    }

    // Compute exclusive enchantment slots per row
    public static int exclusiveSlotsPerRow(int contentWidth, int scrollbarWidth) {
        int spacing = EnchantmentUIRenderer.EXCLUSIVE_SLOT_SPACING;
        return Math.max((contentWidth - scrollbarWidth - EnchantmentUIRenderer.PADDING) / spacing, 1);
    }

    public static int gridX(int index, int perRow, int startX, int spacing) {
        return startX + (index % perRow) * spacing;
    }

    public static int gridY(int index, int perRow, int startY, int spacing) {
        return startY + (index / perRow) * spacing;
    }

    public static class LayoutMetrics {
        public int descY = -1, infoY = -1, appliesToY = -1, exclusivesY = -1;
        public int contentHeight;

        public LayoutMetrics(EnchantmentRecipeData recipe, Font font, int appRows, int excRows) {
            int currentY = 0;
            int ilh = EnchantmentUIRenderer.INFO_LINE_HEIGHT;
            Config cfg = Config.get();

            for (Config.Section sec : cfg.sectionOrder) {
                switch (sec) {
                    case DESCRIPTION -> {
                        this.descY = currentY;
                        int lines = recipe.descriptionLines(font).size();
                        currentY += EnchantmentUIRenderer.DESC_TEXT_Y + lines * (font.lineHeight + 1) + 2;
                    }
                    case INFO_LINES -> {
                        this.infoY = currentY;
                        currentY += visibleInfoLineCount(cfg) * ilh + 2;
                    }
                    case APPLIES_TO -> {
                        this.appliesToY = currentY;
                        currentY += font.lineHeight + appRows * EnchantmentUIRenderer.APPLICABLE_SLOT_SPACING + 2;
                    }
                    case EXCLUSIVES -> {
                        this.exclusivesY = currentY;
                        int actualExcRows = Math.max(excRows, 0);
                        currentY += font.lineHeight + actualExcRows * EnchantmentUIRenderer.EXCLUSIVE_SLOT_SPACING + 2;
                    }
                }
            }
            this.contentHeight = currentY + EnchantmentUIRenderer.PADDING;
        }

        public int getAppliesToSlotsY() {
            return appliesToY == -1 ? -1 : appliesToY + Minecraft.getInstance().font.lineHeight;
        }

        public int getExclusiveSlotsY() {
            return exclusivesY == -1 ? -1 : exclusivesY + Minecraft.getInstance().font.lineHeight;
        }
    }
}