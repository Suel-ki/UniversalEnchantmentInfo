//? if forge {
/*package io.github.suel_ki.uei.forge;

import io.github.suel_ki.uei.Uei;
import io.github.suel_ki.uei.client.ItemEnchantmentKeyHandler;
import io.github.suel_ki.uei.client.screen.ConfigScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(Uei.MOD_ID)
public class UeiForge {
    public UeiForge() {
        Uei.init();
        MinecraftForge.EVENT_BUS.addListener(UeiForge::onScreenKey);
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (mc, parent) -> new ConfigScreen(parent))
        );
    }

    private static void onScreenKey(ScreenEvent.KeyPressed.Pre event) {
        Screen screen = event.getScreen();
        boolean shift = (event.getModifiers() & 1) != 0;
        if (ItemEnchantmentKeyHandler.onKeyPressed(screen, event.getKeyCode(), shift)) {
            event.setCanceled(true);
        }
    }
}
*///?}
