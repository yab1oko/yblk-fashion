package yblk.fashion.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import yblk.fashion.init.ModTags;

/**
 * Soft RPM fabric rules: with Roleplay Materials only {@code cloth} counts as sewing
 * fabric; without it, vanilla wool is used. Uncraft follows the same preference.
 */
public final class ClothingFabric {
    private static final ResourceLocation CLOTH_ID =
            new ResourceLocation("roleplay_materials", "cloth");
    private static final ResourceLocation SEWING_KIT_ID =
            new ResourceLocation("roleplay_materials", "sewing_kit");
    private static final ResourceLocation FORGED_IRON_PLATE_ID =
            new ResourceLocation("roleplay_materials", "forged_iron_plate");

    /** Kits consumed per garment when RPM sewing_kit is loaded. */
    public static final int SEWING_KIT_COST = 1;

    private ClothingFabric() {
    }

    /** Prefer RPM cloth when the mod is present; otherwise colored wool. Shoes → leather. */
    public static ItemStack uncraftOutput(ClothingItem clothing) {
        if (clothing.getClothingType() == ClothingType.STRAW_HAT) {
            return new ItemStack(Items.WHEAT, 2);
        }
        if (clothing.getClothingType() == ClothingType.SHOES
                || clothing.getClothingType() == ClothingType.COWBOY_HAT) {
            int leather = clothing.getClothingType() == ClothingType.SHOES ? 2 : 1;
            return new ItemStack(Items.LEATHER, leather);
        }
        int count = Math.max(1, clothing.getClothingType().uncraftFabricCount());
        Item cloth = clothItem();
        if (cloth != null) {
            return new ItemStack(cloth, count);
        }
        return new ItemStack(woolFor(clothing.getColor()), count);
    }

    /** Bags: fabric cost − 1 (sewing station fabric count minus one). */
    public static ItemStack uncraftBagOutput(PortableBagItem bag) {
        int fabric = 1;
        if (bag instanceof ClutchItem) {
            fabric = 3;
        } else if (bag instanceof SportsBagItem) {
            fabric = 5;
        } else if (bag instanceof BackpackItem) {
            fabric = 7;
        }
        return uncraftSimpleFabric(fabric);
    }

    public static ItemStack uncraftSimpleFabric(int count) {
        int n = Math.max(1, count);
        Item cloth = clothItem();
        if (cloth != null) {
            return new ItemStack(cloth, n);
        }
        return new ItemStack(Items.WHITE_WOOL, n);
    }

    /**
     * With RPM: only cloth. Without: wool (and anything else in {@link ModTags#SEWING_FABRIC}).
     */
    public static boolean isSewingFabric(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        Item cloth = clothItem();
        if (cloth != null) {
            return stack.is(cloth);
        }
        return stack.is(ItemTags.WOOL) || stack.is(ModTags.SEWING_FABRIC);
    }

    public static Ingredient sewingFabricIngredient() {
        Item cloth = clothItem();
        if (cloth != null) {
            return Ingredient.of(cloth);
        }
        return Ingredient.of(ItemTags.WOOL);
    }

    /** Icon for the sewing station fabric requirement. */
    public static ItemStack sewingFabricIcon(int count) {
        Item cloth = clothItem();
        if (cloth != null) {
            return new ItemStack(cloth, Math.max(1, count));
        }
        return new ItemStack(Items.WHITE_WOOL, Math.max(1, count));
    }

    @Nullable
    public static Item clothItem() {
        Item item = ForgeRegistries.ITEMS.getValue(CLOTH_ID);
        return item != null && item != Items.AIR ? item : null;
    }

    public static boolean hasCloth() {
        return clothItem() != null;
    }

    /** Soft RPM dependency — null when Roleplay Materials is absent. */
    @Nullable
    public static Item sewingKitItem() {
        Item item = ForgeRegistries.ITEMS.getValue(SEWING_KIT_ID);
        return item != null && item != Items.AIR ? item : null;
    }

    public static boolean needsSewingKit() {
        return sewingKitItem() != null;
    }

    public static ItemStack sewingKitIcon() {
        return sewingKitIcon(SEWING_KIT_COST);
    }

    public static ItemStack sewingKitIcon(int count) {
        Item kit = sewingKitItem();
        return kit != null ? new ItemStack(kit, Math.max(1, count)) : ItemStack.EMPTY;
    }

    /**
     * Soft RPM: prefer {@code forged_iron_plate} when present; always also accept
     * iron ingots. Used for plate-carrier sewing.
     */
    @Nullable
    public static Item forgedIronPlateItem() {
        Item item = ForgeRegistries.ITEMS.getValue(FORGED_IRON_PLATE_ID);
        return item != null && item != Items.AIR ? item : null;
    }

    public static boolean isSewingPlate(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        if (stack.is(Items.IRON_INGOT)) {
            return true;
        }
        Item plate = forgedIronPlateItem();
        return plate != null && stack.is(plate);
    }

    public static Ingredient sewingPlateIngredient() {
        Item plate = forgedIronPlateItem();
        if (plate != null) {
            return Ingredient.of(Items.IRON_INGOT, plate);
        }
        return Ingredient.of(Items.IRON_INGOT);
    }

    /** Icon prefers RPM plate when loaded. */
    public static ItemStack sewingPlateIcon(int count) {
        int n = Math.max(1, count);
        Item plate = forgedIronPlateItem();
        if (plate != null) {
            return new ItemStack(plate, n);
        }
        return new ItemStack(Items.IRON_INGOT, n);
    }

    public static Item woolFor(@Nullable TShirtColor color) {
        if (color == null) {
            return Items.WHITE_WOOL;
        }
        Item wool = ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", color.woolItemId()));
        return wool != null && wool != Items.AIR ? wool : Items.WHITE_WOOL;
    }
}
