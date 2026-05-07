package com.qualitytools;

import com.qualitytools.screen.ReforgeScreen;
import com.qualitytools.screen.ReforgeScreenHandler;
import com.qualitytools.network.ModPackets;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import com.qualitytools.registry.ModBlocks;
import com.qualitytools.config.ConfigManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class QualitytoolsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		ModPackets.registerPayload();

		// GUI
		HandledScreens.register(
				ReforgeScreenHandler.TYPE,
				ReforgeScreen::new
		);
		ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, lines) -> {
			if (!stack.isOf(ModBlocks.REFORGE_TABLE.asItem())) return;

			lines.add(Text.literal("Yuxina's Quality Tools")
					.formatted(Formatting.BLUE, Formatting.ITALIC));
		});
	}
}