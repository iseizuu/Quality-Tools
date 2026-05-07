package com.qualitytools.mixin;

import com.qualitytools.quality.WeaponQuality;
import com.qualitytools.quality.WeaponQualityHelper;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackTooltipMixin {

    @Inject(method = "getTooltip", at = @At("RETURN"))
    private void qualitytools$addTooltip(
            Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir) {

        ItemStack stack = (ItemStack)(Object)this;

        AttributeModifiersComponent attr = stack.get(DataComponentTypes.ATTRIBUTE_MODIFIERS);
        if (attr == null) return;

        boolean isWeapon = attr.modifiers().stream().anyMatch(mod ->
                mod.attribute().equals(EntityAttributes.ATTACK_DAMAGE)
        );

        if (!isWeapon) return;
        if (WeaponQualityHelper.hasQuality(stack)) return;

        WeaponQuality quality = WeaponQualityHelper.getQuality(stack);
        List<Text> tooltip = cir.getReturnValue();

        float bakedMultiplier = WeaponQualityHelper.getBakedDamageMultiplier(stack);

        tooltip.add(Text.literal("Yuxina's Quality Tools")
                .formatted(Formatting.BLUE, Formatting.ITALIC));
        tooltip.add(Text.literal("✦ Quality: ")
                .formatted(Formatting.WHITE)
                .append(Text.literal(quality.name()).formatted(quality.color)));
        tooltip.add(Text.literal("✦ Damage: ")
                .formatted(Formatting.WHITE)
                .append(Text.literal("x" + bakedMultiplier).formatted(quality.color)));
    }
}
