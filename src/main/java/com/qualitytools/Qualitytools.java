package com.qualitytools;

import com.qualitytools.command.QualityToolsCommand;
import com.qualitytools.config.ConfigManager;
import com.qualitytools.registry.ModBlocks;
import com.qualitytools.screen.ReforgeScreenHandler;
import com.qualitytools.network.ModPackets;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Qualitytools implements ModInitializer {

	public static final Logger LOGGER = LoggerFactory.getLogger("qualitytools");

	@Override
	public void onInitialize() {
		ConfigManager.load();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			QualityToolsCommand.register(dispatcher);
		});
		ModBlocks.register();
		ReforgeScreenHandler.register();
		ModPackets.registerPayload();
		ModPackets.registerReceiver();

		Qualitytools.LOGGER.info("╔════════════════════════════╗");
		Qualitytools.LOGGER.info("║  _____   ______            ║");
		Qualitytools.LOGGER.info("║ /\\  __`\\/\\__  _\\           ║");
		Qualitytools.LOGGER.info("║ \\ \\ \\/\\ \\/_/\\ \\/           ║");
		Qualitytools.LOGGER.info("║  \\ \\ \\ \\ \\ \\ \\ \\           ║");
		Qualitytools.LOGGER.info("║   \\ \\ \\'\\ \\ \\ \\ \\          ║");
		Qualitytools.LOGGER.info("║    \\ \\___\\_\\ \\ \\_\\         ║");
		Qualitytools.LOGGER.info("║     \\/__//_/  \\/_/         ║");
		Qualitytools.LOGGER.info("║                            ║");
		Qualitytools.LOGGER.info("║  QualityTools  		    ║");
		Qualitytools.LOGGER.info("╚════════════════════════════╝");
	}
}