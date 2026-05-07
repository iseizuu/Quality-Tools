package com.qualitytools.config;

import java.util.LinkedHashMap;
import java.util.Map;

public class ModConfig {

    public String forgeMaterial = "minecraft:diamond";
    public int forgeCost = 3;
    public Map<String, QualityConfig> qualities = defaultQualities();

    public static Map<String, QualityConfig> defaultQualities() {
        Map<String, QualityConfig> map = new LinkedHashMap<>();
        map.put("AWFUL",     new QualityConfig(0.7f,  5, 4.0f));
        map.put("TRASH",     new QualityConfig(0.85f, 12, 2.5f));
        map.put("FINE",      new QualityConfig(1.0f,  30, 1.0f));
        map.put("GREAT",     new QualityConfig(1.15f, 15,  0.7f));
        map.put("LEGENDARY", new QualityConfig(1.3f,  8,  0.5f));
        map.put("MYTHIC",    new QualityConfig(1.6f,  2,  0.3f));
        return map;
    }

    public static class QualityConfig {
        public float damageMultiplier;
        public int weight;
        public float durabilityMultiplier;

        public QualityConfig() {}

        public QualityConfig(float damageMultiplier, int weight, float durabilityMultiplier) {
            this.damageMultiplier = damageMultiplier;
            this.weight = weight;
            this.durabilityMultiplier = durabilityMultiplier;
        }
    }
}