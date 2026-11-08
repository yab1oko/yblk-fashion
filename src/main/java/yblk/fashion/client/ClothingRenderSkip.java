package yblk.fashion.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.fml.ModList;

/**
 * Shared first-person cull — must not hide clothing inside inventory / station GUIs,
 * and must not hide clothing during Features Real First Person body pass.
 */
final class ClothingRenderSkip {
    private ClothingRenderSkip() {
    }

    static boolean localFirstPersonWorld(AbstractClientPlayer player) {
        Minecraft mc = Minecraft.getInstance();
        if (player != mc.player
                || !mc.options.getCameraType().isFirstPerson()
                || mc.screen != null) {
            return false;
        }
        // Features RFP manually renders the third-person player in FP — keep garments.
        if (featuresRfpBodyPass()) {
            return false;
        }
        return true;
    }

    private static boolean featuresRfpBodyPass() {
        if (!ModList.get().isLoaded("features")) {
            return false;
        }
        try {
            Class<?> clazz = Class.forName("yblk.features.rfp.FpBodyRenderer");
            Object rendering = clazz.getMethod("isRendering").invoke(null);
            return rendering instanceof Boolean b && b;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
