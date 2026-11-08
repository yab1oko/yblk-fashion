package yblk.fashion.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import yblk.fashion.Fashion;

import java.util.Optional;

public final class FashionNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Fashion.MODID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private static int id;

    private FashionNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(
                id++,
                ToggleHoodPacket.class,
                ToggleHoodPacket::encode,
                ToggleHoodPacket::decode,
                ToggleHoodPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        CHANNEL.registerMessage(
                id++,
                ApplyClothingModPacket.class,
                ApplyClothingModPacket::encode,
                ApplyClothingModPacket::decode,
                ApplyClothingModPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        CHANNEL.registerMessage(
                id++,
                RemoveClothingModPacket.class,
                RemoveClothingModPacket::encode,
                RemoveClothingModPacket::decode,
                RemoveClothingModPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        CHANNEL.registerMessage(
                id++,
                ApplyHelmetModPacket.class,
                ApplyHelmetModPacket::encode,
                ApplyHelmetModPacket::decode,
                ApplyHelmetModPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        CHANNEL.registerMessage(
                id++,
                RemoveHelmetModPacket.class,
                RemoveHelmetModPacket::encode,
                RemoveHelmetModPacket::decode,
                RemoveHelmetModPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        CHANNEL.registerMessage(
                id++,
                CycleClutchPacket.class,
                CycleClutchPacket::encode,
                CycleClutchPacket::decode,
                CycleClutchPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
        CHANNEL.registerMessage(
                id++,
                ToggleNvgPacket.class,
                ToggleNvgPacket::encode,
                ToggleNvgPacket::decode,
                ToggleNvgPacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER)
        );
    }
}
