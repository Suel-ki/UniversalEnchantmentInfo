package io.github.suel_ki.uei.compat.rrv;

import cc.cassian.rrv.api.ReliableRecipeViewerClientPlugin;
import cc.cassian.rrv.api.recipe.ItemView;
import io.github.suel_ki.uei.ench.EnchantmentDataFactory;

public class RRVPlugin implements ReliableRecipeViewerClientPlugin {

    @Override
    public void onIntegrationInitialize() {
        ItemView.addClientRecipeProvider(recipeList -> {
            EnchantmentDataFactory.getOrComputeRecipes()
                    .forEach(recipe -> recipeList.add(new EnchantmentClientRecipe(recipe)));
        });
    }
}
