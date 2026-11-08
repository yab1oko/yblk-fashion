package yblk.fashion.network;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Map player-inventory armor indices (36–39) back onto {@link EquipmentSlot}. */
final class ArmorSlotSync {
    private ArmorSlotSync() {
    }

    static void syncIfArmor(Player player, int inventorySlot, ItemStack clothing) {
        EquipmentSlot armor = switch (inventorySlot) {
            case 36 -> EquipmentSlot.FEET;
            case 37 -> EquipmentSlot.LEGS;
            case 38 -> EquipmentSlot.CHEST;
            case 39 -> EquipmentSlot.HEAD;
            default -> null;
        };
        if (armor != null) {
            player.setItemSlot(armor, clothing.copy());
        }
    }
}
