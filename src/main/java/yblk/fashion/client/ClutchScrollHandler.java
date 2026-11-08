package yblk.fashion.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yblk.fashion.Fashion;
import yblk.fashion.compat.FeaturesCompat;
import yblk.fashion.item.PortableBagItem;
import yblk.fashion.network.CycleClutchPacket;
import yblk.fashion.network.FashionNetwork;

/**
 * Scroll over a clutch / sports bag (or while holding one) to choose which stack comes out next.
 */
@Mod.EventBusSubscriber(modid = Fashion.MODID, value = Dist.CLIENT)
public final class ClutchScrollHandler {
    private ClutchScrollHandler() {
    }

    @SubscribeEvent
    public static void onMouseScroll(ScreenEvent.MouseScrolled.Pre event) {
        if (FeaturesCompat.isFeaturesPresent()) {
            return;
        }
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen)) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        double delta = event.getScrollDelta();
        if (delta == 0) {
            return;
        }
        int direction = delta > 0 ? -1 : 1;

        ItemStack carried = screen.getMenu().getCarried();
        if (PortableBagItem.isBag(carried)) {
            if (tryCycle(carried, -1, direction)) {
                event.setCanceled(true);
            }
            return;
        }

        Slot hovered = screen.getSlotUnderMouse();
        if (hovered == null || !PortableBagItem.isBag(hovered.getItem())) {
            return;
        }
        if (tryCycle(hovered.getItem(), hovered.index, direction)) {
            event.setCanceled(true);
        }
    }

    private static boolean tryCycle(ItemStack bag, int menuSlotIndex, int direction) {
        if (!PortableBagItem.cycleSelected(bag, direction)) {
            return false;
        }
        FashionNetwork.CHANNEL.sendToServer(
                new CycleClutchPacket(menuSlotIndex, PortableBagItem.getSelectedSlotIndex(bag)));
        return true;
    }
}
