package yblk.fashion.item;

import net.minecraft.world.item.ItemStack;

/** Curios {@code body} clutch — 6 portable bag slots (+6 Features while worn). */
public class ClutchItem extends PortableBagItem {
    public static final int FEATURES_SLOTS = 6;
    public static final int BAG_SLOTS = 6;

    public ClutchItem(TShirtColor color) {
        super(color, BAG_SLOTS, FEATURES_SLOTS, "clutch");
    }

    public static boolean isClutch(ItemStack stack) {
        return stack.getItem() instanceof ClutchItem;
    }
}
