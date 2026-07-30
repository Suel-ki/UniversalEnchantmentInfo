package io.github.suel_ki.uei.compat.rei;

import io.github.suel_ki.uei.Uei;
import io.github.suel_ki.uei.ench.EnchantmentRecipeData;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class EnchantmentDisplay implements Display {
    private final EnchantmentRecipeData recipe;
    private final List<EntryIngredient> inputs;
    private final List<EntryIngredient> outputs;
    private final Identifier id;

    public EnchantmentDisplay(EnchantmentRecipeData recipe) {
        this.recipe = recipe;
        this.inputs = List.of(EntryIngredients.ofItemStacks(recipe.exclusiveStacks()));
        this.outputs = List.of(EntryIngredients.ofItemStacks(recipe.allLevelBooks()));
        this.id = recipe.enchantment().unwrapKey()
                .map(key -> {
                    Identifier enchId = key.identifier();
                    return Identifier.fromNamespaceAndPath(
                            Uei.MOD_ID,
                            String.format("/%s/%s", enchId.getNamespace(), enchId.getPath())
                    );
                })
                .orElse(null);
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

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.ofNullable(id);
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return null;
    }
}
