package com.qualitytools.network;

import com.qualitytools.Qualitytools;
import com.qualitytools.screen.ReforgeScreenHandler;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class ModPackets {

    private static boolean payloadRegistered = false;
    public record ReforgePayload() implements CustomPayload {

        public static final Id<ReforgePayload> ID =
                new Id<>(Identifier.of("qualitytools", "reforge"));

        public static final PacketCodec<PacketByteBuf, ReforgePayload> CODEC =
                PacketCodec.unit(new ReforgePayload());

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    // REGISTER PAYLOAD
    public static void registerPayload() {
        if (payloadRegistered) return;

        payloadRegistered = true;

        PayloadTypeRegistry.playC2S().register(
                ReforgePayload.ID,
                ReforgePayload.CODEC
        );

        Qualitytools.LOGGER.info("[QualityTools] Payload registered");
    }

    // SERVER RECEIVER ONLY
    public static void registerReceiver() {

        ServerPlayNetworking.registerGlobalReceiver(
                ReforgePayload.ID,
                (payload, context) -> {

                    context.server().execute(() -> {

                        var player = context.player();

                        if (player.currentScreenHandler instanceof ReforgeScreenHandler screen) {
                            screen.reforge(player);

                        }
                    });
                }
        );
    }
}