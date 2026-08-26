package io.github.suel_ki.uei.compat.jei;

import io.github.suel_ki.uei.Uei;
import io.github.suel_ki.uei.client.render.EnchantmentUIRenderer;
import io.github.suel_ki.uei.client.scroll.EnchantmentScrollContent;
import io.github.suel_ki.uei.compat.jei.widget.JeiEnchantmentScrollWidget;
import io.github.suel_ki.uei.ench.EnchantmentRecipeData;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnchantmentRecipeCategory implements IRecipeCategory<EnchantmentRecipeData> {
    public static final RecipeType<EnchantmentRecipeData> TYPE =
            RecipeType.create(Uei.MOD_ID, "ench_info", EnchantmentRecipeData.class);

    private final IDrawable icon;
    private final Component title;

    public EnchantmentRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, Items.ENCHANTED_BOOK.getDefaultInstance());
        this.title = Component.translatable("uei.ench_info");
    }

    @Override
    public @NotNull RecipeType<EnchantmentRecipeData> getRecipeType() {
        return TYPE;
    }

    @Override
    public int getWidth() {
        return EnchantmentUIRenderer.PANEL_WIDTH;
    }

    @Override
    public int getHeight() {
        return EnchantmentUIRenderer.PANEL_HEIGHT;
    }

    @Override
    public @NotNull Component getTitle() {
        return this.title;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EnchantmentRecipeData recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT,
                        EnchantmentUIRenderer.SLOT_X, EnchantmentUIRenderer.SLOT_Y)
                .addItemStacks(recipe.allLevelBooks())
                .setStandardSlotBackground();

        for (List<ItemStack> batch : EnchantmentScrollContent.batchApplicableItems(recipe)) {
            builder.addInputSlot()
                    .addItemStacks(batch)
                    .setStandardSlotBackground()
                    .setSlotName("applicable_slots");
        }

        for (List<ItemStack> exclusiveBooks : recipe.exclusiveStacks()) {
            builder.addInputSlot()
                    .addItemStacks(exclusiveBooks)
                    .setStandardSlotBackground()
                    .setSlotName("exclusive_books");
        }
    }

    @Override
    public void draw(EnchantmentRecipeData recipe, IRecipeSlotsView slots, GuiGraphics g, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        g.fill(0, EnchantmentUIRenderer.UPPER_HEIGHT, EnchantmentUIRenderer.PANEL_WIDTH,
                EnchantmentUIRenderer.UPPER_HEIGHT + 1, 0xFF555555);
        g.drawString(font, recipe.localizedName(),
                EnchantmentUIRenderer.NAME_X, EnchantmentUIRenderer.NAME_Y, -1);
        g.drawString(font, recipe.modName(),
                EnchantmentUIRenderer.MOD_NAME_X, EnchantmentUIRenderer.MOD_NAME_Y, -1, false);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, EnchantmentRecipeData recipe, IFocusGroup focuses) {
        List<IRecipeSlotDrawable> exclusives = builder.getRecipeSlots().getSlots().stream()
                .filter(s -> "exclusive_books".equals(s.getSlotName().orElse("")))
                .toList();

        List<IRecipeSlotDrawable> applicables = builder.getRecipeSlots().getSlots().stream()
                .filter(s -> "applicable_slots".equals(s.getSlotName().orElse("")))
                .toList();

        int panelWidth = EnchantmentUIRenderer.PANEL_WIDTH;
        int applicableSlotsPerRow = EnchantmentScrollContent.applicableSlotsPerRow(panelWidth, EnchantmentScrollContent.TRACK_WIDTH);

        JeiEnchantmentScrollWidget widget = new JeiEnchantmentScrollWidget(
                recipe, 0, EnchantmentUIRenderer.LOWER_START_Y,
                panelWidth,
                EnchantmentUIRenderer.PANEL_HEIGHT - EnchantmentUIRenderer.LOWER_START_Y,
                exclusives, applicables, applicables.size(), applicableSlotsPerRow
        );
        builder.addSlottedWidget(widget, exclusives);
        builder.addSlottedWidget(widget, applicables);
        builder.addInputHandler(widget);
    }
}
