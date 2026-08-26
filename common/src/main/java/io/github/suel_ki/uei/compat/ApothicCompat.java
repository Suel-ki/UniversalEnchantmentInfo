package io.github.suel_ki.uei.compat;

import io.github.suel_ki.uei.PlatformHelper;
import io.github.suel_ki.uei.config.Config;
import net.minecraft.world.item.enchantment.Enchantment;

public class ApothicCompat {
    private static final boolean APOTHIC_LOADED = PlatformHelper.isModLoaded("apothic_enchanting");

    public static int getRealMaxLevel(Enchantment enchantment) {
        if (Config.get().apothicCompat && APOTHIC_LOADED) {
            return ApothicIntegration.getMaxLevel(enchantment);
        }
        return enchantment.getMaxLevel();
    }
}
