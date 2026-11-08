package yblk.fashion.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yblk.fashion.Fashion;

import java.util.UUID;

/** Cigarette → +1 attack on HEAD; armor plate → +1 armor on CHEST / LEGS. */
@Mod.EventBusSubscriber(modid = Fashion.MODID)
public final class ClothingModAttributes {
    private static final UUID CIGARETTE_DAMAGE =
            UUID.fromString("b7e4d1a0-3c92-4f1b-9e6a-8d2f0c5b1748");
    private static final UUID PLATE_ARMOR =
            UUID.fromString("c8f5e2b1-4d03-5a2c-0f7b-9e3a1d6c2859");

    private ClothingModAttributes() {
    }

    @SubscribeEvent
    public static void onItemAttributes(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        EquipmentSlot slot = event.getSlotType();

        if (slot == EquipmentSlot.HEAD
                && ClothingMods.has(stack, ClothingMods.CIGARETTE)
                && stack.getItem() instanceof ClothingItem clothing
                && clothing.getClothingType().isHelmet()) {
            event.addModifier(
                    Attributes.ATTACK_DAMAGE,
                    new AttributeModifier(
                            CIGARETTE_DAMAGE,
                            Fashion.MODID + ":cigarette_damage",
                            1.0,
                            AttributeModifier.Operation.ADDITION));
        }

        if ((slot == EquipmentSlot.CHEST || slot == EquipmentSlot.LEGS)
                && ClothingMods.has(stack, ClothingMods.PLATE)
                && stack.getItem() instanceof ClothingItem clothing
                && clothing.getClothingType().acceptsPlate()) {
            event.addModifier(
                    Attributes.ARMOR,
                    new AttributeModifier(
                            PLATE_ARMOR,
                            Fashion.MODID + ":armor_plate",
                            1.0,
                            AttributeModifier.Operation.ADDITION));
        }
    }
}
