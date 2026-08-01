package io.github.suel_ki.uei.ench;

import io.github.suel_ki.uei.PlatformHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.List;

public record EnchantmentRecipeData(
        Enchantment enchantment,
        EnchantmentProperties enchantmentProperties,
        ItemStack enchantedBook,
        List<ItemStack> allLevelBooks,
        Component localizedName,
        Component modName,
        List<ItemStack> exclusiveStacks,
        List<ItemStack> applicableStacks
) {

    private static final int MAX_DESC_WIDTH = 144;

    public List<FormattedCharSequence> descriptionLines(Font font) {
        String descKey = enchantmentProperties.descriptionId() + ".desc";
        Component description;

        if (I18n.exists(descKey)) {
            description = Component.translatable(descKey).withStyle(ChatFormatting.BLACK);
        } else {
            description = Component.translatable("uei.no_description").withStyle(ChatFormatting.BLACK);
        }

        return font.split(description, MAX_DESC_WIDTH);
    }

    public Component appliesToText() {
        String categoryKey = "uei.category." + enchantmentProperties.category();
        if (I18n.exists(categoryKey)) {
            return Component.translatable(categoryKey);
        }
        return Component.literal(enchantmentProperties.category());
    }

    public int maxLevel() {
        return enchantmentProperties.maxLevel();
    }

    public boolean curse() {
        return enchantmentProperties.curse();
    }

    public boolean treasure() {
        return enchantmentProperties.treasureOnly();
    }

    public boolean tradeable() {
        return enchantmentProperties.tradeable();
    }

    public boolean discoverable() {
        return enchantmentProperties.discoverable();
    }

    public Component rarityName() {
        return enchantmentProperties.rarity().name();
    }

    public int rarityColor() {
        return enchantmentProperties.rarityColor();
    }

    public static EnchantmentRecipeData create(Enchantment enchantment, List<ItemStack> applicableStacks,
                                               List<ItemStack> exclusiveStacks, ItemStack maxLevelBook) {
        EnchantmentProperties props = EnchantmentProperties.of(enchantment);

        String modId = props.modid();
        Component modName = Component.literal(PlatformHelper.getModName(modId)).withStyle(ChatFormatting.ITALIC, ChatFormatting.BLUE);

        List<ItemStack> allLevels = new ArrayList<>(props.maxLevel());
        for (int lvl = 1; lvl < props.maxLevel(); lvl++) {
            ItemStack lvlBook = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantmentHelper.setEnchantments(java.util.Map.of(enchantment, lvl), lvlBook);
            allLevels.add(lvlBook);
        }
        allLevels.add(maxLevelBook);

        MutableComponent name = Component.translatable(props.descriptionId())
                .withStyle(props.curse() ? ChatFormatting.RED : ChatFormatting.WHITE);
        return new EnchantmentRecipeData(
                enchantment, props, maxLevelBook, allLevels, name, modName,
                exclusiveStacks, applicableStacks
        );
    }
}
