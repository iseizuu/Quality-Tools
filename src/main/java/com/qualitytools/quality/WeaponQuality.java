package com.qualitytools.quality;

import com.qualitytools.config.ConfigManager;
import com.qualitytools.config.ModConfig;
import net.minecraft.util.Formatting;

public enum WeaponQuality {

    AWFUL(Formatting.GRAY),
    TRASH(Formatting.RED),
    FINE(Formatting.AQUA),
    GREAT(Formatting.GREEN),
    LEGENDARY(Formatting.GOLD),
    MYTHIC(Formatting.LIGHT_PURPLE);

    public final Formatting color;

    WeaponQuality(Formatting color) {
        this.color = color;
    }

    public float getDamageMultiplier() {
        ModConfig.QualityConfig qc = ConfigManager.get().qualities.get(this.name());
        return qc != null ? qc.damageMultiplier : 1.0f;
    }

    public int getWeight() {
        ModConfig.QualityConfig qc = ConfigManager.get().qualities.get(this.name());
        return qc != null ? qc.weight : 10;
    }

    public float getDurabilityMultiplier() {
        ModConfig.QualityConfig qc = ConfigManager.get().qualities.get(this.name());
        return qc != null ? qc.durabilityMultiplier : 1.0f;
    }
}