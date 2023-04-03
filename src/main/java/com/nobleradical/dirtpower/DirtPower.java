package com.nobleradical.dirtpower;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DirtPower implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("dirtpower");
    public static final RedstoneBrick REDSTONE_BRICK = new RedstoneBrick(FabricBlockSettings.copy(Blocks.DIRT).hardness(0.25f).solidBlock((state, world, pos) -> false));
    public static final RedstoneBrick GREENSTONE_BRICK = new RedstoneBrick(FabricBlockSettings.copy(Blocks.DIRT).hardness(0.25f).solidBlock((state, world, pos) -> false));
    public static final RedstoneBrick BLUESTONE_BRICK = new RedstoneBrick(FabricBlockSettings.copy(Blocks.DIRT).hardness(0.25f).solidBlock((state, world, pos) -> false));
    

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Initializing dirtpower. Powered... by dirt.");
        Registry.register(Registry.BLOCK, new Identifier("dirtpower", "encased_redstone_powder"), REDSTONE_BRICK);
        Registry.register(Registry.ITEM, new Identifier("dirtpower", "encased_redstone_powder"), new BlockItem(REDSTONE_BRICK, new FabricItemSettings().group(ItemGroup.REDSTONE)));
        Registry.register(Registry.BLOCK, new Identifier("dirtpower", "encased_greenstone_powder"), GREENSTONE_BRICK);
        Registry.register(Registry.ITEM, new Identifier("dirtpower", "encased_greenstone_powder"), new BlockItem(GREENSTONE_BRICK, new FabricItemSettings().group(ItemGroup.REDSTONE)));
        Registry.register(Registry.BLOCK, new Identifier("dirtpower", "encased_bluestone_powder"), BLUESTONE_BRICK);
        Registry.register(Registry.ITEM, new Identifier("dirtpower", "encased_bluestone_powder"), new BlockItem(BLUESTONE_BRICK, new FabricItemSettings().group(ItemGroup.REDSTONE)));
	}
}