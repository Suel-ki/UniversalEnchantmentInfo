//? if <=1.20.1 {
/*package io.github.suel_ki.uei.ench;

import io.github.suel_ki.uei.compat.apothic.ApothicCompat;
import io.github.suel_ki.uei.config.Config;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Locale;

public record EnchantmentProperties(
        String descriptionId,
        int maxLevel,
        boolean curse,
        boolean treasureOnly,
        boolean tradeable,
        boolean discoverable,
        RarityInfo rarity,
        String category,
        String modid
) {

    public record RarityInfo(Component name, String rarityKey) {}

    public static EnchantmentProperties of(Enchantment enchantment) {
        Enchantment.Rarity rarity = enchantment.getRarity();
        RarityInfo rarityInfo = new RarityInfo(
                getRarityComponent(rarity),
                rarity != null ? rarity.name() : "UNKNOWN"
        );

        var key = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);

        return new EnchantmentProperties(
                enchantment.getDescriptionId(),
                ApothicCompat.getRealMaxLevel(enchantment),
                enchantment.isCurse(),
                enchantment.isTreasureOnly(),
                enchantment.isTradeable(),
                enchantment.isDiscoverable(),
                rarityInfo,
                enchantment.category.name().toLowerCase(Locale.ROOT),
                key != null ? key.getNamespace() : "minecraft"
        );
    }

    private static Component getRarityComponent(Enchantment.Rarity rarity) {
        if (rarity == null) {
            return Component.literal("UNKNOWN");
        }

        Config cfg = Config.get();
        return switch (rarity) {
            case COMMON -> Component.translatable("uei.rarity.common").withStyle(style -> style.withColor(TextColor.fromRgb(cfg.rarityColorCommon)));
            case UNCOMMON -> Component.translatable("uei.rarity.uncommon").withStyle(style -> style.withColor(TextColor.fromRgb(cfg.rarityColorUncommon)));
            case RARE -> Component.translatable("uei.rarity.rare").withStyle(style -> style.withColor(TextColor.fromRgb(cfg.rarityColorRare)));
            case VERY_RARE -> Component.translatable("uei.rarity.very_rare").withStyle(style -> style.withColor(TextColor.fromRgb(cfg.rarityColorVeryRare)));
            default -> Component.literal(rarity.name());
        };
    }

    public int rarityColor() {
        Config cfg = Config.get();
        return switch (rarity.rarityKey()) {
            case "COMMON" -> cfg.rarityColorCommon;
            case "UNCOMMON" -> cfg.rarityColorUncommon;
            case "RARE" -> cfg.rarityColorRare;
            case "VERY_RARE" -> cfg.rarityColorVeryRare;
            default -> 0xFFFFFF;
        };
    }

}
*///?} else {
package io.github.suel_ki.uei.ench;

//? if apothic {
//import io.github.suel_ki.uei.compat.apothic.ApothicCompat;
//?}
import io.github.suel_ki.uei.config.Config;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EnchantmentTags;
//? if <1.21.11 {
//import net.minecraft.Util;
//?} else {
import net.minecraft.util.Util;
//?}
import net.minecraft.world.item.enchantment.Enchantment;

public record EnchantmentProperties(
        String descriptionId,
        int maxLevel,
        boolean curse,
        boolean treasureOnly,
        boolean tradeable,
        boolean discoverable,
        boolean inEnchantingTable,
        Component rarity,
        String modid
) {

    public static EnchantmentProperties of(Holder<Enchantment> holder) {
        Enchantment enchantment = holder.value();

        Identifier loc = holder.unwrapKey()
                .map(key -> key.identifier())
                .orElse(Identifier.withDefaultNamespace("unknown"));

        int weight = enchantment.getWeight();

        String descId = Util.makeDescriptionId("enchantment", loc);

        return new EnchantmentProperties(
                descId,
                //? if apothic {
                //ApothicCompat.getRealMaxLevel(enchantment),
                //?} else {
                enchantment.getMaxLevel(),
                //?}
                holder.is(EnchantmentTags.CURSE),
                holder.is(EnchantmentTags.TREASURE),
                holder.is(EnchantmentTags.TRADEABLE),
                holder.is(EnchantmentTags.ON_RANDOM_LOOT),
                holder.is(EnchantmentTags.IN_ENCHANTING_TABLE),
                rarityName(weight),
                loc.getNamespace()
        );
    }

    public static Component rarityName(int weight) {
        Config cfg = Config.get();
        Component name;
        if (weight >= 10) {
            name = Component.translatable("uei.rarity.common").withColor(cfg.rarityColorCommon);
        } else if (weight >= 5) {
            name = Component.translatable("uei.rarity.uncommon").withColor(cfg.rarityColorUncommon);
        } else if (weight >= 2) {
            name = Component.translatable("uei.rarity.rare").withColor(cfg.rarityColorRare);
        } else {
            //? if <=1.21.4 {
            //name = Component.translatable("uei.rarity.very_rare").withColor(cfg.rarityColorVeryRare);
            //?} else {
            name = Component.translatable("uei.rarity.epic").withColor(cfg.rarityColorEpic);
            //?}
        }
        return name;
    }

}
//?}
