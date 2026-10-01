package io.github.suel_ki.uei.compat.rrv;

import cc.cassian.rrv.api.client.RecipeScreenContext;
import cc.cassian.rrv.api.recipe.ReliableClientRecipe;
import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import cc.cassian.rrv.common.recipe.inventory.SlotContent;
import io.github.suel_ki.uei.Uei;
import io.github.suel_ki.uei.client.render.EnchantmentUIRenderer;
import io.github.suel_ki.uei.client.scroll.EnchantmentScrollContent;
import io.github.suel_ki.uei.compat.rrv.widget.EnchantmentItemGrid;
import io.github.suel_ki.uei.compat.rrv.widget.RrvEnchantmentScrollWidget;
import io.github.suel_ki.uei.config.Config;
import io.github.suel_ki.uei.ench.EnchantmentRecipeData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentClientRecipe implements ReliableClientRecipe {

    private static final int CYCLE_TICKS = 25;

    private final EnchantmentRecipeData recipe;
    private final SlotContent book;
    private final List<SlotContent> applicableSlots;
    private final List<SlotContent> exclusiveSlots;
    private final RrvEnchantmentScrollWidget widget;
    private final Identifier id;

    private int cycleTicks;

    public EnchantmentClientRecipe(EnchantmentRecipeData recipe) {
        Font font = Minecraft.getInstance().font;
        int slotAreaWidth = EnchantmentUIRenderer.PANEL_WIDTH - 2 * EnchantmentItemGrid.CONTENT_INSET;

        this.recipe = recipe;
        this.book = SlotContent.of(recipe.allLevelBooks());
        this.applicableSlots = EnchantmentScrollContent.batchApplicableItems(recipe).stream()
                .map(SlotContent::of)
                .toList();
        this.exclusiveSlots = recipe.exclusiveStacks().stream()
                .map(SlotContent::of)
                .toList();

        int applicableSlotsPerRow = EnchantmentScrollContent.applicableSlotsPerRow(slotAreaWidth, EnchantmentScrollContent.TRACK_WIDTH);
        int exclusiveSlotsPerRow = EnchantmentScrollContent.exclusiveSlotsPerRow(slotAreaWidth, EnchantmentScrollContent.TRACK_WIDTH);
        int applicableRows = EnchantmentScrollContent.calculateRows(applicableSlots.size(), applicableSlotsPerRow);
        int exclusiveRows = EnchantmentScrollContent.calculateRows(exclusiveSlots.size(), exclusiveSlotsPerRow);

        this.widget = new RrvEnchantmentScrollWidget(recipe, font, applicableSlots, exclusiveSlots,
                applicableSlotsPerRow, exclusiveSlotsPerRow, applicableRows, exclusiveRows);

        this.id = recipe.enchantment().unwrapKey()
                .map(key -> Identifier.fromNamespaceAndPath(
                        Uei.MOD_ID,
                        String.format("/%s/%s", key.identifier().getNamespace(), key.identifier().getPath())))
                .orElse(null);
    }

    @Override
    public ReliableClientRecipeType getType() {
        return EnchantmentClientRecipeType.INSTANCE;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public void bindSlots(RecipeViewMenu.SlotFillContext slotFillContext) {
        slotFillContext.bindSlot(0, book);
    }

    @Override
    public List<SlotContent> getIngredients() {
        List<SlotContent> ingredients = new ArrayList<>(exclusiveSlots);

        if (Config.get().lookupEnchantmentsByItem) {
            ingredients.addAll(applicableSlots);
        }

        return ingredients;
    }

    @Override
    public List<SlotContent> getResults() {
        return List.of(book);
    }

    @Override
    public boolean isVisualOnly() {
        return true;
    }

    public boolean isCraftReference(ItemStack stack) {
        if (!stack.is(Items.ENCHANTED_BOOK)) {
            return false;
        }

        ItemEnchantments stored = stack.get(DataComponents.STORED_ENCHANTMENTS);

        if (stored == null || stored.isEmpty()) {
            return true;
        }

        return exclusiveSlots.stream()
                .flatMap(slot -> slot.getValidContents().stream())
                .anyMatch(enchantedBook -> ItemStack.isSameItemSameComponents(stack, enchantedBook));
    }

    @Override
    public void tick() {
        if (++cycleTicks < CYCLE_TICKS) {
            return;
        }

        cycleTicks = 0;
        applicableSlots.forEach(SlotContent::next);
        exclusiveSlots.forEach(SlotContent::next);
    }

    @Override
    public void addRecipeWidgets(RecipeScreenContext context) {
        widget.updateLayoutMetrics();
        widget.setPanelOrigin(context.recipePosition().left(), context.recipePosition().top());
        context.widgets().addRecipeWidget(widget);
    }

    @Override
    public void renderRecipe(RecipeScreenContext context) {
        GuiGraphics g = context.guiGraphics();

        if (g == null) {
            return;
        }

        Font font = context.font();
        int inset = EnchantmentItemGrid.CONTENT_INSET;

        g.pose().pushMatrix();
        g.pose().translate((float) (EnchantmentUIRenderer.SLOT_X - 1 + inset),
                (float) (EnchantmentUIRenderer.SLOT_Y - 1 + inset));
        RecipeViewMenu.OptionalSlotRenderer.DEFAULT.extractRenderState(g, context.absoluteMouseX(), context.absoluteMouseY(), context.partialTicks());
        g.pose().popMatrix();

        g.fill(inset, EnchantmentUIRenderer.UPPER_HEIGHT, EnchantmentUIRenderer.PANEL_WIDTH - inset,
                EnchantmentUIRenderer.UPPER_HEIGHT + 1, 0xFF555555);
        int headerMaxX = EnchantmentClientRecipeType.headerMaxX();
        EnchantmentScrollContent.renderScrollingString(g, font, recipe.localizedName(),
                EnchantmentUIRenderer.NAME_X + inset, EnchantmentUIRenderer.NAME_Y + inset, headerMaxX,
                EnchantmentUIRenderer.NAME_Y + inset + font.lineHeight, -1, true);
        g.drawString(font, recipe.modName(),
                EnchantmentUIRenderer.MOD_NAME_X + inset, EnchantmentUIRenderer.MOD_NAME_Y + inset, -1, false);

        widget.setPanelOrigin(context.recipePosition().left(), context.recipePosition().top());
    }

    public boolean handleMouseScrolled(double mouseX, double mouseY, double amount) {
        return widget.handleMouseScrolled(mouseX, mouseY, amount);
    }

    public boolean handleKeyPressed(KeyEvent event) {
        return widget.handleKeyPressed(event);
    }
}
