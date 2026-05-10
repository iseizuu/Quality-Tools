package com.qualitytools.quality;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.Random;

public class WeaponQualityHelper {

    private static final String NBT_KEY = "quality";

    // ── Random quality roll ────────────────────────────────────────────────────

    public static WeaponQuality getRandomQuality(Random random) {
        int totalWeight = 0;
        for (WeaponQuality q : WeaponQuality.values()) {
            totalWeight += q.getWeight();
        }
        int roll = random.nextInt(totalWeight);
        for (WeaponQuality q : WeaponQuality.values()) {
            roll -= q.getWeight();
            if (roll < 0) return q;
        }
        return WeaponQuality.FINE;
    }

    // ── Set / Get quality from NBT ─────────────────────────────────────────────

    /**
     * Simpan quality ke NBT item.
     * Hanya simpan nama quality — TIDAK ada baked values.
     * Multiplier selalu dibaca runtime dari config.
     */
    public static void setQuality(ItemStack stack, WeaponQuality quality) {
        NbtComponent comp = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        NbtCompound nbt = comp.copyNbt();
        nbt.putString(NBT_KEY, quality.name());
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public static WeaponQuality getQuality(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return WeaponQuality.FINE;
        NbtCompound nbt = comp.copyNbt();
        if (!nbt.contains(NBT_KEY)) return WeaponQuality.FINE;
        try {
            return nbt.getString(NBT_KEY).map(WeaponQuality::valueOf).orElse(WeaponQuality.FINE);
        } catch (Exception e) {
            return WeaponQuality.FINE;
        }
    }

    public static boolean hasQuality(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return false;
        return comp.copyNbt().contains(NBT_KEY);
    }
}
