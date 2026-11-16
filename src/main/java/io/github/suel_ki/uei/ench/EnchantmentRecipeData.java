//? if <=1.20.1 {
/*package io.github.suel_ki.uei.ench;

import io.github.suel_ki.uei.PlatformHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.Identifier;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.core.registries.BuiltInRegistries;
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
        List<List<ItemStack>> exclusiveStacks,
        List<ItemStack> applicableStacks
) {

    public static final int MAX_DESC_WIDTH = 144;

    public List<FormattedCharSequence> descriptionLines(Font font) {
        return descriptionLines(font, MAX_DESC_WIDTH);
    }

    public List<FormattedCharSequence> descriptionLines(Font font, int maxWidth) {
        String base = enchantmentProperties.descriptionId();
        String descKey = base + ".desc";
        Component description;

        if (I18n.exists(descKey)) {
            description = Component.translatable(descKey).withStyle(ChatFormatting.BLACK);
        } else if (I18n.exists(base + ".description")) {
            description = Component.translatable(base + ".description").withStyle(ChatFormatting.BLACK);
        } else {
            description = Component.translatable("uei.no_description").withStyle(ChatFormatting.BLACK);
        }

        return font.split(description, maxWidth);
    }

    public Identifier id() {
        return BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
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
                                               List<List<ItemStack>> exclusiveStacks, ItemStack maxLevelBook) {
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
*///?} else {
package io.github.suel_ki.uei.ench;

import io.github.suel_ki.uei.PlatformHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
//? if <26.2 {
//import net.minecraft.client.resources.language.I18n;
//?} else {
import net.minecraft.locale.Language;
//?}
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.FormattedCharSequence;
//? if 1.21.1 {
//import net.minecraft.world.item.EnchantedBookItem;
//?}
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
//? if 1.21.1 {
//import net.minecraft.world.item.enchantment.EnchantmentInstance;
//?} else {
import static io.github.suel_ki.uei.ench.EnchantmentDataFactory.createEnchantedBook;
//?}

import java.util.ArrayList;
import java.util.List;

public record EnchantmentRecipeData(
        Holder<Enchantment> enchantment,
        EnchantmentProperties enchantmentProperties,
        ItemStack enchantedBook,
        List<ItemStack> allLevelBooks,
        Component localizedName,
        Component modName,
        List<List<ItemStack>> exclusiveStacks,
        List<Holder<Item>> applicableItems
) {

    public static final int MAX_DESC_WIDTH = 144;

    public List<FormattedCharSequence> descriptionLines(Font font) {
        return descriptionLines(font, MAX_DESC_WIDTH);
    }

    public List<FormattedCharSequence> descriptionLines(Font font, int maxWidth) {
        String key = enchantmentProperties.descriptionId();
        if (this.enchantment.value().description().getContents() instanceof TranslatableContents translatable) {
            key = translatable.getKey();
        }

        String descKey = key + ".desc";
        Component description;

        //? if <26.2 {
        /*if (I18n.exists(descKey)) {
            description = Component.translatable(descKey).withStyle(ChatFormatting.BLACK);
        } else if (I18n.exists(key + ".description")) {
            description = Component.translatable(key + ".description").withStyle(ChatFormatting.BLACK);
        } else {
            description = Component.translatable("uei.no_description").withStyle(ChatFormatting.BLACK);
        }
        *///?} else {
        if (Language.getInstance().has(descKey)) {
            description = Component.translatable(descKey).withStyle(ChatFormatting.BLACK);
        } else if (Language.getInstance().has(key + ".description")) {
            description = Component.translatable(key + ".description").withStyle(ChatFormatting.BLACK);
        } else {
            description = Component.translatable("uei.no_description").withStyle(ChatFormatting.BLACK);
        }
        //?}

        return font.split(description, maxWidth);
    }

    public Identifier id() {
        return enchantment.unwrapKey().map(key -> key.identifier()).orElse(null);
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
                                               List<List<ItemStack>> exclusiveStacks,
                                               ItemStack maxLevelBook) {
        EnchantmentProperties props = EnchantmentProperties.of(holder);

        String modId = props.modid();
        Component modName = Component.literal(PlatformHelper.getModName(modId)).withStyle(ChatFormatting.ITALIC, ChatFormatting.BLUE);

        List<ItemStack> allLevels = new ArrayList<>(props.maxLevel());
        for (int lvl = 1; lvl < props.maxLevel(); lvl++) {
            //? if 1.21.1 {
            //allLevels.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(holder, lvl)));
            //?} else {
            allLevels.add(createEnchantedBook(holder, lvl));
            //?}
        }
        allLevels.add(maxLevelBook);

        MutableComponent name = holder.value().description().copy()
                .withStyle(props.curse() ? ChatFormatting.RED : ChatFormatting.WHITE);
        return new EnchantmentRecipeData(
                holder, props, maxLevelBook, allLevels, name, modName,
                exclusiveStacks, applicableItems
        );
    }

}
//?}
