package com.qualitytools.mixin;

import com.qualitytools.quality.WeaponQuality;
import com.qualitytools.quality.WeaponQualityHelper;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "getAttributeValue", at = @At("RETURN"), cancellable = true)
    private void qualitytools$injectQualityDamage(
            RegistryEntry<EntityAttribute> attribute,
            CallbackInfoReturnable<Double> cir) {

        if (!attribute.equals(EntityAttributes.ATTACK_DAMAGE)) return;

        LivingEntity self = (LivingEntity)(Object)this;
        ItemStack weapon = self.getMainHandStack();
        if (weapon.isEmpty()) return;
        if (!WeaponQualityHelper.hasQuality(weapon)) return;

        WeaponQuality quality = WeaponQualityHelper.getQuality(weapon);
        float multiplier = quality.getDamageMultiplier();

        if (multiplier == 1.0f) return;

        double vanilla = cir.getReturnValue();
        double scaled = Math.round(vanilla * multiplier * 10.0) / 10.0;
        cir.setReturnValue(scaled);
    }
}