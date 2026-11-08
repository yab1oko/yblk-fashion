package yblk.fashion.item;

import net.minecraft.world.item.ItemStack;

/** Curios {@code body} backpack — 12 portable bag slots (+12 Features while worn). */
public class BackpackItem extends PortableBagItem {
    public static final int FEATURES_SLOTS = 12;
    public static final int BAG_SLOTS = 12;

    public BackpackItem(TShirtColor color) {
        super(color, BAG_SLOTS, FEATURES_SLOTS, "backpack");
    }

    public static boolean isBackpack(ItemStack stack) {
        return stack.getItem() instanceof BackpackItem;
    }
}
