package io.github.suel_ki.uei.client.screen;

import io.github.suel_ki.uei.Uei;
import io.github.suel_ki.uei.config.Config;
import io.github.suel_ki.uei.config.ConfigSpec;
import io.github.suel_ki.uei.ench.EnchantmentDataFactory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;

public class ConfigScreen extends Screen {
    private final Screen parent;
    private Button doneButton;
    private ConfigList list;
    private final List<ConfigList.Entry> entries = new ArrayList<>();

    public ConfigScreen(Screen parent) {
        super(Component.translatable("uei.config.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int listY = 33;
        int listBottom = this.height - 35;
        int listHeight = listBottom - listY;
        this.list = new ConfigList(this.minecraft, this.width, listHeight, listY, 30);
        this.addRenderableWidget(this.list);

        if (this.entries.isEmpty()) {
            Config cfg = Config.get();
            Config def = new Config();
            for (Field f : Config.class.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers()) || f.getAnnotation(ConfigSpec.class) == null) continue;
                f.setAccessible(true);
                this.entries.add(new ConfigList.Entry(this, f, cfg, def, this.font, this::updateButtonValidity));
            }
        }

        for (ConfigList.Entry entry : this.entries) {
            this.list.addEntry(entry);
        }

        int buttonY = this.height - 26;
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, btn -> this.onClose())
                .bounds(this.width / 2 - 155, buttonY, 150, 20).build());

        this.doneButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, btn -> {
            boolean cacheChanged = this.list.children().stream()
                    .anyMatch(e -> e.spec != null && e.spec.impactsCache() && e.hasChanged());
            this.list.children().forEach(e -> e.save(Config.get()));
            Config.save();

            if (cacheChanged) {
                EnchantmentDataFactory.invalidateCache();
            }
            this.onClose();
        }).bounds(this.width / 2 + 5, buttonY, 150, 20).build());

        updateButtonValidity();
    }

    private void updateButtonValidity() {
        if (this.doneButton != null) {
            this.doneButton.active = this.list.children().stream().allMatch(e -> e.valid);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFFFF);

        if (this.list.children().stream().anyMatch(e -> e.spec != null && e.spec.impactsCache() && e.valid && e.hasChanged())) {
            graphics.drawCenteredString(this.font, Component.translatable("uei.config.reload_notice"), this.width / 2, this.height - 34, 0xFFAAAAAA);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    private static class ConfigList extends ContainerObjectSelectionList<ConfigList.Entry> {
        public ConfigList(Minecraft mc, int width, int height, int y, int itemHeight) {
            super(mc, width, height, y, itemHeight);
        }

        public int addEntry(Entry entry) {
            return super.addEntry(entry);
        }

        @Override
        public int getRowWidth() {
            return 340;
        }

        @Override
        protected int scrollBarX() {
            return this.width / 2 + 160;
        }

        public static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
            final Field field;
            final ConfigSpec spec;
            final String translationKey;
            final boolean isBool;
            final boolean isColor;
            final boolean isList;

            String currentValStr;
            final String initialValStr;
            final String defaultValStr;

            List<Object> currentList;
            final List<Object> initialList;
            final List<Object> defaultList;

            boolean valid = true;
            int colorPreview;

            private final AbstractWidget valueWidget;
            private final Button resetButton;
            private final Component label;
            private final Font font;

            Entry(Screen parentScreen, Field field, Config cfg, Config def, Font font, Runnable onChange) {
                this.field = field;
                this.spec = field.getAnnotation(ConfigSpec.class);
                this.translationKey = "uei.config." + field.getName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
                this.isBool = field.getType() == boolean.class;
                this.isList = List.class.isAssignableFrom(field.getType());
                this.isColor = !isBool && !isList && spec != null && spec.isColor();
                this.font = font;
                this.label = Component.translatable(this.translationKey).withStyle(ChatFormatting.WHITE);

                Object curObj = null;
                Object defObj = null;
                try {
                    curObj = field.get(cfg);
                    defObj = field.get(def);
                } catch (Exception e) {
                    Uei.LOGGER.error("Failed to read config field: {}", field.getName(), e);
                }

                if (isList) {
                    this.initialValStr = "";
                    this.defaultValStr = "";
                    this.initialList = curObj instanceof List<?> l ? new ArrayList<>(l) : new ArrayList<>();
                    this.defaultList = defObj instanceof List<?> l ? new ArrayList<>(l) : new ArrayList<>();
                    this.currentList = new ArrayList<>(this.initialList);
                } else {
                    this.initialValStr = String.valueOf(curObj);
                    this.defaultValStr = String.valueOf(defObj);
                    this.initialList = null;
                    this.defaultList = null;
                }

                this.currentValStr = this.initialValStr;

                if (isColor) {
                    try { this.colorPreview = 0xFF000000 | Integer.parseInt(this.currentValStr); } catch (Exception ignored) {}
                }

                if (isList) {
                    this.valueWidget = Button.builder(Component.literal("Edit..."), btn -> {
                        Minecraft.getInstance().setScreenAndShow(new ListEditScreen(parentScreen, this.label, this.currentList, newList -> {
                            this.currentList = new ArrayList<>(newList);

                            try {
                                this.field.set(cfg, new ArrayList<>(this.currentList));
                            } catch (Exception e) {
                                Uei.LOGGER.error("Failed to set config field: {}", field.getName(), e);
                            }

                            this.updateResetButton();
                            onChange.run();
                        }));
                    }).bounds(0, 0, 80, 20).build();
                } else if (isBool) {
                    this.valueWidget = CycleButton.onOffBuilder(Boolean.parseBoolean(this.currentValStr))
                            .displayOnlyValue()
                            .create(0, 0, 80, 20, Component.empty(), (btn, val) -> {
                                this.currentValStr = String.valueOf(val);
                                this.updateResetButton();
                                onChange.run();
                            });
                } else {
                    EditBox box = new EditBox(font, 0, 0, 80, 20, Component.empty());
                    box.setValue(this.currentValStr);
                    box.setResponder(s -> {
                        this.currentValStr = s;
                        this.valid = checkValidity();
                        if (isColor && this.valid) {
                            try {
                                int v = Integer.parseInt(s);
                                this.colorPreview = 0xFF000000 | (v & 0xFFFFFF);
                            } catch (NumberFormatException ignored) {}
                        }
                        box.setTextColor(this.valid ? 0xFFE0E0E0 : 0xFFFF5555);
                        this.updateResetButton();
                        onChange.run();
                    });
                    this.valueWidget = box;
                }

                this.resetButton = Button.builder(Component.translatable("uei.config.reset"), btn -> {
                    this.reset();
                    onChange.run();
                }).bounds(0, 0, 40, 20).build();

                this.updateResetButton();
            }

            private boolean isEquivalentToDefault() {
                if (!this.valid) return false;
                if (isList) return this.currentList.equals(this.defaultList);
                return areEquivalent(this.currentValStr, this.defaultValStr);
            }

            private void updateResetButton() {
                if (this.resetButton != null) {
                    this.resetButton.active = !this.isEquivalentToDefault();
                }
            }

            @Override
            public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
                int x = this.getX();
                int y = this.getY();
                int width = this.getWidth();
                int height = this.getHeight();

                if (isMouseOver) {
                    graphics.fill(x, y - 2, x + width, y + height - 2, 0x1AFFFFFF);
                }

                graphics.drawString(this.font, this.label, x + width / 2 - 10 - this.font.width(this.label), y + 6, 0xFFFFFFFF);

                this.valueWidget.setY(y);
                this.valueWidget.setX(x + width / 2 + 10);
                this.resetButton.setY(y);
                this.resetButton.setX(x + width / 2 + (isColor ? 104 : 92));

                this.valueWidget.render(graphics, mouseX, mouseY, partialTick);
                this.resetButton.render(graphics, mouseX, mouseY, partialTick);

                if (isColor && valid) {
                    int cx = x + width / 2 + 93;
                    int cy = y + 5;
                    graphics.fill(cx, cy, cx + 10, cy + 10, colorPreview);
                }

                if (this.valueWidget.isMouseOver(mouseX, mouseY) && !isList) {
                    Component tooltip = getTooltip();
                    if (tooltip != null) graphics.setTooltipForNextFrame(this.font, tooltip, mouseX, mouseY);
                }
            }

            @Override
            public @NotNull List<? extends GuiEventListener> children() {
                return List.of(this.valueWidget, this.resetButton);
            }

            @Override
            public @NotNull List<? extends NarratableEntry> narratables() {
                return List.of(this.valueWidget, this.resetButton);
            }

            boolean checkValidity() {
                if (isBool || isList) return true;
                try {
                    double v;
                    if (field.getType() == int.class) v = Integer.parseInt(this.currentValStr);
                    else if (field.getType() == float.class) v = Float.parseFloat(this.currentValStr);
                    else if (field.getType() == double.class) v = Double.parseDouble(this.currentValStr);
                    else return false;
                    return spec == null || (v >= spec.min() && v <= spec.max());
                } catch (NumberFormatException e) {
                    return false;
                }
            }

            void reset() {
                this.valid = true;

                if (isList) {
                    this.currentList = new ArrayList<>(this.defaultList);
                } else {
                    this.currentValStr = this.defaultValStr;
                    if (isBool && valueWidget instanceof CycleButton) {
                        ((CycleButton<Boolean>) valueWidget).setValue(Boolean.parseBoolean(this.currentValStr));
                    } else if (valueWidget instanceof EditBox) {
                        ((EditBox) valueWidget).setValue(this.currentValStr);
                        ((EditBox) valueWidget).setTextColor(0xFFE0E0E0);
                    }
                    if (isColor) {
                        try { this.colorPreview = 0xFF000000 | Integer.parseInt(this.currentValStr); } catch (Exception ignored) {}
                    }
                }
                this.updateResetButton();
            }

            void save(Config cfg) {
                if (!valid) return;
                try {
                    if (isList) {
                        field.set(cfg, new ArrayList<>(this.currentList));
                    } else if (isBool) {
                        field.setBoolean(cfg, Boolean.parseBoolean(this.currentValStr));
                    } else if (field.getType() == int.class) {
                        field.setInt(cfg, Integer.parseInt(this.currentValStr));
                    } else if (field.getType() == float.class) {
                        field.setFloat(cfg, Float.parseFloat(this.currentValStr));
                    } else if (field.getType() == double.class) {
                        field.setDouble(cfg, Double.parseDouble(this.currentValStr));
                    }
                } catch (Exception e) {
                    Uei.LOGGER.error("Failed to write config field '{}'", field.getName(), e);
                }
            }

            boolean hasChanged() {
                if (!valid) return false;
                if (isList) return !this.currentList.equals(this.initialList);
                return !areEquivalent(this.currentValStr, this.initialValStr);
            }

            private boolean areEquivalent(String a, String b) {
                try {
                    if (field.getType() == int.class) {
                        return Integer.parseInt(a) == Integer.parseInt(b);
                    } else if (field.getType() == float.class) {
                        return Float.compare(Float.parseFloat(a), Float.parseFloat(b)) == 0;
                    } else if (field.getType() == double.class) {
                        return Double.compare(Double.parseDouble(a), Double.parseDouble(b)) == 0;
                    } else {
                        return a.equals(b);
                    }
                } catch (NumberFormatException e) {
                    return a.equals(b);
                }
            }

            Component getTooltip() {
                if (!valid) {
                    return spec != null ? Component.translatable("uei.config.tooltip.range",
                            formatBound(spec.min()), formatBound(spec.max()))
                            : Component.translatable("uei.config.tooltip.invalid_format");
                }
                return null;
            }

            private String formatBound(double bound) {
                if (bound == Double.NEGATIVE_INFINITY) return "-∞";
                if (bound == Double.POSITIVE_INFINITY) return "∞";
                return field.getType() == int.class ? String.valueOf((int) bound) : String.valueOf((float) bound);
            }
        }
    }

    public static class ListEditScreen extends Screen {
        private final Screen parent;
        private final List<Object> currentList;
        private final Consumer<List<Object>> onSave;
        private OrderList listWidget;

        private final Map<Config.InfoField, Boolean> toggledStates = new HashMap<>();

        public ListEditScreen(Screen parent, Component title, List<Object> list, Consumer<List<Object>> onSave) {
            super(Component.literal("Edit: ").append(title));
            this.parent = parent;
            this.currentList = new ArrayList<>(list);
            this.onSave = onSave;
        }

        @Override
        protected void init() {
            int listY = 33;
            int listBottom = this.height - 35;
            this.listWidget = new OrderList(this.minecraft, this.width, listBottom - listY, listY, 30);
            this.addRenderableWidget(this.listWidget);
            this.listWidget.updateEntries();

            int buttonY = this.height - 26;
            this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, btn -> this.minecraft.setScreenAndShow(this.parent))
                    .bounds(this.width / 2 - 155, buttonY, 150, 20).build());

            this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, btn -> {
                this.onSave.accept(this.currentList);

                this.toggledStates.forEach(this::setShowValue);

                if (this.parent instanceof ConfigScreen cfgScreen) {
                    cfgScreen.entries.clear();
                }

                this.minecraft.setScreenAndShow(this.parent);
            }).bounds(this.width / 2 + 5, buttonY, 150, 20).build());
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            super.render(graphics, mouseX, mouseY, partialTick);
            graphics.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFFFF);
        }

        private boolean getShowValue(Config.InfoField field) {
            return switch (field) {
                case RARITY -> Config.get().showRarity;
                case MAX_LEVEL -> Config.get().showMaxLevel;
                case TREASURE -> Config.get().showTreasure;
                case TRADEABLE -> Config.get().showTradeable;
                case CURSE -> Config.get().showCurse;
                case DISCOVERABLE -> Config.get().showDiscoverable;
                case ENCHANTING_TABLE -> Config.get().showEnchantingTable;
            };
        }

        private void setShowValue(Config.InfoField field, boolean value) {
            switch (field) {
                case RARITY -> Config.get().showRarity = value;
                case MAX_LEVEL -> Config.get().showMaxLevel = value;
                case TREASURE -> Config.get().showTreasure = value;
                case TRADEABLE -> Config.get().showTradeable = value;
                case CURSE -> Config.get().showCurse = value;
                case DISCOVERABLE -> Config.get().showDiscoverable = value;
                case ENCHANTING_TABLE -> Config.get().showEnchantingTable = value;
            }
        }

        private class OrderList extends ContainerObjectSelectionList<OrderEntry> {
            public OrderList(Minecraft mc, int width, int height, int y, int itemHeight) {
                super(mc, width, height, y, itemHeight);
            }

            public void updateEntries() {
                this.clearEntries();
                for (int i = 0; i < currentList.size(); i++) {
                    this.addEntry(new OrderEntry(i));
                }
            }

            @Override
            public int getRowWidth() {
                return 280;
            }

            @Override
            protected int scrollBarX() {
                return this.width / 2 + 150;
            }
        }

        private class OrderEntry extends ContainerObjectSelectionList.Entry<OrderEntry> {
            private final int index;
            private final Object item;
            private final Button upBtn;
            private final Button downBtn;
            private Checkbox visibilityCheckbox;

            public OrderEntry(int index) {
                this.index = index;
                this.item = currentList.get(index);

                if (item instanceof Config.InfoField field) {
                    boolean isVisible = toggledStates.computeIfAbsent(field, ListEditScreen.this::getShowValue);

                    this.visibilityCheckbox = Checkbox.builder(Component.empty(), minecraft.font)
                            .pos(0, 0)
                            .selected(isVisible)
                            .onValueChange((cb, val) -> toggledStates.put(field, val))
                            .build();
                }

                this.upBtn = Button.builder(Component.literal("▲"), btn -> {
                    Collections.swap(currentList, index, index - 1);
                    listWidget.updateEntries();
                }).bounds(0, 0, 24, 20).build();
                this.upBtn.active = index > 0;

                this.downBtn = Button.builder(Component.literal("▼"), btn -> {
                    Collections.swap(currentList, index, index + 1);
                    listWidget.updateEntries();
                }).bounds(0, 0, 24, 20).build();
                this.downBtn.active = index < currentList.size() - 1;
            }

            private Component getLocalizedName(Object item) {
                if (item instanceof Config.InfoField field) {
                    return Component.translatable("uei." + field.name().toLowerCase());
                } else if (item instanceof Config.Section section) {
                    return Component.translatable("uei.section." + section.name().toLowerCase());
                }
                return Component.literal(item.toString());
            }

            @Override
            public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
                int x = this.getX();
                int y = this.getY();
                int width = this.getWidth();
                int itemHeight = 30;

                if (isMouseOver) {
                    graphics.fill(x, y, x + width, y + itemHeight, 0x1AFFFFFF);
                }

                int currentX = x + 10;

                if (this.visibilityCheckbox != null) {
                    this.visibilityCheckbox.setX(currentX);
                    this.visibilityCheckbox.setY(y + (itemHeight - 20) / 2);
                    this.visibilityCheckbox.render(graphics, mouseX, mouseY, partialTick);
                    currentX += 26;
                }

                Component displayName = getLocalizedName(item);
                int textY = y + (itemHeight - minecraft.font.lineHeight) / 2;
                graphics.drawString(minecraft.font, displayName, currentX, textY, 0xFFFFFFFF);

                this.upBtn.setX(x + width - 50);
                this.upBtn.setY(y + (itemHeight - 20) / 2);

                this.downBtn.setX(x + width - 25);
                this.downBtn.setY(y + (itemHeight - 20) / 2);

                this.upBtn.render(graphics, mouseX, mouseY, partialTick);
                this.downBtn.render(graphics, mouseX, mouseY, partialTick);
            }

            @Override
            public @NotNull List<? extends GuiEventListener> children() {
                List<GuiEventListener> list = new ArrayList<>(List.of(this.upBtn, this.downBtn));
                if (this.visibilityCheckbox != null) list.add(this.visibilityCheckbox);
                return list;
            }

            @Override
            public @NotNull List<? extends NarratableEntry> narratables() {
                List<NarratableEntry> list = new ArrayList<>(List.of(this.upBtn, this.downBtn));
                if (this.visibilityCheckbox != null) list.add(this.visibilityCheckbox);
                return list;
            }
        }
    }
}