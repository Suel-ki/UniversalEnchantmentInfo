package io.github.suel_ki.uei.compat.rrv;

import cc.cassian.rrv.api.overlay.ButtonData;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import io.github.suel_ki.uei.Uei;
import io.github.suel_ki.uei.client.render.EnchantmentUIRenderer;
import io.github.suel_ki.uei.compat.rrv.widget.EnchantmentItemGrid;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class EnchantmentClientRecipeType implements ReliableClientRecipeType {

    public static final EnchantmentClientRecipeType INSTANCE = new EnchantmentClientRecipeType();
    public static final Identifier ID = Identifier.fromNamespaceAndPath(Uei.MOD_ID, "ench_info");

    private static final int SHARE_BUTTON_OFFSET = 22;
    private static final int SHARE_BUTTON_GAP = 2;

    private static int shareButtonX() {
        return EnchantmentUIRenderer.PANEL_WIDTH - SHARE_BUTTON_OFFSET;
    }

    public static int headerMaxX() {
        return shareButtonX() - SHARE_BUTTON_GAP;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("uei.ench_info");
    }

    @Override
    public int getDisplayWidth() {
        return EnchantmentUIRenderer.PANEL_WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return EnchantmentUIRenderer.PANEL_HEIGHT;
    }

    @Override
    public Identifier getGuiTexture() {
        return Identifier.fromNamespaceAndPath(Uei.MOD_ID, "textures/gui/rrv_ench.png");
    }

    @Override
    public int getSlotCount() {
        return 1;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.addItemSlot(0, EnchantmentUIRenderer.SLOT_X + EnchantmentItemGrid.CONTENT_INSET,
                EnchantmentUIRenderer.SLOT_Y + EnchantmentItemGrid.CONTENT_INSET);
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.ENCHANTED_BOOK);
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(new ItemStack(Items.ENCHANTED_BOOK));
    }

    @Override
    public ReferenceCondition getCraftReferenceCondition() {
        return (craftReference, clientRecipe) -> clientRecipe instanceof EnchantmentClientRecipe recipe
                && recipe.isCraftReference(craftReference);
    }

    @Override
    public ButtonData placeRecipeShareButton(RecipeViewMenu.DisplayInfo info) {
        return new ButtonData(info.guiLeft() + shareButtonX(), info.guiTop() + EnchantmentItemGrid.CONTENT_INSET, true);
    }
}
