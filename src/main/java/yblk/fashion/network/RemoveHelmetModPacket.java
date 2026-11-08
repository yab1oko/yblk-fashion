package yblk.fashion.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import yblk.fashion.item.ClothingMods;

import java.util.Optional;
import java.util.function.Supplier;

/** Extract a helmet mod onto the cursor like a normal inventory slot. */
public record RemoveHelmetModPacket(String modId) {
    public static void encode(RemoveHelmetModPacket packet, FriendlyByteBuf buf) {
        buf.writeUtf(packet.modId);
    }

    public static RemoveHelmetModPacket decode(FriendlyByteBuf buf) {
        return new RemoveHelmetModPacket(buf.readUtf());
    }

    public static void handle(RemoveHelmetModPacket packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            Optional<ItemStack> extracted = ClothingMods.remove(helmet, packet.modId);
            if (extracted.isEmpty()) {
                return;
            }
            player.setItemSlot(EquipmentSlot.HEAD, helmet.copy());
            ItemStack out = extracted.get();

            if (player.containerMenu.getCarried().isEmpty()) {
                player.containerMenu.setCarried(out);
            } else {
                player.getInventory().placeItemBackInInventory(out);
            }
            player.containerMenu.broadcastChanges();
            player.level().playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.ARMOR_EQUIP_LEATHER,
                    SoundSource.PLAYERS,
                    1.0F,
                    1.0F);
        });
        context.setPacketHandled(true);
    }
}
