package io.github.suel_ki.uei.ench;

import io.github.suel_ki.uei.config.Config;
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

        int maxItems = Config.get().maxApplicableItems;

        List<Item> validItems = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            ItemStack tempStack = item.getDefaultInstance();
            if (!tempStack.isEmpty() && (tempStack.isEnchantable() || item == Items.BOOK)) {
                validItems.add(item);
            }
        }

        Map<Enchantment, ItemStack> maxLevelBookCache = new HashMap<>();
        for (Enchantment e : BuiltInRegistries.ENCHANTMENT) {
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantmentHelper.setEnchantments(Map.of(e, e.getMaxLevel()), book);
            maxLevelBookCache.put(e, book);
        }

        Map<Enchantment, List<Item>> applicableMap = new HashMap<>();
        for (Enchantment enchantment : BuiltInRegistries.ENCHANTMENT) {
            List<Item> applicableList = new ArrayList<>();

            for (Item item : validItems) {
                if (applicableList.size() >= maxItems) {
                    break;
                }
                ItemStack testStack = item.getDefaultInstance();
                if (enchantment.canEnchant(testStack)) {
                    applicableList.add(item);
                }
            }
            applicableMap.put(enchantment, applicableList);
        }

        Map<Enchantment, List<ItemStack>> exclusiveMap = new HashMap<>();
        for (Enchantment targetEnchantment : BuiltInRegistries.ENCHANTMENT) {
            List<ItemStack> exclusiveBooks = new ArrayList<>();

            for (Enchantment otherEnchantment : BuiltInRegistries.ENCHANTMENT) {
                if (otherEnchantment != targetEnchantment && !targetEnchantment.isCompatibleWith(otherEnchantment)) {
                    exclusiveBooks.add(maxLevelBookCache.get(otherEnchantment));
                }
            }
            exclusiveMap.put(targetEnchantment, exclusiveBooks);
        }

        CACHED_RECIPES = BuiltInRegistries.ENCHANTMENT.stream()
                .map(enchantment -> EnchantmentRecipeData.create(
                        enchantment,
                        applicableMap.getOrDefault(enchantment, List.of()),
                        exclusiveMap.getOrDefault(enchantment, List.of())
                ))
                .toList();

        return CACHED_RECIPES;
    }

    public static void invalidateCache() {
        CACHED_RECIPES = null;
    }
}