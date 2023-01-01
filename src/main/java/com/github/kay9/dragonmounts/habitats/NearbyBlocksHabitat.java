package com.github.kay9.dragonmounts.habitats;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public record NearbyBlocksHabitat(float multiplier, HolderSet<Block> blocks) implements Habitat
{
    public static final Codec<NearbyBlocksHabitat> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Habitat.withMultiplier(0.5f, NearbyBlocksHabitat::multiplier),
            RegistryCodecs.homogeneousList(Registry.BLOCK_REGISTRY).fieldOf("blocks").forGetter(NearbyBlocksHabitat::blocks)
    ).apply(instance, NearbyBlocksHabitat::new));

    @Override
    public int getHabitatPoints(Level level, BlockPos basePos)
    {
        return (int) (BlockPos.betweenClosedStream(basePos.offset(1, 1, 1), basePos.offset(-1, -1, -1))
                .filter(p -> level.getBlockState(p).is(blocks))
                .count() * multiplier);
    }

    @Override
    public String type()
    {
        return Habitat.NEARBY_BLOCKS;
    }
}
