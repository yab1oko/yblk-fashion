package yblk.fashion.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import yblk.fashion.Fashion;

/** Full-head balaclava — Curios {@code mask} slot. Shape = PNG (eye/mouth holes). */
public class BalaclavaItem extends CuriosAccessoryItem {
    public static final int DURABILITY = 75;
    public static final String CURIOS_SLOT = "mask";

    private final TShirtColor color;

    public BalaclavaItem(TShirtColor color) {
        super(new Properties().durability(DURABILITY), CURIOS_SLOT);
        this.color = color;
    }

    public TShirtColor getColor() {
        return color;
    }

    public ResourceLocation modelTexture() {
        return new ResourceLocation(Fashion.MODID, "textures/models/" + color.id + "_balaclava.png");
    }

    public static boolean isBalaclava(ItemStack stack) {
        return stack.getItem() instanceof BalaclavaItem;
    }
}
