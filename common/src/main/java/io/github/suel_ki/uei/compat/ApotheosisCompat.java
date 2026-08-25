package io.github.suel_ki.uei.compat;

import io.github.suel_ki.uei.PlatformHelper;
import io.github.suel_ki.uei.config.Config;
import net.minecraft.world.item.enchantment.Enchantment;

public class ApotheosisCompat {
    private static final boolean APOTHIC_LOADED = PlatformHelper.isModLoaded("apotheosis");

    public static int getRealMaxLevel(Enchantment enchantment) {
        if (Config.get().apotheosisCompat && APOTHIC_LOADED) {
            return ApotheosisIntegration.getMaxLevel(enchantment);
        }
        return enchantment.getMaxLevel();
    }
}

