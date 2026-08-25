package io.github.suel_ki.uei.ench;

import io.github.suel_ki.uei.compat.ApotheosisCompat;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

        List<Enchantment> allEnchantments = BuiltInRegistries.ENCHANTMENT.stream().toList();

        List<ItemStack> validStacks = BuiltInRegistries.ITEM.stream()
                .map(Item::getDefaultInstance)
                .filter(stack -> !stack.isEmpty() && (stack.isEnchantable() || stack.getItem() == Items.BOOK))
                .toList();

        Map<Enchantment, List<ItemStack>> allLevelBooksCache = new HashMap<>(allEnchantments.size());
        for (Enchantment e : allEnchantments) {
            int maxLvl = ApotheosisCompat.getRealMaxLevel(e);
            List<ItemStack> books = new ArrayList<>(Math.max(1, maxLvl));
            for (int lvl = 1; lvl <= maxLvl; lvl++) {
                books.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(e, lvl)));
            }
            allLevelBooksCache.put(e, books);
        }

        List<EnchantmentRecipeData> recipes = new ArrayList<>(allEnchantments.size());

        for (int i = 0; i < allEnchantments.size(); i++) {
            Enchantment targetEnchantment = allEnchantments.get(i);
            List<ItemStack> applicable = validStacks.stream()
                    .filter(targetEnchantment::canEnchant)
                    .toList();

            List<List<ItemStack>> exclusiveBooks = new ArrayList<>();
            for (Enchantment otherEnchantment : allEnchantments) {
                if (otherEnchantment != targetEnchantment && !targetEnchantment.isCompatibleWith(otherEnchantment)) {
                    List<ItemStack> books = allLevelBooksCache.get(otherEnchantment);
                    if (books != null && !books.isEmpty()) {
                        exclusiveBooks.add(books);
                    }
                }
            }

            List<ItemStack> targetBooks = allLevelBooksCache.get(targetEnchantment);
            ItemStack maxLevelBook = (targetBooks != null && !targetBooks.isEmpty())
                    ? targetBooks.get(targetBooks.size() - 1)
                    : ItemStack.EMPTY;

            recipes.add(EnchantmentRecipeData.create(
                    targetEnchantment,
                    applicable,
                    exclusiveBooks,
                    maxLevelBook
            ));
        }

        CACHED_RECIPES = List.copyOf(recipes);

        return CACHED_RECIPES;
    }

    public static void invalidateCache() {
        CACHED_RECIPES = null;
    }
}