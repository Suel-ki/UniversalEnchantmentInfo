//? if jei {
package io.github.suel_ki.uei.compat.jei;

import io.github.suel_ki.uei.Uei;
import io.github.suel_ki.uei.client.render.EnchantmentUIRenderer;
import io.github.suel_ki.uei.client.scroll.EnchantmentScrollContent;
import io.github.suel_ki.uei.compat.jei.widget.JeiEnchantmentScrollWidget;
import io.github.suel_ki.uei.config.Config;
import io.github.suel_ki.uei.ench.EnchantmentRecipeData;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
//? if <1.21.4 {
//import mezz.jei.api.recipe.RecipeType;
//?} else {
import mezz.jei.api.recipe.types.IRecipeType;
//?}
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentRecipeCategory implements IRecipeCategory<EnchantmentRecipeData> {
    //? if <1.21.4 {
    /*public static final RecipeType<EnchantmentRecipeData> TYPE =
            RecipeType.create(Uei.MOD_ID, "ench_info", EnchantmentRecipeData.class);
    *///?} else {
    public static final IRecipeType<EnchantmentRecipeData> TYPE =
            IRecipeType.create(Uei.MOD_ID, "ench_info", EnchantmentRecipeData.class);
    //?}

    private final IDrawable icon;
    private final Component title;

    public EnchantmentRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, Items.ENCHANTED_BOOK.getDefaultInstance());
        this.title = Component.translatable("uei.ench_info");
    }

    //? if <1.21.4 {
    /*@Override
    public @NotNull RecipeType<EnchantmentRecipeData> getRecipeType() {
        return TYPE;
    }
    *///?} else {
    @Override
    public @NotNull IRecipeType<EnchantmentRecipeData> getRecipeType() {
        return TYPE;
    }
    //?}

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
        addOutputSlot(builder, EnchantmentUIRenderer.SLOT_X, EnchantmentUIRenderer.SLOT_Y)
                .addItemStacks(recipe.allLevelBooks())
                .setStandardSlotBackground();

        for (List<ItemStack> batch : EnchantmentScrollContent.batchApplicableItems(recipe)) {
            if (Config.get().lookupEnchantmentsByItem) {
                addInputSlot(builder)
                        .addItemStacks(batch)
                        .setStandardSlotBackground()
                        .setSlotName("applicable_slots");
            } else {
                builder.addSlot(RecipeIngredientRole.RENDER_ONLY)
                        .addItemStacks(batch)
                        .setStandardSlotBackground()
                        .setSlotName("applicable_slots");
            }
        }

        for (List<ItemStack> exclusiveBooks : recipe.exclusiveStacks()) {
            addInputSlot(builder)
                    .addItemStacks(exclusiveBooks)
                    .setStandardSlotBackground()
                    .setSlotName("exclusive_books");
        }
    }

    private static IRecipeSlotBuilder addInputSlot(IRecipeLayoutBuilder builder) {
        //? if >=1.21.4 <1.21.11 {
        /*return builder.addSlot(RecipeIngredientRole.INPUT);
        *///?} else {
        return builder.addInputSlot();
        //?}
    }

    private static IRecipeSlotBuilder addOutputSlot(IRecipeLayoutBuilder builder, int x, int y) {
        //? if >=1.21.4 <1.21.11 {
        /*return builder.addSlot(RecipeIngredientRole.OUTPUT, x, y);
        *///?} else {
        return builder.addOutputSlot(x, y);
        //?}
    }

    @Override
    public void draw(EnchantmentRecipeData recipe, IRecipeSlotsView slots, GuiGraphicsExtractor g, double mouseX, double mouseY) {
        Font font = Minecraft.getInstance().font;
        g.fill(0, EnchantmentUIRenderer.UPPER_HEIGHT, EnchantmentUIRenderer.PANEL_WIDTH,
                EnchantmentUIRenderer.UPPER_HEIGHT + 1, 0xFF555555);
        g.text(font, recipe.localizedName(),
                EnchantmentUIRenderer.NAME_X, EnchantmentUIRenderer.NAME_Y, -1);
        g.text(font, recipe.modName(),
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
        List<IRecipeSlotDrawable> slots = new ArrayList<>(exclusives);
        slots.addAll(applicables);

        builder.addSlottedWidget(widget, slots);
        builder.addInputHandler(widget);
    }
}
//?}
