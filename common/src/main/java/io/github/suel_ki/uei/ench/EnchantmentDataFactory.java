package io.github.suel_ki.uei.ench;

import io.github.suel_ki.uei.compat.ApothicCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentDataFactory {

    private static List<EnchantmentRecipeData> CACHED_RECIPES = null;

    public static ItemStack createEnchantedBook(Holder<Enchantment> enchantment, int level) {
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable storedEnchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        storedEnchantments.set(enchantment, level);
        book.set(DataComponents.STORED_ENCHANTMENTS, storedEnchantments.toImmutable());
        return book;
    }

    public static List<EnchantmentRecipeData> getOrComputeRecipes() {
        if (CACHED_RECIPES != null) {
            return CACHED_RECIPES;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return List.of();
        }

        RegistryAccess registryAccess = mc.level.registryAccess();
        Registry<Enchantment> enchantmentRegistry = registryAccess.lookupOrThrow(Registries.ENCHANTMENT);

        List<Holder.Reference<Enchantment>> allEnchantments = enchantmentRegistry.listElements().toList();

        Map<Enchantment, List<ItemStack>> allLevelBooksCache = new HashMap<>(allEnchantments.size());
        for (Holder.Reference<Enchantment> e : allEnchantments) {
            int maxLvl = ApothicCompat.getRealMaxLevel(e.value());
            List<ItemStack> books = new ArrayList<>(maxLvl);
            for (int lvl = 1; lvl <= maxLvl; lvl++) {
                books.add(createEnchantedBook(e, lvl));
            }
            allLevelBooksCache.put(e.value(), books);
        }

        List<EnchantmentRecipeData> recipes = new ArrayList<>(allEnchantments.size());

        for (Holder.Reference<Enchantment> targetEnchantment : allEnchantments) {
            if (!targetEnchantment.isBound()) continue;
            Enchantment enchantment = targetEnchantment.value();

            List<Holder<Item>> applicableList = enchantment.getSupportedItems().stream().toList();

            List<List<ItemStack>> exclusiveBooks = new ArrayList<>();
            for (Holder<Enchantment> otherEnchantment : enchantment.exclusiveSet()) {
                if (!otherEnchantment.isBound()) continue;

                if (!otherEnchantment.is(targetEnchantment.key())) {
                    List<ItemStack> books = allLevelBooksCache.get(otherEnchantment.value());
                    if (books != null) {
                        exclusiveBooks.add(books);
                    }
                }
            }

            List<ItemStack> targetBooks = allLevelBooksCache.get(enchantment);
            ItemStack maxLevelBook = targetBooks.getLast();

            recipes.add(EnchantmentRecipeData.create(
                    targetEnchantment,
                    applicableList,
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