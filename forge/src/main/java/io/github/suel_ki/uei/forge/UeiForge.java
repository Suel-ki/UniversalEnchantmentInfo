package io.github.suel_ki.uei.forge;

import io.github.suel_ki.uei.Uei;
import io.github.suel_ki.uei.client.screen.ConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(Uei.MOD_ID)
public class UeiForge {
    public UeiForge() {
        Uei.init();
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (mc, parent) -> new ConfigScreen(parent))
        );
    }
}
