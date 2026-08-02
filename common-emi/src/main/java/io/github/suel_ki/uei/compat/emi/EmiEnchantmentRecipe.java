package io.github.suel_ki.uei.compat.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.WidgetHolder;
import io.github.suel_ki.uei.client.render.EnchantmentUIRenderer;
import io.github.suel_ki.uei.client.scroll.EnchantmentScrollContent;
import io.github.suel_ki.uei.compat.emi.widget.EmiEnchantmentScrollWidget;
import io.github.suel_ki.uei.compat.emi.widget.ScrollSlotWidget;
import io.github.suel_ki.uei.ench.EnchantmentRecipeData;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class EmiEnchantmentRecipe implements EmiRecipe {
    private final EnchantmentRecipeData recipe;
    private final Identifier id;

    private final List<EmiIngredient> inputs;
    private final List<EmiStack> outputs;

    private final List<EmiIngredient> applicableSlotIngredients;

    public EmiEnchantmentRecipe(Identifier id, EnchantmentRecipeData recipe) {
        this.recipe = recipe;
        this.id = id;
        List<Holder<Item>> applicableHolders = recipe.applicableItems();
        List<EmiStack> applicableEmiStacks = applicableHolders.stream()
                .map(holder -> EmiStack.of(holder.value()))
                .toList();

        List<EmiStack> exclusiveEmiStacks = recipe.exclusiveStacks().stream().map(EmiStack::of).toList();

        this.inputs = Stream.concat(
                exclusiveEmiStacks.stream().<EmiIngredient>map(s -> s),
                applicableEmiStacks.stream().<EmiIngredient>map(s -> s)
        ).toList();

        this.outputs = recipe.allLevelBooks().stream().map(EmiStack::of).toList();

        Map<Item, EmiStack> itemToEmi = new IdentityHashMap<>();
        for (int i = 0; i < applicableHolders.size(); i++) {
            itemToEmi.put(applicableHolders.get(i).value(), applicableEmiStacks.get(i));
        }

        this.applicableSlotIngredients = EnchantmentScrollContent.batchApplicableItems(recipe).stream()
                .map(batch -> EmiIngredient.of(batch.stream()
                        .map(stack -> itemToEmi.getOrDefault(stack.getItem(), EmiStack.of(stack)))
                        .toList()))
                .toList();
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return EMIPlugin.ENCHANTMENT_CATEGORY;
    }

    @Override
    public @Nullable Identifier getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return inputs;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return outputs;
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
    public void addWidgets(WidgetHolder widgets) {
        widgets.addDrawable(0, EnchantmentUIRenderer.UPPER_HEIGHT,
                getDisplayWidth(), 1,
                (g, mouseX, mouseY, delta) -> g.fill(0, 0, getDisplayWidth(), 1, 0xFF555555));

        widgets.addText(recipe.localizedName(),
                EnchantmentUIRenderer.NAME_X, EnchantmentUIRenderer.NAME_Y, -1, true);
        widgets.addText(recipe.modName().copy(),
                EnchantmentUIRenderer.MOD_NAME_X, EnchantmentUIRenderer.MOD_NAME_Y, -1, false);

        widgets.addSlot(EmiStack.of(recipe.enchantedBook()),
                        EnchantmentUIRenderer.SLOT_X, EnchantmentUIRenderer.SLOT_Y)
                .recipeContext(this);

        int lowerX = 0;
        int lowerY = EnchantmentUIRenderer.LOWER_START_Y;
        int lowerW = getDisplayWidth();
        int lowerH = Math.max(1, Math.min(getDisplayHeight(), widgets.getHeight()) - lowerY);

        int aspacing = EnchantmentUIRenderer.APPLICABLE_SLOT_SPACING;
        int sbWidth = EnchantmentScrollContent.TRACK_WIDTH;
        int slotsPerRow = EnchantmentScrollContent.applicableSlotsPerRow(lowerW, sbWidth);
        int batchCount = applicableSlotIngredients.size();

        EmiEnchantmentScrollWidget scrollWidget = new EmiEnchantmentScrollWidget(recipe, lowerX, lowerY, lowerW, lowerH,
                batchCount, slotsPerRow);
        Bounds scrollArea = scrollWidget.getBounds();

        widgets.add(scrollWidget);

        int aiy = EnchantmentScrollContent.applicableItemStartY(recipe);

        for (int i = 0; i < batchCount; i++) {
            int col = i % slotsPerRow;
            int row = i / slotsPerRow;
            int sx = lowerX + EnchantmentUIRenderer.PADDING + col * aspacing;
            int sy = lowerY + aiy + row * aspacing;

            widgets.add(new ScrollSlotWidget(applicableSlotIngredients.get(i), sx, sy,
                    scrollWidget.scrollAmountSupplier(), scrollArea));
        }

        int exclusiveCount = recipe.exclusiveStacks().size();
        if (exclusiveCount > 0) {
            int applicableRows = EnchantmentScrollContent.calculateRows(batchCount, slotsPerRow);
            int ciy = EnchantmentScrollContent.exclusiveSlotsStartY(aiy, applicableRows);
            int spacing = EnchantmentUIRenderer.EXCLUSIVE_SLOT_SPACING;
            int exclusiveSlotsPerRow = EnchantmentScrollContent.exclusiveSlotsPerRow(lowerW, sbWidth);

            for (int i = 0; i < exclusiveCount; i++) {
                int col = i % exclusiveSlotsPerRow;
                int row = i / exclusiveSlotsPerRow;
                EmiIngredient ingredient = EmiStack.of(recipe.exclusiveStacks().get(i));
                int sx = lowerX + EnchantmentUIRenderer.PADDING + col * spacing;
                int sy = lowerY + ciy + row * spacing;

                widgets.add(new ScrollSlotWidget(ingredient, sx, sy,
                        scrollWidget.scrollAmountSupplier(), scrollArea));
            }
        }
    }
}
