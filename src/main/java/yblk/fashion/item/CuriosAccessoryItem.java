package yblk.fashion.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

/** Fashion accessory that only equips in a Curios slot. */
public class CuriosAccessoryItem extends Item implements ICurioItem {
    private final String curiosSlot;

    public CuriosAccessoryItem(Properties properties, String curiosSlot) {
        // Do not call stacksTo() here: durability() already forces max stack 1,
        // and Item.Properties throws "Unable to have damage AND stack".
        super(properties);
        this.curiosSlot = curiosSlot;
    }

    public String getCuriosSlot() {
        return curiosSlot;
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return curiosSlot.equals(slotContext.identifier());
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(SoundEvents.ARMOR_EQUIP_LEATHER, 1.0F, 1.0F);
    }
}
