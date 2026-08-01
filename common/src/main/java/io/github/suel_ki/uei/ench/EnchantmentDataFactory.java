package io.github.suel_ki.uei.ench;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

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

        List<Enchantment> allEnchantments = BuiltInRegistries.ENCHANTMENT.stream().toList();

        List<ItemStack> validStacks = BuiltInRegistries.ITEM.stream()
                .map(Item::getDefaultInstance)
                .filter(stack -> !stack.isEmpty() && (stack.isEnchantable() || stack.getItem() == Items.BOOK))
                .toList();

        Map<Enchantment, ItemStack> maxLevelBookCache = new HashMap<>(allEnchantments.size());
        for (Enchantment e : allEnchantments) {
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantmentHelper.setEnchantments(Map.of(e, e.getMaxLevel()), book);
            maxLevelBookCache.put(e, book);
        }

        List<EnchantmentRecipeData> recipes = new ArrayList<>(allEnchantments.size());

        List<List<ItemStack>> applicable = allEnchantments.stream()
                .map(e -> validStacks.stream().filter(e::canEnchant).toList())
                .toList();

        for (int i = 0; i < allEnchantments.size(); i++) {
            Enchantment targetEnchantment = allEnchantments.get(i);

            List<ItemStack> exclusiveBooks = new ArrayList<>();
            for (Enchantment otherEnchantment : allEnchantments) {
                if (otherEnchantment != targetEnchantment && !targetEnchantment.isCompatibleWith(otherEnchantment)) {
                    exclusiveBooks.add(maxLevelBookCache.get(otherEnchantment));
                }
            }

            recipes.add(EnchantmentRecipeData.create(
                    targetEnchantment,
                    applicable.get(i),
                    exclusiveBooks,
                    maxLevelBookCache.get(targetEnchantment)
            ));
        }

        CACHED_RECIPES = List.copyOf(recipes);

        return CACHED_RECIPES;
    }

    public static void invalidateCache() {
        CACHED_RECIPES = null;
    }
}