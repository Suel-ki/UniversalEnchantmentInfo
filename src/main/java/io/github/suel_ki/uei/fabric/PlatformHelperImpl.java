//? if fabric {
package io.github.suel_ki.uei.fabric;

import net.fabricmc.loader.api.FabricLoader;
//? if <26.2 {
//import net.minecraft.client.resources.language.I18n;
//?} else {
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
//?}

import java.nio.file.Path;

public class PlatformHelperImpl {

    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static String getModName(String modId) {
        String translationKey = String.format("modmenu.nameTranslation.%s", modId);

        //? if <26.2 {
        /*if (I18n.exists(translationKey)) {
            return I18n.get(translationKey);
        }
        *///?} else {
        if (Language.getInstance().has(translationKey)) {
            return Component.translatable(translationKey).getString();
        }
        //?}

        return FabricLoader.getInstance().getModContainer(modId)
                .map(container -> container.getMetadata().getName())
                .orElseGet(() -> capitalize(modId));
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        String[] words = str.split("_");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1));
            }
        }

        return result.toString();
    }
}
//?}
