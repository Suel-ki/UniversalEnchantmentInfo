package io.github.suel_ki.uei.config;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.suel_ki.uei.Uei;
import io.github.suel_ki.uei.PlatformHelper;
import net.minecraft.util.Mth;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    return f.getAnnotation(ConfigSpec.class) == null;
                }
                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            })
            .create();

    private static Config INSTANCE;

    public static void init() {
        INSTANCE = load();
    }

    @ConfigSpec(min = 0.1, max = 10)
    public float scrollSpeed = 1.0f;

    @ConfigSpec(min = 0.1, max = 10)
    public float textScrollSpeedMultiplier = 1.0f;

    @ConfigSpec(min = 1, max = 1000)
    public int maxApplicableSlots = 4;

    @ConfigSpec
    public boolean useTextForBooleans = false;

    @ConfigSpec
    public boolean showRarity = true;

    @ConfigSpec
    public boolean showMaxLevel = true;

    @ConfigSpec
    public boolean showTreasure = true;

    @ConfigSpec
    public boolean showTradeable = true;

    @ConfigSpec
    public boolean showCurse = true;

    @ConfigSpec
    public boolean showDiscoverable = true;

    @ConfigSpec
    public boolean showEnchantingTable = true;

    @ConfigSpec(min = 0, max = 16777215, isColor = true)
    public int rarityColorCommon = 16777215;

    @ConfigSpec(min = 0, max = 16777215, isColor = true)
    public int rarityColorUncommon = 16777045;

    @ConfigSpec(min = 0, max = 16777215, isColor = true)
    public int rarityColorRare = 5592575;

    @ConfigSpec(min = 0, max = 16777215, isColor = true)
    public int rarityColorEpic = 16733695;

    public static Config get() {
        return INSTANCE;
    }

    private void validate() {
        for (Field field : Config.class.getFields()) {
            ConfigSpec spec = field.getAnnotation(ConfigSpec.class);
            if (spec == null) continue;

            try {
                if (field.getType() == float.class) {
                    float val = field.getFloat(this);
                    float clamped = Mth.clamp(val, (float) spec.min(), (float) spec.max());
                    if (val != clamped) {
                        field.setFloat(this, clamped);
                    }
                } else if (field.getType() == int.class) {
                    int val = field.getInt(this);
                    int clamped = Mth.clamp(val, (int) spec.min(), (int) spec.max());
                    if (val != clamped) {
                        field.setInt(this, clamped);
                    }
                } else if (field.getType() == double.class) {
                    double val = field.getDouble(this);
                    double clamped = Mth.clamp(val, spec.min(), spec.max());
                    if (val != clamped) {
                        field.setDouble(this, clamped);
                    }
                }
            } catch (IllegalAccessException e) {
                Uei.LOGGER.warn("Failed to validate config field '{}'", field.getName(), e);
            }
        }
    }

    public static void save() {
        Path file = getConfigFile();
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(INSTANCE));
        } catch (IOException e) {
            Uei.LOGGER.error("Failed to save config: ", e);
        }
    }

    private static Config load() {
        Path file = getConfigFile();
        if (Files.exists(file)) {
            try {
                String json = Files.readString(file);
                Config cfg = GSON.fromJson(json, Config.class);
                if (cfg != null) {
                    cfg.validate();
                    return cfg;
                }
            } catch (Exception e) {
                Uei.LOGGER.warn("Failed to parse config, resetting to defaults.");
            }
        }

        Config defaultCfg = new Config();
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(defaultCfg));
        } catch (IOException e) {
            Uei.LOGGER.error("Failed to create default config: ", e);
        }
        return defaultCfg;
    }

    private static Path getConfigFile() {
        return PlatformHelper.getConfigDirectory()
                .resolve("universalenchantmentinfo/uei.json");
    }

}
