package io.github.suel_ki.uei.ench;

import io.github.suel_ki.uei.config.Config;
import net.minecraft.resources.Identifier;

public class EnchantmentBlacklist {

    public static boolean isBlacklisted(Identifier id) {
        if (id == null) {
            return false;
        }

        Config config = Config.get();
        String enchantmentId = id.toString();

        for (String entry : config.blacklistedMods) {
            if (entry != null && entry.strip().equals(id.getNamespace())) {
                return true;
            }
        }

        for (String entry : config.blacklistedEnchantments) {
            if (entry != null && entry.strip().equals(enchantmentId)) {
                return true;
            }
        }

        return false;
    }
}
