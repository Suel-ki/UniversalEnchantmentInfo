package io.github.suel_ki.uei.compat.emi.mixin;

import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.screen.RecipeScreen;
import dev.emi.emi.screen.WidgetGroup;
import io.github.suel_ki.uei.compat.emi.compat.IMouseEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/*
 * Adapted from AdvancedLootInfo
 * Original source: https://github.com/yanny7/AdvancedLootInfo/blob/master/ali/common-emi/src/main/java/com/yanny/ali/emi/mixin/MixinRecipeScreen.java
 * Original Copyright (c) Yanny
 * Licensed under the MIT License.
 */
@Pseudo
@Mixin(RecipeScreen.class)
public class MixinRecipeScreen {

    @Shadow(remap = false)
    private List<WidgetGroup> currentPage;

    @Inject(method = "mouseScrolled(DDDD)Z", at = @At("HEAD"), cancellable = true)
    private void uei_onMouseScrolled(double mouseX, double mouseY, double horizontal, double amount, CallbackInfoReturnable<Boolean> cir) {
        if (currentPage == null) return;
        for (WidgetGroup group : currentPage) {
            double mx = mouseX - group.x();
            double my = mouseY - group.y();
            for (Widget widget : group.widgets) {
                if (widget instanceof IMouseEvents scrollable) {
                    if (scrollable.onMouseScrolled(mx, my, amount)) {
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }
        }
    }

    @Inject(method = "mouseDragged(DDIDD)Z", at = @At("HEAD"), cancellable = true)
    private void uei_onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
        if (currentPage == null) return;
        for (WidgetGroup group : currentPage) {
            double mx = mouseX - group.x();
            double my = mouseY - group.y();
            for (Widget widget : group.widgets) {
                if (widget instanceof IMouseEvents scrollable) {
                    if (scrollable.onMouseDragged(mx, my, button, deltaX, deltaY)) {
                        cir.setReturnValue(true);
                        return;
                    }
                }
            }
        }
    }
}
