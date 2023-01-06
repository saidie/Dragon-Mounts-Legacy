package com.github.kay9.dragonmounts.abilities;

import com.github.kay9.dragonmounts.dragon.TameableDragon;
import com.mojang.serialization.Codec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.phys.Vec3;

public class FireballAbility implements Ability {

    private static final int COOLDOWN_PERIOD = 4;
    private static final double SPEED_SCALE = 2;

    public static final FireballAbility INSTANCE = new FireballAbility();
    public static final Codec<FireballAbility> CODEC = Codec.unit(INSTANCE);

    private int cooldown = 0;

    @Override
    public void tick(TameableDragon dragon) {
        if (dragon.level.isClientSide || !dragon.isAdult() || dragon.getControllingPassenger() == null) {
            return;
        }

        Player player = (Player) dragon.getControllingPassenger();

        if (dragon.abilityEnabled) {
            if (cooldown > 0) {
                --cooldown;
                return;
            } else {
                cooldown = COOLDOWN_PERIOD;
            }
//            String msg = String.format("say Player xRot=%f, yRot=%f : conv xRot=%f, yRot=%f",
//                    player.getXRot(), player.getYRot(), xRot, yRot);
//            MinecraftServer server = dragon.level.getServer();
//            server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), msg);

            double lambda = -dragon.getYRot() * ((float)Math.PI / 180F);
            Vec3 dragonHeadPos = dragon.getEyePosition().
                    add(5 * Math.sin(lambda), 0, 5 * Math.cos((lambda)));

            Vec3 delta = player.getLookAngle().scale(SPEED_SCALE);
            Entity fireball = new LargeFireball(dragon.level, player, delta.x, delta.y, delta.z, 0);
            fireball.moveTo(dragonHeadPos.x, dragonHeadPos.y, dragonHeadPos.z, player.getXRot(), player.getYRot());
            dragon.level.addFreshEntity(fireball);
        } else {
            if (cooldown > 0) {
                --cooldown;
            }
        }
    }

    @Override
    public String type() {
        return FIRE_BALL;
    }
}
