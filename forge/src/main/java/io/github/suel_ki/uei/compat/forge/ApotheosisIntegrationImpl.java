package io.github.suel_ki.uei.compat.forge;

import dev.shadowsoffire.apotheosis.ench.asm.EnchHooks;
import net.minecraft.world.item.enchantment.Enchantment;

public class ApotheosisIntegrationImpl {
    public static int getMaxLevel(Enchantment enchantment) {
        return EnchHooks.getMaxLevel(enchantment);
    }
}
