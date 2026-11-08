package yblk.fashion.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import yblk.fashion.item.PortableBagItem;

import java.util.function.Supplier;

/**
 * Client → server: set which bag stack comes out next after a mouse-wheel cycle.
 * {@code slotIndex} {@code < 0} means the carried (cursor) stack.
 */
public record CycleClutchPacket(int slotIndex, int selected) {
    public static void encode(CycleClutchPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.slotIndex);
        buf.writeVarInt(packet.selected);
    }

    public static CycleClutchPacket decode(FriendlyByteBuf buf) {
        return new CycleClutchPacket(buf.readVarInt(), buf.readVarInt());
    }

    public static void handle(CycleClutchPacket packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            ItemStack bag;
            if (packet.slotIndex < 0) {
                bag = player.containerMenu.getCarried();
            } else {
                if (packet.slotIndex >= player.containerMenu.slots.size()) {
                    return;
                }
                Slot slot = player.containerMenu.getSlot(packet.slotIndex);
                if (!slot.allowModification(player)) {
                    return;
                }
                bag = slot.getItem();
            }
            if (PortableBagItem.setSelectedSlot(bag, packet.selected)) {
                player.containerMenu.broadcastChanges();
            }
        });
        context.setPacketHandled(true);
    }
}
