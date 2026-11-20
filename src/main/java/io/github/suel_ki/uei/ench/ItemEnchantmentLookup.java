package io.github.suel_ki.uei.ench;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
//? if >=1.21 {
import net.minecraft.core.Holder;
//?}
//? if <=1.20.1 {
//import net.minecraft.world.item.enchantment.EnchantmentHelper;
//?}

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class ItemEnchantmentLookup {
    private static List<EnchantmentRecipeData> indexedRecipes;
    private static Map<Enchantment, EnchantmentRecipeData> index;

    public static List<EnchantmentRecipeData> of(ItemStack stack) {
        List<EnchantmentRecipeData> recipes = EnchantmentDataFactory.getOrComputeRecipes();
        if (recipes.isEmpty()) {
            return List.of();
        }

        Map<Enchantment, EnchantmentRecipeData> lookup = index(recipes);
        List<EnchantmentRecipeData> result = new ArrayList<>(4);

        //? if <=1.20.1 {
        /*for (Enchantment enchantment : EnchantmentHelper.getEnchantments(stack).keySet()) {
            EnchantmentRecipeData recipe = lookup.get(enchantment);
            if (recipe != null) {
                result.add(recipe);
            }
        }
        *///?} else {
        for (Holder<Enchantment> enchantment : stack.getEnchantments().keySet()) {
            EnchantmentRecipeData recipe = lookup.get(enchantment.value());
            if (recipe != null) {
                result.add(recipe);
            }
        }
        //?}

        return result;
    }

    private static Map<Enchantment, EnchantmentRecipeData> index(List<EnchantmentRecipeData> recipes) {
        if (indexedRecipes == recipes && index != null) {
            return index;
        }

        Map<Enchantment, EnchantmentRecipeData> lookup = new IdentityHashMap<>(recipes.size());

        //? if <=1.20.1 {
        /*for (EnchantmentRecipeData recipe : recipes) {
            lookup.put(recipe.enchantment(), recipe);
        }
        *///?} else {
        for (EnchantmentRecipeData recipe : recipes) {
            lookup.put(recipe.enchantment().value(), recipe);
        }
        //?}

        indexedRecipes = recipes;
        index = lookup;
        return lookup;
    }
}
