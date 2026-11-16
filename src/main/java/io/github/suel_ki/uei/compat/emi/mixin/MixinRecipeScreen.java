//? if emi {
package io.github.suel_ki.uei.compat.emi.mixin;

import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.screen.RecipeScreen;
import dev.emi.emi.screen.WidgetGroup;
import io.github.suel_ki.uei.compat.emi.compat.IMouseEvents;
//? if >=26 {
import net.minecraft.client.input.MouseButtonEvent;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Pseudo
@Mixin(RecipeScreen.class)
public class MixinRecipeScreen {

    @Shadow(remap = false)
    private List<WidgetGroup> currentPage;

    //? if <1.21 {
    /*@Inject(method = "mouseScrolled(DDD)Z", at = @At("HEAD"), cancellable = true)
    private void uei_onMouseScrolled(double mouseX, double mouseY, double amount, CallbackInfoReturnable<Boolean> cir) {
    *///?} else {
    @Inject(method = "mouseScrolled(DDDD)Z", at = @At("HEAD"), cancellable = true)
    private void uei_onMouseScrolled(double mouseX, double mouseY, double horizontal, double amount, CallbackInfoReturnable<Boolean> cir) {
    //?}
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

    //? if >=26 {
    @Inject(method = "mouseDragged(Lnet/minecraft/client/input/MouseButtonEvent;DD)Z", at = @At("HEAD"), cancellable = true)
    private void uei_onMouseDragged(MouseButtonEvent event, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
    //?} else {
    /*@Inject(method = "mouseDragged(DDIDD)Z", at = @At("HEAD"), cancellable = true)
    private void uei_onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
    *///?}
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

    //? if >=26 {
    @Inject(method = "mouseReleased(Lnet/minecraft/client/input/MouseButtonEvent;)Z", at = @At("HEAD"))
    private void uei_onMouseReleased(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
    //?} else {
    /*@Inject(method = "mouseReleased(DDI)Z", at = @At("HEAD"))
    private void uei_onMouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
    *///?}
        if (currentPage == null) return;
        for (WidgetGroup group : currentPage) {
            double mx = mouseX - group.x();
            double my = mouseY - group.y();
            for (Widget widget : group.widgets) {
                if (widget instanceof IMouseEvents scrollable) {
                    scrollable.onMouseReleased(mx, my, button);
                }
            }
        }
    }
}
//?}
