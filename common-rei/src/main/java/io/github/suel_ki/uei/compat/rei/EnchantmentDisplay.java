package io.github.suel_ki.uei.compat.rei;

import io.github.suel_ki.uei.Uei;
import io.github.suel_ki.uei.client.scroll.EnchantmentScrollContent;
import io.github.suel_ki.uei.ench.EnchantmentRecipeData;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class EnchantmentDisplay implements Display {
    private final EnchantmentRecipeData recipe;
    private final List<EntryIngredient> inputs;
    private final List<EntryIngredient> outputs;
    private final List<EntryIngredient> batchedApplicableIngredients;
    private final List<EntryIngredient> exclusiveIngredients;
    private final EntryIngredient bookIngredient;
    private final ResourceLocation id;

    public EnchantmentDisplay(EnchantmentRecipeData recipe) {
        this.recipe = recipe;
        this.bookIngredient = EntryIngredients.of(recipe.enchantedBook());

        this.exclusiveIngredients = recipe.exclusiveStacks().stream()
                .map(EntryIngredients::of)
                .toList();

        List<List<ItemStack>> batches = EnchantmentScrollContent.batchApplicableItems(recipe);
        this.batchedApplicableIngredients = batches.stream()
                .map(EntryIngredients::ofItemStacks)
                .toList();

        List<ItemStack> allApplicableStacks = recipe.applicableItems().stream()
                .map(holder -> new ItemStack(holder.value()))
                .toList();

        this.inputs = List.of(
                EntryIngredients.ofItemStacks(recipe.exclusiveStacks()),
                EntryIngredients.ofItemStacks(allApplicableStacks)
        );
        this.outputs = List.of(EntryIngredients.ofItemStacks(recipe.allLevelBooks()));
        this.id = recipe.enchantment().unwrapKey()
                .map(key -> {
                    ResourceLocation enchId = key.registry();
                    return ResourceLocation.fromNamespaceAndPath(
                            Uei.MOD_ID,
                            String.format("/%s/%s", enchId.getNamespace(), enchId.getPath())
                    );
                })
                .orElse(null);
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

    @Override
    public Optional<ResourceLocation> getDisplayLocation() {
        return Optional.ofNullable(id);
    }

    @Override
    public @Nullable DisplaySerializer<? extends Display> getSerializer() {
        return null;
    }
}
