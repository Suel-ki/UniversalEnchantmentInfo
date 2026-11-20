//? if rei {
//? if <=1.20.1 {
/*package io.github.suel_ki.uei.compat.rei.mixin;

import io.github.suel_ki.uei.compat.rei.ReiItemEnchantments;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Map;

@Pseudo
@Mixin(targets = "me.shedaniel.rei.impl.client.ClientHelperImpl")
public abstract class MixinClientHelper implements ReiItemEnchantments.DisplayOpener {
    @Shadow
    public abstract void openDisplayViewingScreen(Map<?, ?> categories, CategoryIdentifier<?> categoryId,
                                                   List<?> usages, List<?> results);

    @Override
    public void uei$open(Map<?, ?> categories, CategoryIdentifier<?> categoryId, List<?> usages, List<?> results) {
        openDisplayViewingScreen(categories, categoryId, usages, results);
    }
}
*///?}
//?}
