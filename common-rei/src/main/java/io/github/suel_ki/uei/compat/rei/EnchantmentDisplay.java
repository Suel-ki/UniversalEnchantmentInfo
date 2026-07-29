package io.github.suel_ki.uei.compat.rei;

import io.github.suel_ki.uei.ench.EnchantmentRecipeData;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.List;

public class EnchantmentDisplay implements Display {
    private final EnchantmentRecipeData recipe;
    private final List<EntryIngredient> inputs;
    private final List<EntryIngredient> outputs;

    public EnchantmentDisplay(EnchantmentRecipeData recipe) {
        this.recipe = recipe;
        this.inputs = List.of(EntryIngredients.ofItemStacks(recipe.exclusiveStacks()));
        this.outputs = List.of(EntryIngredients.ofItemStacks(recipe.allLevelBooks()));
    }

    public EnchantmentRecipeData getRecipeData() {
        return recipe;
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
