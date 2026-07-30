package io.github.suel_ki.uei.neoforge;

import net.minecraft.client.resources.language.I18n;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class PlatformHelperImpl {

    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static String getModName(String modId) {
        String translationKey = String.format("modmenu.nameTranslation.%s", modId);

        if (I18n.exists(translationKey)) {
            return I18n.get(translationKey);
        }

        return ModList.get().getModContainerById(modId)
                .map(container -> container.getModInfo().getDisplayName())
                .orElseGet(() -> capitalize(modId));
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
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
