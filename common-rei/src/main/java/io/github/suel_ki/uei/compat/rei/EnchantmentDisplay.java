package io.github.suel_ki.uei.compat.rei;

import io.github.suel_ki.uei.client.scroll.EnchantmentScrollContent;
import io.github.suel_ki.uei.config.Config;
import io.github.suel_ki.uei.ench.EnchantmentRecipeData;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class EnchantmentDisplay implements Display {
    private final EnchantmentRecipeData recipe;
    private final List<EntryIngredient> inputs;
    private final List<EntryIngredient> outputs;

    private final List<EntryIngredient> batchedApplicableIngredients;
    private final List<EntryIngredient> exclusiveIngredients;
    private final EntryIngredient bookIngredient;

    public EnchantmentDisplay(EnchantmentRecipeData recipe) {
        this.recipe = recipe;
        this.bookIngredient = EntryIngredients.ofItemStacks(recipe.allLevelBooks());

        this.exclusiveIngredients = recipe.exclusiveStacks().stream()
                .map(EntryIngredients::ofItemStacks)
                .toList();

        List<List<ItemStack>> batches = EnchantmentScrollContent.batchApplicableItems(recipe);
        this.batchedApplicableIngredients = batches.stream()
                .map(EntryIngredients::ofItemStacks)
                .toList();

        List<ItemStack> applicableStacks = recipe.applicableStacks();

        List<ItemStack> exclusiveStacks = recipe.exclusiveStacks().stream()
                .flatMap(List::stream)
                .toList();

        if (Config.get().lookupEnchantmentsByItem) {
            this.inputs = List.of(
                    EntryIngredients.ofItemStacks(exclusiveStacks),
                    EntryIngredients.ofItemStacks(applicableStacks)
            );
        } else {
            this.inputs = List.of(
                    EntryIngredients.ofItemStacks(exclusiveStacks)
            );
        }
        this.outputs = List.of(bookIngredient);
    }

    public EnchantmentRecipeData getRecipeData() {
        return recipe;
    }

    public EntryIngredient getBookIngredient() {
        return bookIngredient;
    }

    public List<EntryIngredient> getBatchedApplicableIngredients() {
        return batchedApplicableIngredients;
    }

    public List<EntryIngredient> getExclusiveIngredients() {
        return exclusiveIngredients;
    }

    public int getBatchCount() {
        return batchedApplicableIngredients.size();
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return inputs;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return outputs;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return ReiEnchantmentCategory.ID;
    }
}