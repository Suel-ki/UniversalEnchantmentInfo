package io.github.suel_ki.uei.compat.fabric;

import net.minecraft.world.item.enchantment.Enchantment;

public class ApotheosisIntegrationImpl {
    public static int getMaxLevel(Enchantment enchantment) {
        return enchantment.getMaxLevel();
    }
}
