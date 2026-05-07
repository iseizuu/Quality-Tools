package com.qualitytools.screen;

import com.qualitytools.quality.WeaponQuality;
import com.qualitytools.quality.WeaponQualityHelper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import com.qualitytools.config.ConfigManager;
import net.minecraft.item.Item;

import java.util.Random;

public class ReforgeScreenHandler extends ScreenHandler {

    public static ScreenHandlerType<ReforgeScreenHandler> TYPE;
    private final SimpleInventory inventory = new SimpleInventory(1);

    public ReforgeScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(TYPE, syncId);

        // WEAPON SLOT
        this.addSlot(new Slot(inventory, 0, 80, 35) {
            @Override
            public boolean canInsert(ItemStack stack) {

                var attr = stack.get(net.minecraft.component.DataComponentTypes.ATTRIBUTE_MODIFIERS);

                if (attr == null) return false;

                return attr.modifiers().stream().anyMatch(mod ->
                        mod.attribute().equals(net.minecraft.entity.attribute.EntityAttributes.ATTACK_DAMAGE)
                );
            }
        });

        // PLAYER INVENTORY
        int m, l;

        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9,
                        8 + l * 18, 84 + m * 18));
            }
        }

        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m,
                    8 + m * 18, 142));
        }
    }

    public void reforge(PlayerEntity player) {

        ItemStack stack = inventory.getStack(0);

        if (stack.isEmpty()) {
            player.sendMessage(
                    Text.literal("[QualityTools] ").formatted(Formatting.GREEN)
                            .append(Text.literal("Reforge required tools").formatted(Formatting.RED)),
                    false
            );

            ((ServerPlayerEntity) player).closeHandledScreen();
            return;
        }

        int cost = ConfigManager.get().forgeCost;
        String materialId = ConfigManager.get().forgeMaterial;
        Item requiredItem = Registries.ITEM.get(Identifier.of(materialId));

        // CHECK MATERIAL
        int found = 0;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack inv = player.getInventory().getStack(i);
            if (inv.isOf(requiredItem)) {
                found += inv.getCount();
            }
        }

        if (found < cost) {

            player.sendMessage(
                    Text.literal("[QualityTools] ").formatted(Formatting.GREEN)
                            .append(Text.literal("Not enough " + materialId + " §c(Need " + cost + ")")),
                    false
            );

            player.getEntityWorld().playSound(null, player.getBlockPos(),
                    SoundEvents.ENTITY_CAT_DEATH, SoundCategory.PLAYERS, 1.0f, 1.0f);
            ((ServerPlayerEntity) player).closeHandledScreen();
            return;
        }

        // REMOVE MATERIAL
        int remaining = cost;
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack inv = player.getInventory().getStack(i);
            if (inv.isOf(requiredItem)) {
                int take = Math.min(inv.getCount(), remaining);
                inv.decrement(take);
                remaining -= take;
                if (remaining <= 0) break;
            }
        }

        //  REROLL QUALITY
        WeaponQuality newQuality = WeaponQualityHelper.getRandomQuality(new Random());
        WeaponQualityHelper.setQuality(stack, newQuality);
        WeaponQualityHelper.resetBaseDamage(stack);

        player.sendMessage(
                Text.literal("[Quality Tools] ")
                        .formatted(net.minecraft.util.Formatting.GREEN)
                        .append(Text.literal("New Quality: ").formatted(net.minecraft.util.Formatting.WHITE))
                        .append(Text.literal(newQuality.name()).formatted(newQuality.color).formatted(Formatting.BOLD)),
                false
        );

        player.getEntityWorld().playSound(
                null,
                player.getBlockPos(),
                newQuality.name().equals("MYTHIC") ? SoundEvents.ENTITY_PLAYER_LEVELUP : SoundEvents.BLOCK_ANVIL_USE,
                SoundCategory.PLAYERS,
                1.0f,
                1.0f
        );

        // PARTICLE EFFECT
        if (player.getEntityWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    newQuality.name().equals("MYTHIC") ? ParticleTypes.HEART : ParticleTypes.HAPPY_VILLAGER,
                    player.getX(),
                    player.getY() + 1,
                    player.getZ(),
                    25,
                    0.5, 0.5, 0.5,
                    0.1
            );
        }

        ((ServerPlayerEntity) player).closeHandledScreen();
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.dropInventory(player, inventory);
    }


    // REGISTER
    public static void register() {
        TYPE = Registry.register(
                Registries.SCREEN_HANDLER,
                Identifier.of("qualitytools", "reforge"),
                new ScreenHandlerType<>(
                        ReforgeScreenHandler::new,
                        FeatureSet.empty()
                )
        );
    }
}