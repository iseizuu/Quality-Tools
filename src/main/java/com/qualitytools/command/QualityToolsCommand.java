package com.qualitytools.command;

import com.mojang.brigadier.CommandDispatcher;
import com.qualitytools.config.ConfigManager;
import com.qualitytools.quality.WeaponQuality;
import com.qualitytools.quality.WeaponQualityHelper;
import net.minecraft.command.permission.PermissionLevel;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class QualityToolsCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("qualitytools")
                        .requires(source -> source.getPermissions().hasPermission(
                                new net.minecraft.command.permission.Permission.Level(PermissionLevel.ADMINS)))
                        .then(CommandManager.literal("reload")
                                .executes(ctx -> {
                                    ConfigManager.reload();
                                    if (ctx.getSource().getServer() != null) {
                                        for (ServerPlayerEntity player :
                                                ctx.getSource().getServer().getPlayerManager().getPlayerList()) {
                                            for (int i = 0; i < player.getInventory().size(); i++) {
                                                var stack = player.getInventory().getStack(i);
                                                if (!stack.isEmpty()) {
                                                    WeaponQualityHelper.resetBaseDamage(stack);

                                                    if (!WeaponQualityHelper.hasQuality(stack)) {
                                                        WeaponQuality quality = WeaponQualityHelper.getQuality(stack);
                                                        WeaponQualityHelper.rebakeMultiplier(stack, quality);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    ctx.getSource().sendFeedback(
                                            () -> Text.literal("[QualityTools] ").formatted(Formatting.GREEN)
                                                        .append(Text.literal(ConfigManager.Messages).formatted(
                                                                ConfigManager.isSuccess ? Formatting.GREEN : Formatting.RED
                                                        )),
                                                true
                                    );

                                    return 1;
                                })
                        )
        );
    }
}