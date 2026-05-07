package com.qualitytools.quality;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeaponQualityHelper {

    private static final String NBT_KEY = "quality";
    private static final String BASE_DAMAGE_KEY = "base_damage";
    private static final String BAKED_MULTIPLIER_KEY = "damage_multiplier";
    private static final String BAKED_DURABILITY_KEY = "durability_multiplier";

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

    public static void setQuality(ItemStack stack, WeaponQuality quality) {
        NbtComponent comp = stack.getOrDefault(
                DataComponentTypes.CUSTOM_DATA,
                NbtComponent.DEFAULT
        );
        NbtCompound nbt = comp.copyNbt();

        nbt.putString(NBT_KEY, quality.name());
        nbt.putFloat(BAKED_MULTIPLIER_KEY, quality.getDamageMultiplier());
        nbt.putFloat(BAKED_DURABILITY_KEY, quality.getDurabilityMultiplier());

        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    public static WeaponQuality getQuality(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return WeaponQuality.FINE;

        NbtCompound nbt = comp.copyNbt();
        if (!nbt.contains(NBT_KEY)) return WeaponQuality.FINE;

        try {
            var optional = nbt.getString(NBT_KEY);
            return optional.map(WeaponQuality::valueOf).orElse(WeaponQuality.FINE);
        } catch (Exception e) {
            return WeaponQuality.FINE;
        }
    }

    public static float getBakedDamageMultiplier(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return 1.0f;
        NbtCompound nbt = comp.copyNbt();
        if (!nbt.contains(BAKED_MULTIPLIER_KEY)) {
            return getQuality(stack).getDamageMultiplier();
        }
        return nbt.getFloat(BAKED_MULTIPLIER_KEY).orElse(1.0f);
    }

    public static float getBakedDurabilityMultiplier(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return 1.0f;
        NbtCompound nbt = comp.copyNbt();
        if (!nbt.contains(BAKED_DURABILITY_KEY)) {
            return getQuality(stack).getDurabilityMultiplier();
        }
        return nbt.getFloat(BAKED_DURABILITY_KEY).orElse(1.0f);
    }

    public static boolean hasQuality(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return true;
        NbtCompound nbt = comp.copyNbt();
        return !nbt.contains(NBT_KEY);
    }

    public static void resetBaseDamage(ItemStack stack) {
        NbtComponent comp = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (comp == null) return;

        NbtCompound nbt = comp.copyNbt();
        if (!nbt.contains(BASE_DAMAGE_KEY)) return;

        double base = nbt.getDouble(BASE_DAMAGE_KEY).orElse(0.0);

        AttributeModifiersComponent attr = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (attr != null && base > 0) {
            List<AttributeModifiersComponent.Entry> reset = getEntries(attr, base);
            stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS,
                    new AttributeModifiersComponent(reset));
        }

        nbt.remove(BASE_DAMAGE_KEY);
        nbt.remove(BAKED_MULTIPLIER_KEY);
        nbt.remove(BAKED_DURABILITY_KEY);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    private static @NonNull List<AttributeModifiersComponent.Entry> getEntries(AttributeModifiersComponent attr, double base) {
        List<AttributeModifiersComponent.Entry> reset = new ArrayList<>();
        for (AttributeModifiersComponent.Entry entry : attr.modifiers()) {
            if (entry.attribute().equals(EntityAttributes.ATTACK_DAMAGE)) {
                reset.add(new AttributeModifiersComponent.Entry(
                        entry.attribute(),
                        new EntityAttributeModifier(
                                entry.modifier().id(),
                                base,
                                entry.modifier().operation()
                        ),
                        entry.slot()
                ));
            } else {
                reset.add(entry);
            }
        }
        return reset;
    }
}
