package com.github.kay9.dragonmounts.abilities;

import com.github.kay9.dragonmounts.dragon.TameableDragon;
import com.mojang.serialization.Codec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class NightVisionAbility implements Ability {
    public static final NightVisionAbility INSTANCE = new NightVisionAbility();
    public static final Codec<NightVisionAbility> CODEC = Codec.unit(INSTANCE);

    @Override
    public void tick(TameableDragon dragon) {
        if (dragon.level.isClientSide || !dragon.isAdult()) {
            return;
        }

        Entity entity = dragon.getControllingPassenger();
        if (entity == null || !(entity instanceof LivingEntity player)) {
            return;
        }

        if (player.level.random.nextInt(6) == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300), dragon);
        }
    }

    @Override
    public String type() {
        return NIGHT_VISION;
    }
}
