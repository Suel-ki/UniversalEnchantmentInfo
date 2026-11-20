//? if emi {
package io.github.suel_ki.uei.compat.emi;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.screen.RecipeScreen;
import io.github.suel_ki.uei.ench.EnchantmentRecipeData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmiItemEnchantments {
    public static boolean open(List<EnchantmentRecipeData> recipes) {
        List<EmiRecipe> registered = EmiApi.getRecipeManager().getRecipes(EMIPlugin.ENCHANTMENT_CATEGORY);
        List<EmiRecipe> selected = new ArrayList<>(recipes.size());

        for (EnchantmentRecipeData data : recipes) {
            for (EmiRecipe candidate : registered) {
                if (candidate instanceof EmiEnchantmentRecipe wrapper && wrapper.data() == data) {
                    selected.add(wrapper);
                    break;
                }
            }
        }

        if (selected.isEmpty()) {
            return false;
        }

        Map<EmiRecipeCategory, List<EmiRecipe>> map = new HashMap<>(1);
        map.put(EMIPlugin.ENCHANTMENT_CATEGORY, selected);

        Minecraft minecraft = Minecraft.getInstance();
        //? if >=26.2 {
        minecraft.setScreenAndShow(new RecipeScreen(EmiApi.getHandledScreen(), map));
        //?} else {
        //minecraft.setScreen(new RecipeScreen(EmiApi.getHandledScreen(), map));
        //?}
        return true;
    }
}
//?}
