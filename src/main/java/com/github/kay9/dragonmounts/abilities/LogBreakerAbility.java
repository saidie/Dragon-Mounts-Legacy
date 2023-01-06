package com.github.kay9.dragonmounts.abilities;

import com.github.kay9.dragonmounts.dragon.TameableDragon;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

import static com.github.kay9.dragonmounts.client.KeyMap.DRAGON_ABILITY;

public class LogBreakerAbility implements Ability {

    private static final int COOLDOWN_PERIOD = 6;
    private static final int SEARCH_RANGE_XZ = 5;
    private static final int SEARCH_RANGE_Y = 20;

    public static final LogBreakerAbility INSTANCE = new LogBreakerAbility();
    public static final Codec<LogBreakerAbility> CODEC = Codec.unit(INSTANCE);

    private int cooldown = 0;

    @Override
    public void tick(TameableDragon dragon) {
        if (dragon.level.isClientSide || !dragon.isAdult() || dragon.getControllingPassenger() == null) {
            return;
        }

        if (DRAGON_ABILITY.isDown()) {
            if (cooldown > 0) {
                --cooldown;
                return;
            } else {
                cooldown = COOLDOWN_PERIOD;
            }

            float health = dragon.getHealth();
            if (health < 16) {
                return;
            }

            Optional<BlockPos> target = BlockPos.findClosestMatch(dragon.blockPosition(), SEARCH_RANGE_XZ, SEARCH_RANGE_Y, blockPos -> {
                BlockState st = dragon.level.getBlockState(blockPos);
                return st.is(BlockTags.LOGS);
            });

            if (target.isEmpty()) {
                return;
            }

            dragon.level.destroyBlock(target.get(), true);
            dragon.setHealth(health - 0.3f);
        } else {
            if (cooldown > 0) {
                --cooldown;
            }
        }
    }

    @Override
    public String type() {
        return LOG_BREAKER;
    }
}
