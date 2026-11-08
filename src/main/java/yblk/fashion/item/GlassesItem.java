package yblk.fashion.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import yblk.fashion.Fashion;

/**
 * Face glasses — Curios {@code mask}, no armor. Shape = PNG alpha over 2nd skin
 * (leave hat layer on; shell inflate above hat).
 */
public class GlassesItem extends CuriosAccessoryItem {
    public static final int DURABILITY = 50;
    public static final String CURIOS_SLOT = "mask";

    private final String textureId;

    public GlassesItem(String textureId) {
        super(new Properties().durability(DURABILITY), CURIOS_SLOT);
        this.textureId = textureId;
    }

    public String textureId() {
        return textureId;
    }

    public ResourceLocation modelTexture() {
        return new ResourceLocation(Fashion.MODID, "textures/models/" + textureId + ".png");
    }

    public static boolean isGlasses(ItemStack stack) {
        return stack.getItem() instanceof GlassesItem;
    }
}
