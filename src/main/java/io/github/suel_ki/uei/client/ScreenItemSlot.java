package io.github.suel_ki.uei.client;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ScreenItemSlot {
    public static ItemStack hovered(Screen screen) {
        if (!(screen instanceof AbstractContainerScreen<?> container)) {
            return null;
        }

        //? if fabric {
        Slot slot = container.hoveredSlot;
        //?} elif >=26 {
        //Slot slot = container.getHoveredSlot();
        //?} else {
        //Slot slot = container.getSlotUnderMouse();
        //?}

        return slot != null && slot.hasItem() ? slot.getItem() : null;
    }
}
