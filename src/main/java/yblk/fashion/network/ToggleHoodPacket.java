package yblk.fashion.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import yblk.fashion.compat.CuriosClothing;
import yblk.fashion.item.ClothingItem;

import java.util.function.Supplier;

/** Client → server: toggle open/closed hood on Curios (or legacy CHEST) outerwear. */
public record ToggleHoodPacket() {
    public static void encode(ToggleHoodPacket packet, FriendlyByteBuf buf) {
    }

    public static ToggleHoodPacket decode(FriendlyByteBuf buf) {
        return new ToggleHoodPacket();
    }

    public static void handle(ToggleHoodPacket packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            var curios = CuriosClothing.findOuterwear(player);
            if (curios.isPresent()) {
                ItemStack stack = curios.get().stack();
                if (stack.getItem() instanceof ClothingItem clothing
                        && clothing.getClothingType().hasHood()) {
                    ClothingItem.toggleHood(stack);
                    player.inventoryMenu.broadcastChanges();
                    return;
                }
            }
            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
            if (chest.getItem() instanceof ClothingItem clothing
                    && clothing.getClothingType().hasHood()) {
                ClothingItem.toggleHood(chest);
                player.setItemSlot(EquipmentSlot.CHEST, chest);
                player.inventoryMenu.broadcastChanges();
            }
        });
        context.setPacketHandled(true);
    }
}
