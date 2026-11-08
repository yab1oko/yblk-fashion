package yblk.fashion.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import yblk.fashion.item.ClothingMods;

import java.util.function.Supplier;

/**
 * Client → server: apply carried mod to fashion clothing in a player-inventory slot.
 * {@code inventorySlot} is {@link Slot#getSlotIndex()} (0–40), not the creative menu index.
 */
public record ApplyClothingModPacket(int inventorySlot) {
    public static void encode(ApplyClothingModPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.inventorySlot);
    }

    public static ApplyClothingModPacket decode(FriendlyByteBuf buf) {
        return new ApplyClothingModPacket(buf.readVarInt());
    }

    public static void handle(ApplyClothingModPacket packet, Supplier<NetworkEvent.Context> ctx) {
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
            ItemStack carried = player.containerMenu.getCarried();
            ItemStack clothing = inv.getItem(packet.inventorySlot);
            if (!ClothingMods.canAccept(clothing, carried)) {
                return;
            }
            if (!ClothingMods.apply(clothing, carried)) {
                return;
            }
            inv.setItem(packet.inventorySlot, clothing);
            ArmorSlotSync.syncIfArmor(player, packet.inventorySlot, clothing);
            carried.shrink(1);
            if (carried.isEmpty()) {
                player.containerMenu.setCarried(ItemStack.EMPTY);
            }
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

    static boolean isPlayerInventorySlot(Player player, Slot slot) {
        return slot != null && slot.container == player.getInventory();
    }
}
