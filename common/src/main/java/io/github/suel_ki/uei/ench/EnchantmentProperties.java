package io.github.suel_ki.uei.ench;

import io.github.suel_ki.uei.config.Config;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;

public record EnchantmentProperties(
        String descriptionId,
        int maxLevel,
        boolean curse,
        boolean treasureOnly,
        boolean tradeable,
        boolean discoverable,
        Component rarity,
        String modid
) {

    public static EnchantmentProperties of(Holder<Enchantment> holder) {
        Enchantment enchantment = holder.value();

        ResourceLocation loc = holder.unwrapKey()
                .map(ResourceKey::location)
                .orElse(ResourceLocation.withDefaultNamespace("unknown"));

        int weight = enchantment.getWeight();

        String descId = Util.makeDescriptionId("enchantment", loc);

        return new EnchantmentProperties(
                descId,
                enchantment.getMaxLevel(),
                holder.is(EnchantmentTags.CURSE),
                holder.is(EnchantmentTags.TREASURE),
                holder.is(EnchantmentTags.TRADEABLE),
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
            name = Component.translatable("uei.rarity.very_rare").withColor(cfg.rarityColorVeryRare);
        }
        return name;
    }

}
