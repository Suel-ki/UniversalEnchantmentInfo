package io.github.suel_ki.uei.compat;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.enchantment.Enchantment;

public class ApotheosisIntegration {

    @ExpectPlatform
    public static int getMaxLevel(Enchantment enchantment) {
        throw new AssertionError();
    }
}