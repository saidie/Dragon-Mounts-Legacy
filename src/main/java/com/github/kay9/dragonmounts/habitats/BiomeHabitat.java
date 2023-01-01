package com.github.kay9.dragonmounts.habitats;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public record BiomeHabitat(int points, HolderSet<Biome> biomes) implements Habitat
{
    public static final Codec<BiomeHabitat> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Habitat.withPoints(2, BiomeHabitat::points),
            RegistryCodecs.homogeneousList(Registry.BIOME_REGISTRY).fieldOf("biomes").forGetter(BiomeHabitat::biomes)
    ).apply(instance, BiomeHabitat::new));

    @Override
    public int getHabitatPoints(Level level, BlockPos pos)
    {
        return biomes.contains(level.getBiome(pos))? points : 0;
    }

    @Override
    public String type()
    {
        return Habitat.BIOMES;
    }
}
