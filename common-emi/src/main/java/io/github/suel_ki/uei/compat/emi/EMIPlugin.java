package io.github.suel_ki.uei.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import io.github.suel_ki.uei.Uei;
import io.github.suel_ki.uei.ench.EnchantmentDataFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

@EmiEntrypoint
public class EMIPlugin implements EmiPlugin {
    public static final EmiRecipeCategory ENCHANTMENT_CATEGORY = new EnchantmentRecipeCategory(
            ResourceLocation.fromNamespaceAndPath(Uei.MOD_ID, "ench_info"), EmiStack.of(Items.ENCHANTED_BOOK));

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(ENCHANTMENT_CATEGORY);

        registry.addWorkstation(EMIPlugin.ENCHANTMENT_CATEGORY, EmiStack.of(Items.ENCHANTED_BOOK));

        for (var recipe : EnchantmentDataFactory.getOrComputeRecipes()) {
            recipe.enchantment().unwrapKey().ifPresent(key -> {
                ResourceLocation id = key.location();

                ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(
                        Uei.MOD_ID,
                        String.format("/%s/%s", id.getNamespace(), id.getPath())
                );

                registry.addRecipe(new EmiEnchantmentRecipe(recipeId, recipe));
            });
        }
    }
}
