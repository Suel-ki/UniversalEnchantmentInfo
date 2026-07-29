package io.github.suel_ki.uei.compat.rei;

import io.github.suel_ki.uei.ench.EnchantmentDataFactory;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.item.Items;

public class REIPlugin implements REIClientPlugin {
    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new ReiEnchantmentCategory());
        registry.addWorkstations(ReiEnchantmentCategory.ID, EntryStacks.of(Items.ENCHANTED_BOOK));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        for (var recipe : EnchantmentDataFactory.getOrComputeRecipes()) {
            registry.add(new EnchantmentDisplay(recipe));
        }
    }

}
