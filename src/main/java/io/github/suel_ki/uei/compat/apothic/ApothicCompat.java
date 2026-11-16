package io.github.suel_ki.uei.compat.apothic;

import io.github.suel_ki.uei.PlatformHelper;
import io.github.suel_ki.uei.config.Config;
import net.minecraft.world.item.enchantment.Enchantment;

public class ApothicCompat {
//? if <=1.20.1 {
    //private static final String APOTHIC_MOD_ID = "apotheosis";
//?} else {
    private static final String APOTHIC_MOD_ID = "apothic_enchanting";
//?}

    private static final boolean APOTHIC_LOADED = PlatformHelper.isModLoaded(APOTHIC_MOD_ID);

    public static int getRealMaxLevel(Enchantment enchantment) {
        //? if apothic {
        /*if (Config.get().apothicCompat && APOTHIC_LOADED) {
            return ApothicIntegration.getMaxLevel(enchantment);
        }
        *///?}
        return enchantment.getMaxLevel();
    }
}
