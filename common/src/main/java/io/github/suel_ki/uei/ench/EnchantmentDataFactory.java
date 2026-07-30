package io.github.suel_ki.uei.ench;

import io.github.suel_ki.uei.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentDataFactory {

    private static List<EnchantmentRecipeData> CACHED_RECIPES = null;

    public static List<EnchantmentRecipeData> getOrComputeRecipes() {
        if (CACHED_RECIPES != null) {
            return CACHED_RECIPES;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return List.of();
        }

        RegistryAccess registryAccess = mc.level.registryAccess();
        Registry<Enchantment> enchantmentRegistry = registryAccess.registryOrThrow(Registries.ENCHANTMENT);

        List<Holder.Reference<Enchantment>> allEnchantments = enchantmentRegistry.holders().toList();
        int maxItems = Config.get().maxApplicableItems;

        Map<Holder<Enchantment>, ItemStack> maxLevelBookCache = new HashMap<>(allEnchantments.size());
        for (Holder.Reference<Enchantment> e : allEnchantments) {
            maxLevelBookCache.put(e, EnchantedBookItem.createForEnchantment(new EnchantmentInstance(e, e.value().getMaxLevel())));
        }

        List<EnchantmentRecipeData> recipes = new ArrayList<>(allEnchantments.size());

        for (Holder.Reference<Enchantment> targetEnchantment : allEnchantments) {
            Enchantment enchantment = targetEnchantment.value();

            List<Holder<Item>> applicableList = enchantment.getSupportedItems().stream()
                    .limit(maxItems)
                    .toList();

            List<ItemStack> exclusiveBooks = new ArrayList<>();
            for (Holder<Enchantment> otherEnchantment : enchantment.exclusiveSet()) {
                if (otherEnchantment != targetEnchantment) {
                    ItemStack book = maxLevelBookCache.get(otherEnchantment);
                    if (book != null) {
                        exclusiveBooks.add(book);
                    }
                }
            }

            recipes.add(EnchantmentRecipeData.create(
                    targetEnchantment,
                    applicableList,
                    exclusiveBooks
            ));
        }

        CACHED_RECIPES = List.copyOf(recipes);

        return CACHED_RECIPES;
    }

    public static void invalidateCache() {
        CACHED_RECIPES = null;
    }
}