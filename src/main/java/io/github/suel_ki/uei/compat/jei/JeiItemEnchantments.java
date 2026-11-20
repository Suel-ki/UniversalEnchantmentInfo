//? if jei {
package io.github.suel_ki.uei.compat.jei;

import io.github.suel_ki.uei.ench.EnchantmentRecipeData;
import mezz.jei.api.runtime.IJeiRuntime;

import java.util.List;

public class JeiItemEnchantments {
    public static boolean open(List<EnchantmentRecipeData> recipes) {
        IJeiRuntime runtime = JEIPlugin.runtime();
        EnchantmentRecipeCategory category = JEIPlugin.category();
        if (runtime == null || category == null || recipes.isEmpty()) {
            return false;
        }

        runtime.getRecipesGui().showRecipes(category, recipes, List.of());
        return true;
    }
}
//?}
