package yblk.fashion.client;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClothingType;

/**
 * Ghillie camouflage coverage — 2nd-skin hide regions and which extras to suppress.
 * Any worn ghillie piece blanks its body region; wearing the set (any piece) also
 * hides fashion curios, helmet mods, and overlapping clothing visuals.
 */
public final class GhillieCover {
    private GhillieCover() {
    }

    public static boolean isGhillie(ItemStack stack) {
        return stack.getItem() instanceof ClothingItem c && c.getClothingType().isGhillie();
    }

    public static boolean hood(LivingEntity entity) {
        return isType(entity, EquipmentSlot.HEAD, ClothingType.GHILLIE_HOOD);
    }

    public static boolean top(LivingEntity entity) {
        return isType(entity, EquipmentSlot.CHEST, ClothingType.GHILLIE_TOP);
    }

    public static boolean pants(LivingEntity entity) {
        return isType(entity, EquipmentSlot.LEGS, ClothingType.GHILLIE_PANTS);
    }

    public static boolean boots(LivingEntity entity) {
        return isType(entity, EquipmentSlot.FEET, ClothingType.GHILLIE_BOOTS);
    }

    /** Any ghillie piece equipped — camouflage mode. */
    public static boolean any(LivingEntity entity) {
        return hood(entity) || top(entity) || pants(entity) || boots(entity);
    }

    /**
     * Hide Curios cosmetics, clothing mods (cigarette), outerwear mesh, masks, etc.
     * Active when the camouflage suit is on the torso/head (or a full lower pair).
     */
    public static boolean hidesExtras(LivingEntity entity) {
        if (hood(entity) || top(entity)) {
            return true;
        }
        return pants(entity) && boots(entity);
    }

    private static boolean isType(LivingEntity entity, EquipmentSlot slot, ClothingType type) {
        ItemStack stack = entity.getItemBySlot(slot);
        return stack.getItem() instanceof ClothingItem c && c.getClothingType() == type;
    }
}
