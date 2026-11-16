package io.github.suel_ki.uei;

//? if fabric {
import io.github.suel_ki.uei.fabric.PlatformHelperImpl;
//?} elif forge {
//import io.github.suel_ki.uei.forge.PlatformHelperImpl;
//?} elif neoforge {
//import io.github.suel_ki.uei.neoforge.PlatformHelperImpl;
//?}

import java.nio.file.Path;

public class PlatformHelper {

    public static Path getConfigDirectory() {
        return PlatformHelperImpl.getConfigDirectory();
    }

    public static String getModName(String modId) {
        return PlatformHelperImpl.getModName(modId);
    }

    public static boolean isModLoaded(String modId) {
        return PlatformHelperImpl.isModLoaded(modId);
    }
}
