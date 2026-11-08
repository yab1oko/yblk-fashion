package yblk.fashion.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Foliage/grass biome tint for ghillie (same source as leaves). */
@OnlyIn(Dist.CLIENT)
public final class GhillieColors {
    private GhillieColors() {
    }

    /** Packed RGB for entity at its feet. */
    public static int foliage(Entity entity) {
        if (entity == null || entity.level() == null) {
            return FoliageColor.getDefaultColor();
        }
        BlockPos pos = BlockPos.containing(entity.getX(), entity.getY(), entity.getZ());
        return BiomeColors.getAverageFoliageColor(entity.level(), pos);
    }

    /** Inventory / GUI tint — player position if available. */
    public static int foliageForGui() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.level != null) {
            return foliage(mc.player);
        }
        return FoliageColor.getDefaultColor();
    }

    public static float red(int rgb) {
        return ((rgb >> 16) & 0xFF) / 255.0F;
    }

    public static float green(int rgb) {
        return ((rgb >> 8) & 0xFF) / 255.0F;
    }

    public static float blue(int rgb) {
        return (rgb & 0xFF) / 255.0F;
    }

    public static int foliage(BlockAndTintGetter level, BlockPos pos) {
        return BiomeColors.getAverageFoliageColor(level, pos);
    }
}
