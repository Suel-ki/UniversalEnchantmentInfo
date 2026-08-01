package io.github.suel_ki.uei.ench;

import io.github.suel_ki.uei.PlatformHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.ArrayList;
import java.util.List;

public record EnchantmentRecipeData(
        Holder<Enchantment> enchantment,
        EnchantmentProperties enchantmentProperties,
        ItemStack enchantedBook,
        List<ItemStack> allLevelBooks,
        Component localizedName,
        Component modName,
        List<ItemStack> exclusiveStacks,
        List<Holder<Item>> applicableItems

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
        return enchantmentProperties.rarity();
    }

    public static EnchantmentRecipeData create(Holder<Enchantment> holder, List<Holder<Item>> applicableItems,
                                               List<ItemStack> exclusiveStacks, ItemStack maxLevelBook) {
        EnchantmentProperties props = EnchantmentProperties.of(holder);

        String modId = props.modid();
        Component modName = Component.literal(PlatformHelper.getModName(modId)).withStyle(ChatFormatting.ITALIC, ChatFormatting.BLUE);

        List<ItemStack> allLevels = new ArrayList<>(props.maxLevel());
        for (int lvl = 1; lvl < props.maxLevel(); lvl++) {
            allLevels.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(holder, lvl)));
        }
        allLevels.add(maxLevelBook);

        MutableComponent name = Component.translatable(props.descriptionId())
                .withStyle(props.curse() ? ChatFormatting.RED : ChatFormatting.WHITE);
        return new EnchantmentRecipeData(
                holder, props, maxLevelBook, allLevels, name, modName,
                exclusiveStacks, applicableItems
        );
    }

}
