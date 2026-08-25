package io.github.suel_ki.uei.ench;

import io.github.suel_ki.uei.compat.ApotheosisCompat;
import io.github.suel_ki.uei.config.Config;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
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
                ApotheosisCompat.getRealMaxLevel(enchantment),
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
        return switch (rarity) {
            case COMMON -> Component.translatable("uei.rarity.common");
            case UNCOMMON -> Component.translatable("uei.rarity.uncommon");
            case RARE -> Component.translatable("uei.rarity.rare");
            case VERY_RARE -> Component.translatable("uei.rarity.very_rare");
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
