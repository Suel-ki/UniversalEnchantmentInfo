//? if rrv {
package io.github.suel_ki.uei.compat.rrv;

import cc.cassian.rrv.api.ActionType;
import cc.cassian.rrv.api.recipe.ItemView;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import io.github.suel_ki.uei.ench.EnchantmentRecipeData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu.setScreen;

public class RrvItemEnchantments {
    public static boolean open(Screen screen, ItemStack origin, List<EnchantmentRecipeData> recipes) {
        List<ReliableClientRecipe> registered = new ItemView().getRecipes(EnchantmentClientRecipeType.ID);
        List<ReliableClientRecipe> displays = new ArrayList<>(recipes.size());
        for (EnchantmentRecipeData recipe : recipes) {
            for (ReliableClientRecipe candidate : registered) {
                if (candidate instanceof EnchantmentClientRecipe wrapper && wrapper.data() == recipe) {
                    displays.add(wrapper);
                    break;
                }
            }
        }

        if (displays.isEmpty()) {
            for (EnchantmentRecipeData recipe : recipes) {
                displays.add(new EnchantmentClientRecipe(recipe));
            }
        }

        if (displays.isEmpty()) {
            return false;
        }

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }

        RecipeViewMenu menu = new RecipeViewMenu(screen, 0, player.getInventory(), displays, origin,
                ActionType.RESULT, new ArrayList<>(), EnchantmentClientRecipeType.INSTANCE);
        setScreen(player, menu);
        return true;
    }
}
//?}
