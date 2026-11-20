//? if fabric || neoforge {
package io.github.suel_ki.uei.mixin;

import io.github.suel_ki.uei.client.ItemEnchantmentKeyHandler;
//? if >=1.21.10 {
import net.minecraft.client.input.KeyEvent;
//?}
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class MixinScreenKey {
    //? if >=1.21.10 {
    @Inject(method = "keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z", at = @At("HEAD"), cancellable = true)
    private void uei$keyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (ItemEnchantmentKeyHandler.onKeyPressed((Screen) (Object) this, event.key(), event.hasShiftDown())) {
            cir.setReturnValue(true);
        }
    }
    //?} else {
    //@Inject(method = "keyPressed(III)Z", at = @At("HEAD"), cancellable = true)
    //private void uei$keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
    //    if (ItemEnchantmentKeyHandler.onKeyPressed((Screen) (Object) this, keyCode, Screen.hasShiftDown())) {
    //        cir.setReturnValue(true);
    //    }
    //}
    //?}
}
//?}
