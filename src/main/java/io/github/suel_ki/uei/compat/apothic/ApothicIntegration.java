package io.github.suel_ki.uei.compat.apothic;

//? if apothic && neoforge {
//import dev.shadowsoffire.apothic_enchanting.asm.EnchHooks;
//?} elif apothic && forge {
//import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
//?}

import net.minecraft.world.item.enchantment.Enchantment;

public class ApothicIntegration {
    public static int getMaxLevel(Enchantment enchantment) {
        //? if apothic && (neoforge || forge) {
        //return EnchHooks.getMaxLevel(enchantment);
        //?} else {
        return enchantment.getMaxLevel();
        //?}
    }
}
