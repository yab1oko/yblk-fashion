package yblk.fashion.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import yblk.fashion.item.ClothingMods;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Client → server: extract a clothing mod onto empty cursor.
 * {@code inventorySlot} is {@link net.minecraft.world.inventory.Slot#getSlotIndex()}.
 */
public record RemoveClothingModPacket(int inventorySlot, String modId) {
    public static void encode(RemoveClothingModPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.inventorySlot);
        buf.writeUtf(packet.modId);
    }

    public static RemoveClothingModPacket decode(FriendlyByteBuf buf) {
        return new RemoveClothingModPacket(buf.readVarInt(), buf.readUtf());
    }

    public static void handle(RemoveClothingModPacket packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            Inventory inv = player.getInventory();
            if (packet.inventorySlot < 0 || packet.inventorySlot >= inv.getContainerSize()) {
                return;
            }
            if (!player.containerMenu.getCarried().isEmpty()) {
                return;
            }
            ItemStack clothing = inv.getItem(packet.inventorySlot);
            Optional<ItemStack> extracted = ClothingMods.remove(clothing, packet.modId);
            if (extracted.isEmpty()) {
                return;
            }
            inv.setItem(packet.inventorySlot, clothing);
            ArmorSlotSync.syncIfArmor(player, packet.inventorySlot, clothing);
            player.containerMenu.setCarried(extracted.get());
            player.containerMenu.broadcastChanges();
            player.inventoryMenu.broadcastChanges();
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
