package com.github.kay9.dragonmounts.habitats;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public record FluidHabitat(float multiplier, HolderSet<Fluid> fluids) implements Habitat
{
    public static final Codec<FluidHabitat> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Habitat.withMultiplier(0.5f, FluidHabitat::multiplier),
            RegistryCodecs.homogeneousList(Registry.FLUID_REGISTRY).fieldOf("fluids").forGetter(FluidHabitat::fluids)
    ).apply(instance, FluidHabitat::new));

    @Override
    public int getHabitatPoints(Level level, BlockPos pos)
    {
        return (int) (BlockPos.betweenClosedStream(pos.offset(1, 1, 1), pos.offset(-1, -1, -1))
                .filter(p -> level.getFluidState(p).is(fluids))
                .count() * multiplier);
    }

    @Override
    public String type()
    {
        return Habitat.IN_FLUID;
    }
}
