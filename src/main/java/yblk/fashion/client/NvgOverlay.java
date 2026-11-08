package yblk.fashion.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import yblk.fashion.Fashion;
import yblk.fashion.item.NvgItem;

/** Full-screen green tint while local player's NVG is powered. */
@Mod.EventBusSubscriber(modid = Fashion.MODID, value = Dist.CLIENT)
public final class NvgOverlay {
    /** ARGB — phosphor wash (between soft 0x18 and previous 0x55). */
    private static final int TINT = 0x3018A030;

    private NvgOverlay() {
    }

    @SubscribeEvent
    public static void onOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.VIGNETTE.type()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) {
            return;
        }
        ItemStack worn = CuriosApi.getCuriosInventory(mc.player)
                .resolve()
                .flatMap(inv -> inv.findFirstCurio(NvgItem::isNvg))
                .map(r -> r.stack())
                .orElse(ItemStack.EMPTY);
        if (worn.isEmpty() || !NvgItem.isActive(worn)) {
            return;
        }

        GuiGraphics graphics = event.getGuiGraphics();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        graphics.fill(0, 0, w, h, TINT);
    }
}
