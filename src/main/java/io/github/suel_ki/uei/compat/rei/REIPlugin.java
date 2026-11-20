//? if rei {
package io.github.suel_ki.uei.compat.rei;

import io.github.suel_ki.uei.ench.EnchantmentDataFactory;
import io.github.suel_ki.uei.ench.EnchantmentRecipeData;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.item.Items;

public class REIPlugin implements REIClientPlugin {
    //? if <=1.20.1 {
    /*private static ReiEnchantmentCategory CATEGORY;

    public static ReiEnchantmentCategory category() {
        return CATEGORY;
    }
    *///?}

    @Override
    public void registerCategories(CategoryRegistry registry) {
        ReiEnchantmentCategory category = new ReiEnchantmentCategory();
        //? if <=1.20.1 {
        //CATEGORY = category;
        //?}
        registry.add(category);
        registry.addWorkstations(ReiEnchantmentCategory.ID, EntryStacks.of(Items.ENCHANTED_BOOK));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        for (EnchantmentRecipeData recipe : EnchantmentDataFactory.getOrComputeRecipes()) {
            registry.add(new EnchantmentDisplay(recipe));
        }
    }
}
//?}
