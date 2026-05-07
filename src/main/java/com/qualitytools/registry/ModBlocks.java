package com.qualitytools.registry;

import com.qualitytools.Qualitytools;
import com.qualitytools.block.ReforgeTableBlock;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

import net.minecraft.block.Block;
import net.minecraft.block.AbstractBlock;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Identifier REFORGE_ID =
            Identifier.of("qualitytools", "reforge_table");

    public static final Block REFORGE_TABLE = new ReforgeTableBlock(
            AbstractBlock.Settings.create()
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, REFORGE_ID))
                    .strength(4.0f)
                    .requiresTool()
    );

    public static void register() {

        Registry.register(Registries.BLOCK, REFORGE_ID, REFORGE_TABLE);
        Registry.register(
                Registries.ITEM,
                REFORGE_ID,
                new BlockItem(
                        REFORGE_TABLE,
                        new Item.Settings().registryKey(
                                RegistryKey.of(RegistryKeys.ITEM, REFORGE_ID)
                        )
                )
        );

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL)
                .register(entries -> entries.add(REFORGE_TABLE));

        Qualitytools.LOGGER.info("[QualityTools] Reforge Block registered");
    }
}