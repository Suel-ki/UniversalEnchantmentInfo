package io.github.suel_ki.uei.fabric;

import io.github.suel_ki.uei.Uei;
import net.fabricmc.api.ModInitializer;

public class UeiFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Uei.init();
    }
}
