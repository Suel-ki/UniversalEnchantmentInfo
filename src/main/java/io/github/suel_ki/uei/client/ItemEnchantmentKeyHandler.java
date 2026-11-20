package io.github.suel_ki.uei.client;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.suel_ki.uei.PlatformHelper;
import io.github.suel_ki.uei.config.Config;
import io.github.suel_ki.uei.ench.EnchantmentRecipeData;
import io.github.suel_ki.uei.ench.ItemEnchantmentLookup;
//? if jei {
import io.github.suel_ki.uei.compat.jei.JeiItemEnchantments;
//?}
//? if emi {
import io.github.suel_ki.uei.compat.emi.EmiItemEnchantments;
//?}
//? if rei {
import io.github.suel_ki.uei.compat.rei.ReiItemEnchantments;
//?}
//? if rrv {
import io.github.suel_ki.uei.compat.rrv.RrvItemEnchantments;
//?}
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ItemEnchantmentKeyHandler {
    public static boolean onKeyPressed(Screen screen, int key, boolean shift) {
        if (!Config.get().lookupItemEnchantments) {
            return false;
        }

        if (key != InputConstants.KEY_V || !shift || screen instanceof ChatScreen) {
            return false;
        }

        ItemStack stack = ScreenItemSlot.hovered(screen);
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        List<EnchantmentRecipeData> recipes = ItemEnchantmentLookup.of(stack);
        return !recipes.isEmpty() && open(screen, stack, recipes);
    }

    private static boolean open(Screen screen, ItemStack stack, List<EnchantmentRecipeData> recipes) {
        //? if jei {
        if (PlatformHelper.isModLoaded("jei") && JeiItemEnchantments.open(recipes)) {
            return true;
        }
        //?}
        //? if emi {
        if (PlatformHelper.isModLoaded("emi") && EmiItemEnchantments.open(recipes)) {
            return true;
        }
        //?}
        //? if rei {
        if (PlatformHelper.isModLoaded("roughlyenoughitems") && ReiItemEnchantments.open(recipes)) {
            return true;
        }
        //?}
        //? if rrv {
        if (PlatformHelper.isModLoaded("rrv") && RrvItemEnchantments.open(screen, stack, recipes)) {
            return true;
        }
        //?}
        return false;
    }
}
