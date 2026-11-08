package yblk.fashion.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;
import yblk.fashion.item.NvgItem;

import java.util.function.Supplier;

/** Client → server: toggle powered state on worn Curios mask NVG. */
public record ToggleNvgPacket() {
    public static void encode(ToggleNvgPacket packet, FriendlyByteBuf buf) {
    }

    public static ToggleNvgPacket decode(FriendlyByteBuf buf) {
        return new ToggleNvgPacket();
    }

    public static void handle(ToggleNvgPacket packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            ItemStack worn = CuriosApi.getCuriosInventory(player)
                    .resolve()
                    .flatMap(inv -> inv.findFirstCurio(NvgItem::isNvg))
                    .map(r -> r.stack())
                    .orElse(ItemStack.EMPTY);
            if (worn.isEmpty()) {
                return;
            }
            boolean wantOn = !NvgItem.isActive(worn);
            if (wantOn && NvgItem.remaining(worn) <= NvgItem.MIN_REMAINING_TO_RUN) {
                return;
            }
            if (NvgItem.setActive(worn, wantOn)) {
                if (wantOn) {
                    NvgItem.applyNightVision(player);
                } else {
                    player.removeEffect(MobEffects.NIGHT_VISION);
                }
                player.inventoryMenu.broadcastChanges();
            }
        });
        context.setPacketHandled(true);
    }
}
