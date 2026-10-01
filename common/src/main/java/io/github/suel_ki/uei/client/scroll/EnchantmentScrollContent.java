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
        render(g, font, text, minX, minY, maxX, maxY, color, scissorRenderer, false);
    }

    public static void renderScrollingString(GuiGraphicsExtractor g, Font font, Component text,
                                             int minX, int minY, int maxX, int maxY, int color, boolean shadow) {
        render(g, font, text, minX, minY, maxX, maxY, color,
                shadow ? UNIVERSAL_SCISSOR_SHADOW : UNIVERSAL_SCISSOR, shadow);
    }

    private static void render(GuiGraphicsExtractor g, Font font, Component text,
                               int minX, int minY, int maxX, int maxY, int color,
                               ScissorRenderer scissorRenderer, boolean shadow) {
        int textWidth = font.width(text);
        int availableWidth = maxX - minX;
        if (textWidth > availableWidth) {
            float offset = computeScrollOffset(textWidth, availableWidth);
            scissorRenderer.render(g, font, text, minX, minY, maxX, maxY, color, offset);
        } else {
            g.text(font, text, minX, minY, color, shadow);
        }
    }

    @FunctionalInterface
    public interface ScissorRenderer {
        void render(GuiGraphicsExtractor g, Font font, Component text, int minX, int minY,
                    int maxX, int maxY, int color, float offset);
    }

    private static ScissorRenderer scissor(boolean shadow) {
        return (g, font, text, minX, minY, maxX, maxY, color, offset) -> {
            try (var ignored = ScissorHelper.scissor(g, minX, minY, maxX, maxY)) {
                g.pose().pushMatrix();
                g.pose().translate(minX - offset, (float) minY);
                g.text(font, text, 0, 0, color, shadow);
                g.pose().popMatrix();
            }
        };
    }

    public static final ScissorRenderer UNIVERSAL_SCISSOR = scissor(false);
    public static final ScissorRenderer UNIVERSAL_SCISSOR_SHADOW = scissor(true);

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
        int descTextY = y + EnchantmentUIRenderer.DESC_TEXT_Y;
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
            if (!isFieldVisible(cfg, field)) {
                continue;
            }

            switch (field) {
                case RARITY -> drawInfoLine(g, font, x, lineY, maxX,
                        RARITY, recipe.rarityName(), -1, scissorRenderer);
                case MAX_LEVEL -> drawInfoLine(g, font, x, lineY, maxX,
                        MAX_LEVEL, Component.literal(String.valueOf(recipe.maxLevel())), -1, scissorRenderer);
                case TREASURE -> drawInfoLine(g, font, x, lineY, maxX,
                        TREASURE, boolDisplay(recipe.treasure()), -1, scissorRenderer);
                case TRADEABLE -> drawInfoLine(g, font, x, lineY, maxX,
                        TRADEABLE, boolDisplay(recipe.tradeable()), -1, scissorRenderer);
                case CURSE -> drawInfoLine(g, font, x, lineY, maxX,
                        CURSE, boolDisplay(recipe.curse()), -1, scissorRenderer);
                case DISCOVERABLE -> drawInfoLine(g, font, x, lineY, maxX,
                        DISCOVERABLE, boolDisplay(recipe.discoverable()), -1, scissorRenderer);
                case ENCHANTING_TABLE -> drawInfoLine(g, font, x, lineY, maxX,
                        ENCHANTING_TABLE, boolDisplay(!recipe.treasure() && !recipe.curse()), -1, scissorRenderer);
            }

            lineY += ilh;
        }
    }

    private static boolean isFieldVisible(Config cfg, Config.InfoField field) {
        return switch (field) {
            case RARITY -> cfg.showRarity;
            case MAX_LEVEL -> cfg.showMaxLevel;
            case TREASURE -> cfg.showTreasure;
            case TRADEABLE -> cfg.showTradeable;
            case CURSE -> cfg.showCurse;
            case DISCOVERABLE -> cfg.showDiscoverable;
            case ENCHANTING_TABLE -> cfg.showEnchantingTable;
        };
    }

    private static int visibleInfoLineCount(Config cfg) {
        int count = 0;

        for (Config.InfoField field : cfg.infoOrder) {
            if (isFieldVisible(cfg, field)) {
                count++;
            }
        }

        return count;
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
        return slotsPerRow(contentWidth, scrollbarWidth, EnchantmentUIRenderer.APPLICABLE_SLOT_SPACING);
    }

    // Compute exclusive enchantment slots per row
    public static int exclusiveSlotsPerRow(int contentWidth, int scrollbarWidth) {
        return slotsPerRow(contentWidth, scrollbarWidth, EnchantmentUIRenderer.EXCLUSIVE_SLOT_SPACING);
    }

    private static int slotsPerRow(int contentWidth, int scrollbarWidth, int spacing) {
        return Math.max(1, (contentWidth - scrollbarWidth - EnchantmentUIRenderer.PADDING) / spacing);
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
            this(recipe, font, appRows, excRows, EnchantmentRecipeData.MAX_DESC_WIDTH);
        }

        public LayoutMetrics(EnchantmentRecipeData recipe, Font font, int appRows, int excRows, int descWidth) {
            int currentY = 0;

            for (Config.Section section : Config.get().sectionOrder) {
                switch (section) {
                    case DESCRIPTION -> this.descY = currentY;
                    case INFO_LINES -> this.infoY = currentY;
                    case APPLIES_TO -> this.appliesToY = currentY;
                    case EXCLUSIVES -> this.exclusivesY = currentY;
                }

                currentY += sectionHeight(section, recipe, font, appRows, excRows, descWidth);
            }

            this.contentHeight = currentY + EnchantmentUIRenderer.PADDING;
        }

        private static int sectionHeight(Config.Section section, EnchantmentRecipeData recipe, Font font,
                                         int appRows, int excRows, int descWidth) {
            return switch (section) {
                case DESCRIPTION -> EnchantmentUIRenderer.DESC_TEXT_Y
                        + recipe.descriptionLines(font, descWidth).size() * (font.lineHeight + 1) + 2;
                case INFO_LINES -> visibleInfoLineCount(Config.get()) * EnchantmentUIRenderer.INFO_LINE_HEIGHT + 2;
                case APPLIES_TO -> font.lineHeight + appRows * EnchantmentUIRenderer.APPLICABLE_SLOT_SPACING + 2;
                case EXCLUSIVES -> font.lineHeight
                        + Math.max(excRows, 0) * EnchantmentUIRenderer.EXCLUSIVE_SLOT_SPACING + 2;
            };
        }

        public int maxScroll(int viewportHeight) {
            return Math.max(contentHeight - viewportHeight, 0);
        }

        public int getAppliesToSlotsY() {
            return appliesToY == -1 ? -1 : appliesToY + Minecraft.getInstance().font.lineHeight;
        }

        public int getExclusiveSlotsY() {
            return exclusivesY == -1 ? -1 : exclusivesY + Minecraft.getInstance().font.lineHeight;
        }
    }
}
