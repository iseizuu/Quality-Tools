package com.qualitytools.mixin;

import com.qualitytools.quality.WeaponQuality;
import com.qualitytools.quality.WeaponQualityHelper;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.AttributeModifiersComponent.Entry;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Unique
    private static final String BASE_DAMAGE_KEY = "base_damage";

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void qualitytools$applyQuality(World world, Entity entity, EquipmentSlot slot, CallbackInfo ci) {

        ItemStack stack = (ItemStack)(Object)this;

        if (world.isClient()) return;
        if (stack.isEmpty()) return;

        AttributeModifiersComponent attr = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (attr == null) return;

        boolean isWeapon = attr.modifiers().stream().anyMatch(mod ->
                mod.attribute().equals(EntityAttributes.ATTACK_DAMAGE)
        );
        if (!isWeapon) return;

        if (WeaponQualityHelper.hasQuality(stack)) {
            WeaponQuality quality = WeaponQualityHelper.getRandomQuality(new Random());
            WeaponQualityHelper.setQuality(stack, quality);
        }

        WeaponQuality quality = WeaponQualityHelper.getQuality(stack);

        NbtComponent comp = stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT);
        var nbt = comp.copyNbt();

        if (nbt.contains(BASE_DAMAGE_KEY)) return;

        List<Entry> newModifiers = new ArrayList<>();

        for (Entry entry : attr.modifiers()) {
            if (entry.attribute().equals(EntityAttributes.ATTACK_DAMAGE)) {

                double base = entry.modifier().value();
                nbt.putDouble(BASE_DAMAGE_KEY, base);

                double scaled = Math.round(base * quality.getDamageMultiplier() * 10.0) / 10.0;

                EntityAttributeModifier newMod = new EntityAttributeModifier(
                        entry.modifier().id(),
                        scaled,
                        entry.modifier().operation()
                );
                newModifiers.add(new Entry(entry.attribute(), newMod, entry.slot()));

            } else {
                newModifiers.add(entry);
            }
        }

        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        stack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, new AttributeModifiersComponent(newModifiers));
    }

    @Inject(method = "setDamage", at = @At("HEAD"), cancellable = true)
    private void qualitytools$modifyDurability(int damage, CallbackInfo ci) {

        ItemStack stack = (ItemStack)(Object)this;

        if (stack.isEmpty()) return;
        if (WeaponQualityHelper.hasQuality(stack)) return;

        AttributeModifiersComponent attr = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (attr == null) return;
        boolean isWeapon = attr.modifiers().stream().anyMatch(mod ->
                mod.attribute().equals(EntityAttributes.ATTACK_DAMAGE)
        );
        if (!isWeapon) return;

        int currentDamage = stack.getDamage();
        if (damage <= currentDamage) return;

        int delta = damage - currentDamage;
        int scaledDelta;

        float durabilityMult = WeaponQualityHelper.getBakedDurabilityMultiplier(stack);

        if (durabilityMult >= 1.0f) {
            scaledDelta = Math.max(1, Math.round(delta * durabilityMult));
        } else {
            scaledDelta = (Math.random() < durabilityMult) ? 1 : 0;
        }

        if (scaledDelta == 0) {
            ci.cancel();
            return;
        }

        int newDamage = currentDamage + scaledDelta;
        ci.cancel();
        stack.set(
                net.minecraft.component.DataComponentTypes.DAMAGE,
                Math.min(newDamage, stack.getMaxDamage())
        );
    }
}
