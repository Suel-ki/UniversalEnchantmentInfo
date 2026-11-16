//? if <=1.20.1 {
/*package io.github.suel_ki.uei.ench;

import io.github.suel_ki.uei.compat.apothic.ApothicCompat;
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

        List<Enchantment> allEnchantments = BuiltInRegistries.ENCHANTMENT.stream()
                .filter(enchantment -> !EnchantmentBlacklist.isBlacklisted(BuiltInRegistries.ENCHANTMENT.getKey(enchantment)))
                .toList();

        List<ItemStack> validStacks = BuiltInRegistries.ITEM.stream()
                .map(Item::getDefaultInstance)
                .filter(stack -> !stack.isEmpty() && (stack.isEnchantable() || stack.getItem() == Items.BOOK))
                .toList();

        Map<Enchantment, List<ItemStack>> allLevelBooksCache = new HashMap<>(allEnchantments.size());
        for (Enchantment e : allEnchantments) {
            int maxLvl = ApothicCompat.getRealMaxLevel(e);
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
*///?} else {
package io.github.suel_ki.uei.ench;

//? if apothic {
//import io.github.suel_ki.uei.compat.apothic.ApothicCompat;
//?}
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
//? if >=1.21.4 {
import net.minecraft.core.component.DataComponents;
//?}
import net.minecraft.core.registries.Registries;
//? if 1.21.1 {
//import net.minecraft.world.item.EnchantedBookItem;
//?}
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if >=1.21.4 {
import net.minecraft.world.item.Items;
//?}
import net.minecraft.world.item.enchantment.Enchantment;
//? if 1.21.1 {
//import net.minecraft.world.item.enchantment.EnchantmentInstance;
//?} else {
import net.minecraft.world.item.enchantment.ItemEnchantments;
//?}

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentDataFactory {

    private static List<EnchantmentRecipeData> CACHED_RECIPES = null;

//? if >=1.21.4 {
    public static ItemStack createEnchantedBook(Holder<Enchantment> enchantment, int level) {
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        ItemEnchantments.Mutable storedEnchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        storedEnchantments.set(enchantment, level);
        book.set(DataComponents.STORED_ENCHANTMENTS, storedEnchantments.toImmutable());
        return book;
    }

//?}
    public static List<EnchantmentRecipeData> getOrComputeRecipes() {
        if (CACHED_RECIPES != null) {
            return CACHED_RECIPES;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return List.of();
        }

        RegistryAccess registryAccess = mc.level.registryAccess();
        //? if 1.21.1 {
        /*Registry<Enchantment> enchantmentRegistry = registryAccess.registryOrThrow(Registries.ENCHANTMENT);

        List<Holder.Reference<Enchantment>> allEnchantments = enchantmentRegistry.holders().toList();
        *///?} else {
        Registry<Enchantment> enchantmentRegistry = registryAccess.lookupOrThrow(Registries.ENCHANTMENT);

        List<Holder.Reference<Enchantment>> allEnchantments = enchantmentRegistry.listElements().toList();
        //?}

        allEnchantments = allEnchantments.stream()
                .filter(holder -> !EnchantmentBlacklist.isBlacklisted(holder.key().identifier()))
                .toList();

        Map<Enchantment, List<ItemStack>> allLevelBooksCache = new HashMap<>(allEnchantments.size());
        for (Holder.Reference<Enchantment> e : allEnchantments) {
            //? if apothic {
            //int maxLvl = ApothicCompat.getRealMaxLevel(e.value());
            //?} else {
            int maxLvl = e.value().getMaxLevel();
            //?}
            List<ItemStack> books = new ArrayList<>(maxLvl);
            for (int lvl = 1; lvl <= maxLvl; lvl++) {
                //? if 1.21.1 {
                //books.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(e, lvl)));
                //?} else {
                books.add(createEnchantedBook(e, lvl));
                //?}
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
//?}
