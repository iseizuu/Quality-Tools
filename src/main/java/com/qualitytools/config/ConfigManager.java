package com.qualitytools.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qualitytools.Qualitytools;
import com.qualitytools.quality.WeaponQuality;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.*;
import java.nio.file.*;

public class ConfigManager {

    public static String Messages = "Check Console";
    public static boolean isSuccess = false;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("qualitytools.json");

    private static ModConfig config = new ModConfig();
    public static ModConfig get() {
        return config;
    }

    public static void load() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                save();
                Messages = "Config created";
                isSuccess = true;
                Qualitytools.LOGGER.info("Config created: {}", CONFIG_PATH);
                return;
            }

            ModConfig loaded;

            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                loaded = GSON.fromJson(reader, ModConfig.class);
            }

            if (loaded == null) {
                Messages = "❌ Config file is empty or invalid JSON!";
                isSuccess = false;
                Qualitytools.LOGGER.error("❌ Config file is empty or invalid JSON!");
                return;
            }

            Identifier itemId = Identifier.of(loaded.forgeMaterial);
            if (!Registries.ITEM.containsId(itemId)) {
                Messages = "❌ Invalid forgeMaterial: '" + loaded.forgeMaterial + "'";
                isSuccess = false;
                Qualitytools.LOGGER.error("❌ Invalid forgeMaterial: '{}'", loaded.forgeMaterial);
                return;
            }

            if (loaded.forgeCost <= 0) {
                Messages = "❌ Invalid forgeCost: must greater than 0!";
                isSuccess = false;
                Qualitytools.LOGGER.error("❌ Invalid forgeCost: must greater than 0!");
                return;
            }

            if (loaded.qualities == null || loaded.qualities.isEmpty()) {
                Messages = "❌ qualities config is empty";
                isSuccess = false;
                Qualitytools.LOGGER.error("❌ qualities config is empty");
                loaded.qualities = ModConfig.defaultQualities();
            }

            for (String name : loaded.qualities.keySet()) {
                try {
                    WeaponQuality.valueOf(name);

                } catch (IllegalArgumentException e) {
                    Messages = "❌ Invalid quality name: '" + name + "' —  AWFUL/TRASH/FINE/GREAT/LEGENDARY/MYTHIC";
                    isSuccess = false;
                    Qualitytools.LOGGER.error("❌ Invalid quality name: '{}' —  AWFUL/TRASH/FINE/GREAT/LEGENDARY/MYTHIC", name);
                    Qualitytools.LOGGER.error("⚠️ Config NOT applied.");
                    return;
                }
                ModConfig.QualityConfig qc = loaded.qualities.get(name);
                if (qc.weight <= 0) {
                    Messages = "❌ Weight for '" + name + "' should greater than 0!";
                    isSuccess = false;
                    Qualitytools.LOGGER.error("❌ Weight for '{}' should greater than 0!", name);
                    return;
                }
                if (qc.damageMultiplier <= 0) {
                    Messages = "❌ damageMultiplier should '" + name + "' greater than > 0!";
                    isSuccess = false;
                    Qualitytools.LOGGER.error("❌ damageMultiplier should '{}' greater than > 0!", name);
                    return;
                }
            }

            config = loaded;
            Messages = "✅ Config loaded! Material: "
                    + config.forgeMaterial + ", Cost: " + config.forgeCost;
            isSuccess = true;

            Qualitytools.LOGGER.info("✅ Config loaded! Material: {}, Cost: {}", config.forgeMaterial, config.forgeCost);

        } catch (Exception e) {
            Messages = "❌ Failed to load config, check console";
            isSuccess = false;
            Qualitytools.LOGGER.error("❌ Failed to load config: {}", e.getMessage());
        }
    }

    public static void save() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            GSON.toJson(config, writer);
            Qualitytools.LOGGER.info("Config saved!");
        } catch (Exception e) {
            Qualitytools.LOGGER.error("❌ Failed to save config: {}", e.getMessage());
        }
    }

    public static void reload() {
        load();
    }
}