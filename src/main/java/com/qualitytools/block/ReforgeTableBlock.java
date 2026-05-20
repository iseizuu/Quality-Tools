package com.qualitytools.block;

import net.minecraft.block.Block;
import net.minecraft.util.Formatting;

public class ReforgeTableBlock extends Block {

    public ReforgeTableBlock(Settings settings) {
        super(settings);
    }


    @Override
    public net.minecraft.util.ActionResult onUseWithItem(
            net.minecraft.item.ItemStack stack,
            net.minecraft.block.BlockState state,
            net.minecraft.world.World world,
            net.minecraft.util.math.BlockPos pos,
            net.minecraft.entity.player.PlayerEntity player,
            net.minecraft.util.Hand hand,
            net.minecraft.util.hit.BlockHitResult hit
    ) {
        if (world.isClient()) {
            return net.minecraft.util.ActionResult.SUCCESS;
        }

        if (player instanceof net.minecraft.server.network.ServerPlayerEntity serverPlayer) {
            assert world
                    .getServer() != null;
            net.minecraft.advancement.AdvancementEntry advancement = world
                    .getServer()
                    .getAdvancementLoader()
                    .get(net.minecraft.util.Identifier.of("qualitytools", "use_reforge_table"));

            if (advancement != null) {
                net.minecraft.advancement.PlayerAdvancementTracker tracker =
                        serverPlayer.getAdvancementTracker();
                if (!tracker.getProgress(advancement).isDone()) {
                    tracker.grantCriterion(advancement, "use_reforge_table");
                }
            }
        }

        player.openHandledScreen(
                new net.minecraft.screen.SimpleNamedScreenHandlerFactory(
                        (syncId, inventory, p) ->
                                new com.qualitytools.screen.ReforgeScreenHandler(syncId, inventory),
                        net.minecraft.text.Text.literal("Reforge Table").formatted(Formatting.BOLD).formatted(Formatting.DARK_PURPLE)
                )
        );

        return net.minecraft.util.ActionResult.CONSUME;
    }
}