//? if rei {
package io.github.suel_ki.uei.compat.rei;

import io.github.suel_ki.uei.ench.EnchantmentRecipeData;
//? if <=1.20.1 {
//import me.shedaniel.rei.api.client.ClientHelper;
//import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
//import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
//import me.shedaniel.rei.api.common.category.CategoryIdentifier;
//import me.shedaniel.rei.api.common.display.Display;
//?}
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.item.ItemStack;

import java.util.List;
//? if <=1.20.1 {
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//?}

public class ReiItemEnchantments {
    //? if <=1.20.1 {
    /*public interface DisplayOpener {
        void uei$open(Map<?, ?> categories, CategoryIdentifier<?> categoryId, List<?> usages, List<?> results);
    }
    *///?}

    public static boolean open(List<EnchantmentRecipeData> recipes) {
        //? if <=1.20.1 {
        /*ReiEnchantmentCategory category = REIPlugin.category();
        ClientHelper helper = ClientHelper.getInstance();

        if (category != null && helper instanceof DisplayOpener opener) {
            List<EnchantmentDisplay> registered = DisplayRegistry.getInstance().get(ReiEnchantmentCategory.ID);
            List<Display> displays = new ArrayList<>(recipes.size());
            for (EnchantmentRecipeData recipe : recipes) {
                for (EnchantmentDisplay candidate : registered) {
                    if (candidate.data() == recipe) {
                        displays.add(candidate);
                        break;
                    }
                }
            }

            if (!displays.isEmpty()) {
                Map<DisplayCategory<?>, List<Display>> map = new HashMap<>(1);
                map.put(category, displays);
                opener.uei$open(map, ReiEnchantmentCategory.ID, List.of(), List.of());
                return true;
            }
        }
        *///?}

        ViewSearchBuilder builder = ViewSearchBuilder.builder()
                .setPreferredOpenedCategory(ReiEnchantmentCategory.ID)
                .mergingDisplays(true);

        int added = 0;
        for (EnchantmentRecipeData recipe : recipes) {
            EntryStack<ItemStack> book = EntryStacks.of(recipe.enchantedBook());
            if (book != null) {
                builder.addRecipesFor(book);
                added++;
            }
        }

        return added > 0 && builder.open();
    }
}
//?}
