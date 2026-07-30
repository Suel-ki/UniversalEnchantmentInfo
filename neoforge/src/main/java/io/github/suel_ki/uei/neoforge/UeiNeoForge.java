package io.github.suel_ki.uei.neoforge;

import io.github.suel_ki.uei.Uei;
import io.github.suel_ki.uei.client.screen.ConfigScreen;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.NotNull;

@Mod(Uei.MOD_ID)
public class UeiNeoForge {
    public UeiNeoForge() {
        Uei.init();
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
                () -> new IConfigScreenFactory() {
                    @Override
                    public @NotNull Screen createScreen(@NotNull ModContainer modContainer, @NotNull Screen parent) {
                        return new ConfigScreen(parent);
                    }
                });
    }
}
