package io.github.suel_ki.uei.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class EnchantmentRecipeCategory extends EmiRecipeCategory {
    public EnchantmentRecipeCategory(ResourceLocation id, EmiRenderable icon) {
        super(id, icon);
    }

    @Override
    public Component getName() {
        return Component.translatable("uei.ench_info");
    }
}
