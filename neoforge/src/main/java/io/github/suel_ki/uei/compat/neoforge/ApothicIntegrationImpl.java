package io.github.suel_ki.uei.compat.neoforge;

import dev.shadowsoffire.apothic_enchanting.asm.EnchHooks;
import net.minecraft.world.item.enchantment.Enchantment;

public class ApothicIntegrationImpl {

    public static int getMaxLevel(Enchantment enchantment) {
        return EnchHooks.getMaxLevel(enchantment);
    }
}
