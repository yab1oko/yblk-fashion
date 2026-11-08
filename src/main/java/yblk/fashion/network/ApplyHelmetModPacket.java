package yblk.fashion.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import yblk.fashion.item.CigaretteItem;
import yblk.fashion.item.ClothingMods;

import java.util.function.Supplier;

/**
 * Apply a cigarette mod to worn HEAD. Client sends the mod stack because creative
 * often does not sync the carried cursor to the server.
 */
public record ApplyHelmetModPacket(ItemStack mod) {
    public static void encode(ApplyHelmetModPacket packet, FriendlyByteBuf buf) {
        buf.writeItem(packet.mod);
    }

    public static ApplyHelmetModPacket decode(FriendlyByteBuf buf) {
        return new ApplyHelmetModPacket(buf.readItem());
    }

    public static void handle(ApplyHelmetModPacket packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null || !CigaretteItem.isCigarette(packet.mod)) {
                return;
            }
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            if (!ClothingMods.canAccept(helmet, packet.mod)) {
                return;
            }
            if (!ClothingMods.apply(helmet, packet.mod)) {
                return;
            }
            player.setItemSlot(EquipmentSlot.HEAD, helmet.copy());

            // Always consume from cursor like a real slot (creative too — no inventory clone).
            ItemStack carried = player.containerMenu.getCarried();
            if (CigaretteItem.isCigarette(carried)) {
                carried.shrink(1);
                if (carried.isEmpty()) {
                    player.containerMenu.setCarried(ItemStack.EMPTY);
                }
            } else if (!player.getAbilities().instabuild) {
                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    ItemStack stack = player.getInventory().getItem(i);
                    if (CigaretteItem.isCigarette(stack)) {
                        stack.shrink(1);
                        break;
                    }
                }
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
