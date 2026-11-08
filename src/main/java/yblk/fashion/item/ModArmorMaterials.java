package yblk.fashion.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import yblk.fashion.Fashion;
import yblk.fashion.init.ModTags;

import java.util.function.Supplier;

/** Light cloth clothing (not leather — avoids dye overlay). */
public enum ModArmorMaterials implements ArmorMaterial {
    /** boots, leggings, chest, helmet — chest defense 1, dur 120 */
    CLOTH("cloth", 1, new int[]{0, 0, 1, 0}, 5, SoundEvents.ARMOR_EQUIP_LEATHER, 0f, 0f, () -> Ingredient.EMPTY),
    /** Tank top — no defense, chest dur 100 */
    TANK("tank", 1, new int[]{0, 0, 0, 0}, 5, SoundEvents.ARMOR_EQUIP_LEATHER, 0f, 0f, () -> Ingredient.EMPTY),
    /** Vest — chest defense 5, dur 400 */
    VEST("vest", 1, new int[]{0, 0, 5, 0}, 12, SoundEvents.ARMOR_EQUIP_IRON, 0f, 0f, () -> Ingredient.EMPTY),
    /** Tactical helmet — helmet defense 3, dur 250 */
    HELMET("helmet", 1, new int[]{0, 0, 0, 3}, 10, SoundEvents.ARMOR_EQUIP_IRON, 0f, 0f, () -> Ingredient.EMPTY),
    /** Hoodie — chest defense 2, dur 150 */
    HOODIE("hoodie", 1, new int[]{0, 0, 2, 0}, 8, SoundEvents.ARMOR_EQUIP_LEATHER, 0f, 0f, () -> Ingredient.EMPTY),
    /** Jacket — chest defense 2, dur 170 */
    JACKET("jacket", 1, new int[]{0, 0, 2, 0}, 8, SoundEvents.ARMOR_EQUIP_LEATHER, 0f, 0f, () -> Ingredient.EMPTY),
    /** Coat — chest defense 2, dur 200 */
    COAT("coat", 1, new int[]{0, 0, 2, 0}, 9, SoundEvents.ARMOR_EQUIP_LEATHER, 0f, 0f, () -> Ingredient.EMPTY),
    /** Pants — leggings defense 1, dur 135 */
    PANTS("pants", 1, new int[]{0, 1, 0, 0}, 6, SoundEvents.ARMOR_EQUIP_LEATHER, 0f, 0f, () -> Ingredient.EMPTY),
    /** Shorts — leggings defense 1, dur 100 */
    SHORTS("shorts", 1, new int[]{0, 1, 0, 0}, 6, SoundEvents.ARMOR_EQUIP_LEATHER, 0f, 0f, () -> Ingredient.EMPTY),
    /** Sneakers — no defense, dur 100 */
    SNEAKERS("sneakers", 1, new int[]{0, 0, 0, 0}, 5, SoundEvents.ARMOR_EQUIP_LEATHER, 0f, 0f, () -> Ingredient.EMPTY),
    /** Leather shoes — boots defense 1, dur 130 */
    SHOES("shoes", 1, new int[]{1, 0, 0, 0}, 7, SoundEvents.ARMOR_EQUIP_LEATHER, 0f, 0f, () -> Ingredient.EMPTY),
    /** Hat — helmet defense 1, dur 110 */
    HAT("hat", 1, new int[]{0, 0, 0, 1}, 6, SoundEvents.ARMOR_EQUIP_LEATHER, 0f, 0f, () -> Ingredient.EMPTY),
    /**
     * Ghillie set — boots/legs/chest/helmet = 1/4/5/2 (total 12).
     * Repair with fabric or leaves.
     */
    GHILLIE("ghillie", 1, new int[]{1, 4, 5, 2}, 12, SoundEvents.ARMOR_EQUIP_LEATHER, 0f, 0f,
            () -> Ingredient.of(ModTags.SEWING_FABRIC)),
    /**
     * Scrap armor — scrap plating (1/3/4/2 boots/legs/chest/helmet), modest durability.
     * Repair with RPM {@code forged_iron_plate} when present.
     */
    SCRAP("scrap", 1, new int[]{1, 3, 4, 2}, 7, SoundEvents.ARMOR_EQUIP_IRON, 0f, 0f,
            ModArmorMaterials::scrapRepair);

    private final String name;
    private final int durabilityMultiplier;
    private final int[] slotProtections;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;

    ModArmorMaterials(String name, int durabilityMultiplier, int[] slotProtections, int enchantmentValue,
                      SoundEvent sound, float toughness, float knockbackResistance,
                      Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.slotProtections = slotProtections;
        this.enchantmentValue = enchantmentValue;
        this.sound = sound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return switch (this) {
            case CLOTH -> switch (type) {
                case CHESTPLATE -> 120;
                case HELMET, LEGGINGS, BOOTS -> 80;
            };
            case TANK -> switch (type) {
                case CHESTPLATE -> 100;
                case HELMET, LEGGINGS, BOOTS -> 70;
            };
            case VEST -> switch (type) {
                case CHESTPLATE -> 400;
                case HELMET, LEGGINGS, BOOTS -> 200;
            };
            case HELMET -> switch (type) {
                case HELMET -> 250;
                case CHESTPLATE, LEGGINGS, BOOTS -> 150;
            };
            case HOODIE -> switch (type) {
                case CHESTPLATE -> 150;
                case HELMET, LEGGINGS, BOOTS -> 100;
            };
            case JACKET -> switch (type) {
                case CHESTPLATE -> 170;
                case HELMET, LEGGINGS, BOOTS -> 110;
            };
            case COAT -> switch (type) {
                case CHESTPLATE -> 200;
                case HELMET, LEGGINGS, BOOTS -> 130;
            };
            case PANTS -> switch (type) {
                case LEGGINGS -> 135;
                case HELMET, CHESTPLATE, BOOTS -> 90;
            };
            case SHORTS -> switch (type) {
                case LEGGINGS -> 100;
                case HELMET, CHESTPLATE, BOOTS -> 70;
            };
            case SNEAKERS -> switch (type) {
                case BOOTS -> 100;
                case HELMET, CHESTPLATE, LEGGINGS -> 70;
            };
            case SHOES -> switch (type) {
                case BOOTS -> 130;
                case HELMET, CHESTPLATE, LEGGINGS -> 80;
            };
            case HAT -> switch (type) {
                case HELMET -> 110;
                case CHESTPLATE, LEGGINGS, BOOTS -> 70;
            };
            case GHILLIE -> switch (type) {
                case HELMET -> 145;
                case CHESTPLATE -> 195;
                case LEGGINGS -> 175;
                case BOOTS -> 160;
            };
            case SCRAP -> switch (type) {
                case HELMET -> 130;
                case CHESTPLATE -> 180;
                case LEGGINGS -> 155;
                case BOOTS -> 120;
            };
        };
    }

    private static Ingredient scrapRepair() {
        Item plate = ForgeRegistries.ITEMS.getValue(
                new ResourceLocation("roleplay_materials", "forged_iron_plate"));
        if (plate != null && plate != Items.AIR) {
            return Ingredient.of(plate);
        }
        return Ingredient.EMPTY;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return this.slotProtections[type.getSlot().getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.sound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @Override
    public String getName() {
        return Fashion.MODID + ":" + this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
