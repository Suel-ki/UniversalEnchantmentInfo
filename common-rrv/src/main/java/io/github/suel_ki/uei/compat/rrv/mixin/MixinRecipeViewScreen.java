package io.github.suel_ki.uei.compat.rrv.mixin;

import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewScreen;
import io.github.suel_ki.uei.compat.rrv.EnchantmentClientRecipe;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(RecipeViewScreen.class)
public class MixinRecipeViewScreen {

    @Inject(method = "mouseScrolled(DDDD)Z", at = @At("HEAD"), cancellable = true)
    private void uei_onMouseScrolled(double mouseX, double mouseY, double horizontal, double amount, CallbackInfoReturnable<Boolean> cir) {
        RecipeViewScreen screen = (RecipeViewScreen) (Object) this;

        for (ReliableClientRecipe recipe : screen.getMenu().getCurrentDisplay()) {
            if (recipe instanceof EnchantmentClientRecipe enchantmentRecipe
                    && enchantmentRecipe.handleMouseScrolled(mouseX, mouseY, amount)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }

    @Inject(method = "keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z", at = @At("HEAD"), cancellable = true)
    private void uei_onKeyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        RecipeViewScreen screen = (RecipeViewScreen) (Object) this;

        for (ReliableClientRecipe recipe : screen.getMenu().getCurrentDisplay()) {
            if (recipe instanceof EnchantmentClientRecipe enchantmentRecipe
                    && enchantmentRecipe.handleKeyPressed(event)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
