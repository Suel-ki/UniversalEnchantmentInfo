package io.github.suel_ki.uei.compat.rei;

import io.github.suel_ki.uei.Uei;
import io.github.suel_ki.uei.client.render.EnchantmentUIRenderer;
import io.github.suel_ki.uei.client.scroll.EnchantmentScrollContent;
import io.github.suel_ki.uei.compat.rei.widget.ReiEnchantmentScrollWidget;
import io.github.suel_ki.uei.ench.EnchantmentRecipeData;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ReiEnchantmentCategory implements DisplayCategory<EnchantmentDisplay> {
    public static final CategoryIdentifier<EnchantmentDisplay> ID = CategoryIdentifier.of(Uei.MOD_ID, "ench_info");

    @Override
    public CategoryIdentifier<? extends EnchantmentDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("uei.ench_info");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(net.minecraft.world.item.Items.ENCHANTED_BOOK);
    }

    @Override
    public int getDisplayWidth(EnchantmentDisplay display) {
        return EnchantmentUIRenderer.PANEL_WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return EnchantmentUIRenderer.PANEL_HEIGHT;
    }

    @Override
    public List<Widget> setupDisplay(EnchantmentDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        EnchantmentRecipeData recipe = display.getRecipeData();
        int sx = bounds.x;
        int sy = bounds.y;

        widgets.add(Widgets.createRecipeBase(bounds));

        int margin = 3;
        int topOff = sy + margin;
        widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> {
            Font font = Minecraft.getInstance().font;
            graphics.fill(sx + margin, topOff + EnchantmentUIRenderer.UPPER_HEIGHT,
                    sx + bounds.width - margin,
                    topOff + EnchantmentUIRenderer.UPPER_HEIGHT + 1, 0xFF555555);
            graphics.drawString(font, recipe.localizedName(),
                    sx + EnchantmentUIRenderer.NAME_X + margin, topOff + EnchantmentUIRenderer.NAME_Y, -1);
            graphics.drawString(font, recipe.modName(),
                    sx + EnchantmentUIRenderer.MOD_NAME_X + margin, topOff + EnchantmentUIRenderer.MOD_NAME_Y, -1, false);
        }));

        widgets.add(Widgets.createSlot(new Point(sx + EnchantmentUIRenderer.SLOT_X + margin, topOff + EnchantmentUIRenderer.SLOT_Y))
                .entries(EntryIngredients.of(recipe.enchantedBook()))
                .disableBackground()
                .markOutput());

        int lowerY = topOff + EnchantmentUIRenderer.LOWER_START_Y;
        int lowerH = bounds.height - (lowerY - sy) - margin;
        Rectangle scrollBounds = new Rectangle(sx + margin, lowerY, bounds.width - margin * 2, lowerH);

        int applicableSlotsPerRow = EnchantmentScrollContent.applicableSlotsPerRow(
                bounds.width - margin * 2, 8);

        List<Slot> applicableSlots = new ArrayList<>();
        List<Slot> exclusiveSlots = new ArrayList<>();

        List<List<ItemStack>> batches = EnchantmentScrollContent.batchApplicableItems(recipe);
        for (List<ItemStack> batch : batches) {
            Slot slot = Widgets.createSlot(new Point(0, 0))
                    .entries(EntryIngredients.ofItemStacks(batch));
            applicableSlots.add(slot);
        }

        for (ItemStack exclusiveBook : recipe.exclusiveStacks()) {
            Slot slot = Widgets.createSlot(new Point(0, 0))
                    .entries(EntryIngredients.of(exclusiveBook))
                    .markInput();
            exclusiveSlots.add(slot);
        }

        widgets.add(new ReiEnchantmentScrollWidget(recipe, scrollBounds, exclusiveSlots, applicableSlots,
                batches.size(), applicableSlotsPerRow));
        return widgets;
    }
}
