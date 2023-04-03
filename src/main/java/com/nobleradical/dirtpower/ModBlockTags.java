package com.nobleradical.dirtpower;

import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlockTags {
    public static final TagKey<Block> REDSTONE_WIRES = TagKey.of(Registry.BLOCK_KEY, new Identifier("c", "redstone_wires"));
    public static final TagKey<Block> ENCASED_REDSTONE_POWDERS = TagKey.of(Registry.BLOCK_KEY, new Identifier("dirtpower", "encased_redstone_powders"));
}
