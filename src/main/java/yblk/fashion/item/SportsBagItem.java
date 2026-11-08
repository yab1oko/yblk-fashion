package yblk.fashion.item;

import net.minecraft.world.item.ItemStack;

/** Curios {@code body} sports bag — 9 portable bag slots (+9 Features while worn). */
public class SportsBagItem extends PortableBagItem {
    public static final int FEATURES_SLOTS = 9;
    public static final int BAG_SLOTS = 9;

    public SportsBagItem(TShirtColor color) {
        super(color, BAG_SLOTS, FEATURES_SLOTS, "sports_bag");
    }

    public static boolean isSportsBag(ItemStack stack) {
        return stack.getItem() instanceof SportsBagItem;
    }
}
