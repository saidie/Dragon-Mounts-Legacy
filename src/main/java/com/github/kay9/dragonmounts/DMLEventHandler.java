package com.github.kay9.dragonmounts;

import com.github.kay9.dragonmounts.dragon.TameableDragon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

import static com.github.kay9.dragonmounts.client.KeyMap.DRAGON_ABILITY;

@Mod.EventBusSubscriber(modid = DragonMountsLegacy.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DMLEventHandler {

    public static final int MSG_ID_SIMPLE_MESSAGE = 1;
    private static final String PROTOCOL_VERSION = "1";

    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(DragonMountsLegacy.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    static {
        INSTANCE.registerMessage(MSG_ID_SIMPLE_MESSAGE, KeyStateMessage.class, KeyStateMessage::encode, KeyStateMessage::new, KeyStateMessage::messageHandler);
    }

    private static boolean abilityPressed;

    @SubscribeEvent
    public static void onClientTickEvent(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (DRAGON_ABILITY.isDown() != abilityPressed) {
            abilityPressed = DRAGON_ABILITY.isDown();
            INSTANCE.sendToServer(new KeyStateMessage("ability", abilityPressed));
        }
    }

    public static class KeyStateMessage {

        private final String key;
        private final boolean pressed;

        public KeyStateMessage(String key, boolean pressed) {
            this.key = key;
            this.pressed = pressed;
        }

        public KeyStateMessage(FriendlyByteBuf buf) {
            this.key = new String(buf.readByteArray());
            this.pressed = buf.readBoolean();
        }

        public static void encode(KeyStateMessage msg, FriendlyByteBuf buf) {
            buf.writeByteArray(msg.key.getBytes());
            buf.writeBoolean(msg.pressed);
        }

        public static void messageHandler(KeyStateMessage msg, Supplier<NetworkEvent.Context> ctx) {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();
                if (player == null) {
                    return;
                }

                Entity vehicle = player.getVehicle();
                if (vehicle instanceof TameableDragon dragon) {
                    if (msg.key.equals("ability")) {
                        dragon.abilityEnabled = msg.pressed;
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}
