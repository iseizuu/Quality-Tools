package com.qualitytools.mixin;

import com.qualitytools.quality.WeaponQualityHelper;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.Random;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Unique private static final Random RANDOM = new Random();

    @Unique private static final Set<ItemStack> NON_WEAPON_CACHE =
            Collections.newSetFromMap(new WeakHashMap<>());

    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void qualitytools$assignQuality(World world, Entity entity, EquipmentSlot slot, CallbackInfo ci) {
        if (world.isClient()) return;

        ItemStack stack = (ItemStack)(Object)this;
        if (stack.isEmpty()) return;
        if (WeaponQualityHelper.hasQuality(stack)) return;
        if (NON_WEAPON_CACHE.contains(stack)) return;

        AttributeModifiersComponent attr = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (attr == null) {
            NON_WEAPON_CACHE.add(stack);
            return;
        }

        boolean isWeapon = attr.modifiers().stream()
                .anyMatch(mod -> mod.attribute().equals(EntityAttributes.ATTACK_DAMAGE));

        if (!isWeapon) {
            NON_WEAPON_CACHE.add(stack);
            return;
        }

        WeaponQualityHelper.setQuality(stack, WeaponQualityHelper.getRandomQuality(RANDOM));
        System.out.println("[QualityTools] Assigned quality to: " + stack.getItem());
    }

    @Inject(method = "setDamage", at = @At("HEAD"), cancellable = true)
    private void qualitytools$modifyDurability(int damage, CallbackInfo ci) {
        ItemStack stack = (ItemStack)(Object)this;

        if (stack.isEmpty()) return;
        if (WeaponQualityHelper.hasQuality(stack)) return;
        if (NON_WEAPON_CACHE.contains(stack)) return;

        AttributeModifiersComponent attr = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (attr == null) return;

        boolean isWeapon = attr.modifiers().stream()
                .anyMatch(mod -> mod.attribute().equals(EntityAttributes.ATTACK_DAMAGE));
        if (!isWeapon) {
            NON_WEAPON_CACHE.add(stack);
            return;
        }

        int currentDamage = stack.getDamage();
        if (damage <= currentDamage) return;

        int delta = damage - currentDamage;
        float durabilityMult = WeaponQualityHelper.getQuality(stack).getDurabilityMultiplier();

        int scaledDelta;
        if (durabilityMult >= 1.0f) {
            scaledDelta = Math.max(1, Math.round(delta * durabilityMult));
        } else {
            scaledDelta = (Math.random() < durabilityMult) ? 1 : 0;
        }

        if (scaledDelta == 0) {
            ci.cancel();
            return;
        }

        ci.cancel();
        stack.set(
                DataComponentTypes.DAMAGE,
                Math.min(currentDamage + scaledDelta, stack.getMaxDamage())
        );
    }
}