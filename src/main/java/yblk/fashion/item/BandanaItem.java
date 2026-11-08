package yblk.fashion.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import yblk.fashion.Fashion;

/** Face bandana — Curios {@code mask} slot, no armor. Shape = PNG alpha. */
public class BandanaItem extends CuriosAccessoryItem {
    public static final int DURABILITY = 75;
    public static final String CURIOS_SLOT = "mask";

    private final TShirtColor color;

    public BandanaItem(TShirtColor color) {
        // durability() implies stacksTo(1) — do not also call stacksTo.
        super(new Properties().durability(DURABILITY), CURIOS_SLOT);
        this.color = color;
    }

    public TShirtColor getColor() {
        return color;
    }

    public ResourceLocation modelTexture() {
        return new ResourceLocation(Fashion.MODID, "textures/models/" + color.id + "_bandana.png");
    }

    public static boolean isBandana(ItemStack stack) {
        return stack.getItem() instanceof BandanaItem;
    }
}
