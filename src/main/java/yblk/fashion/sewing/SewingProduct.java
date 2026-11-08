package yblk.fashion.sewing;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import yblk.fashion.init.ModItems;
import yblk.fashion.item.ClothingFabric;
import yblk.fashion.item.ClothingType;
import yblk.fashion.item.TShirtColor;

import java.util.List;
import java.util.function.Supplier;

/**
 * A craftable garment at the sewing station. Dye in the station slot picks the color.
 * {@link #sewingKitCount} is consumed only when RPM {@code sewing_kit} is loaded.
 */
public record SewingProduct(
        String id,
        Component title,
        int fabricCount,
        int leatherCount,
        int wheatCount,
        int leavesCount,
        int sewingKitCount,
        boolean needsDye,
        Supplier<ItemStack> icon
) {
    private static List<SewingProduct> cache;

    public static List<SewingProduct> all() {
        if (cache == null) {
            cache = List.of(
                    product("tshirt", ClothingType.TSHIRT,
                            () -> new ItemStack(ModItems.TSHIRTS.get(TShirtColor.RED).get())),
                    product("tank_top", ClothingType.TANK_TOP,
                            () -> new ItemStack(ModItems.TANK_TOPS.get(TShirtColor.WHITE).get())),
                    product("vest", ClothingType.VEST,
                            () -> new ItemStack(ModItems.VESTS.get(TShirtColor.BLACK).get())),
                    undyed("helmet", ClothingType.HELMET,
                            () -> new ItemStack(ModItems.HELMET.get())),
                    product("pants", ClothingType.PANTS,
                            () -> new ItemStack(ModItems.PANTS.get(TShirtColor.RED).get())),
                    product("shorts", ClothingType.SHORTS,
                            () -> new ItemStack(ModItems.SHORTS.get(TShirtColor.RED).get())),
                    product("hoodie", ClothingType.HOODIE,
                            () -> new ItemStack(ModItems.HOODIES.get(TShirtColor.RED).get())),
                    product("jacket", ClothingType.JACKET,
                            () -> new ItemStack(ModItems.JACKETS.get(TShirtColor.RED).get())),
                    product("coat", ClothingType.COAT,
                            () -> new ItemStack(ModItems.COATS.get(TShirtColor.BROWN).get())),
                    product("sneakers", ClothingType.SNEAKERS,
                            () -> new ItemStack(ModItems.SNEAKERS.get(TShirtColor.RED).get())),
                    product("shoes", ClothingType.SHOES,
                            () -> new ItemStack(ModItems.SHOES.get(TShirtColor.BROWN).get())),
                    product("hat", ClothingType.HAT,
                            () -> new ItemStack(ModItems.HATS.get(TShirtColor.RED).get())),
                    product("cowboy_hat", ClothingType.COWBOY_HAT,
                            () -> new ItemStack(ModItems.COWBOY_HATS.get(TShirtColor.BROWN).get())),
                    undyed("straw_hat", ClothingType.STRAW_HAT,
                            () -> new ItemStack(ModItems.STRAW_HAT.get())),
                    product("cap", ClothingType.CAP,
                            () -> new ItemStack(ModItems.CAPS.get(TShirtColor.RED).get())),
                    bandana(),
                    balaclava(),
                    nvg(),
                    gasMask(),
                    glasses(),
                    blackGlasses(),
                    clutch(),
                    sportsBag(),
                    backpack(),
                    undyed("ghillie_hood", ClothingType.GHILLIE_HOOD,
                            () -> new ItemStack(ModItems.GHILLIE_HOOD.get())),
                    undyed("ghillie_top", ClothingType.GHILLIE_TOP,
                            () -> new ItemStack(ModItems.GHILLIE_TOP.get())),
                    undyed("ghillie_pants", ClothingType.GHILLIE_PANTS,
                            () -> new ItemStack(ModItems.GHILLIE_PANTS.get())),
                    undyed("ghillie_boots", ClothingType.GHILLIE_BOOTS,
                            () -> new ItemStack(ModItems.GHILLIE_BOOTS.get()))
            );
        }
        return cache;
    }

    /** Kits to consume right now (0 if RPM sewing_kit is absent). */
    public int effectiveSewingKitCost() {
        return ClothingFabric.needsSewingKit() ? Math.max(0, sewingKitCount) : 0;
    }

    /** 2 fabric + dye (+ kit if RPM). */
    private static SewingProduct bandana() {
        return custom("bandana", 2, 0, 0, 0, 1, true,
                () -> new ItemStack(ModItems.blackBandana()));
    }

    /** 3 fabric + dye (+ kit if RPM). */
    private static SewingProduct balaclava() {
        return custom("balaclava", 3, 0, 0, 0, 1, true,
                () -> new ItemStack(ModItems.blackBalaclava()));
    }

    /** 1 fabric + 3 plates (+ kit if RPM) — undyed tactical NVG. */
    private static SewingProduct nvg() {
        return custom("nvg", 1, 0, 0, 0, 1, false,
                () -> new ItemStack(ModItems.NVG.get()));
    }

    /** 2 fabric + 2 plates (+ kit if RPM) — undyed gas mask, no filters. */
    private static SewingProduct gasMask() {
        return custom("gas_mask", 2, 0, 0, 0, 1, false,
                () -> new ItemStack(ModItems.GAS_MASK.get()));
    }

    /** 1 fabric (+ kit if RPM) — gray glasses. */
    private static SewingProduct glasses() {
        return custom("glasses", 1, 0, 0, 0, 1, false,
                () -> new ItemStack(ModItems.GLASSES.get()));
    }

    /** 1 fabric (+ kit if RPM) — black glasses. */
    private static SewingProduct blackGlasses() {
        return custom("black_glasses", 1, 0, 0, 0, 1, false,
                () -> new ItemStack(ModItems.BLACK_GLASSES.get()));
    }

    /** 4 fabric + dye (+ kit if RPM). */
    private static SewingProduct clutch() {
        return custom("clutch", 4, 0, 0, 0, 1, true,
                () -> new ItemStack(ModItems.CLUTCHES.get(TShirtColor.BLACK).get()));
    }

    /** 6 fabric + 2 leather + dye (+ 2 kits if RPM). */
    private static SewingProduct sportsBag() {
        return custom("sports_bag", 6, 2, 0, 0, 2, true,
                () -> new ItemStack(ModItems.SPORTS_BAGS.get(TShirtColor.RED).get()));
    }

    /** 8 fabric + 3 leather + dye (+ 3 kits if RPM). */
    private static SewingProduct backpack() {
        return custom("backpack", 8, 3, 0, 0, 3, true,
                () -> new ItemStack(ModItems.BACKPACKS.get(TShirtColor.RED).get()));
    }

    private static SewingProduct custom(
            String id,
            int fabric,
            int leather,
            int wheat,
            int leaves,
            int kits,
            boolean dye,
            Supplier<ItemStack> icon) {
        return new SewingProduct(
                id,
                Component.translatable("sewing.fashion." + id),
                fabric,
                leather,
                wheat,
                leaves,
                kits,
                dye,
                icon
        );
    }

    private static SewingProduct product(String id, ClothingType type, Supplier<ItemStack> icon) {
        return new SewingProduct(
                id,
                Component.translatable("sewing.fashion." + id),
                type.sewFabricCost(),
                type.sewLeatherCost(),
                type.sewWheatCost(),
                type.sewLeavesCost(),
                1,
                true,
                icon
        );
    }

    private static SewingProduct undyed(String id, ClothingType type, Supplier<ItemStack> icon) {
        return new SewingProduct(
                id,
                Component.translatable("sewing.fashion." + id),
                type.sewFabricCost(),
                type.sewLeatherCost(),
                type.sewWheatCost(),
                type.sewLeavesCost(),
                1,
                false,
                icon
        );
    }

    public static SewingProduct byIndex(int index) {
        List<SewingProduct> list = all();
        if (index < 0 || index >= list.size()) {
            return list.get(0);
        }
        return list.get(index);
    }

    /** RPM cloth only when present; otherwise vanilla wool. */
    public Ingredient fabric() {
        return ClothingFabric.sewingFabricIngredient();
    }

    /** Iron ingot and/or RPM forged iron plate. */
    public int plateCount() {
        return switch (id) {
            case "vest" -> ClothingType.VEST.sewPlateCost();
            case "helmet" -> ClothingType.HELMET.sewPlateCost();
            case "nvg" -> 3;
            case "gas_mask" -> 2;
            default -> 0;
        };
    }

    public Ingredient plates() {
        return ClothingFabric.sewingPlateIngredient();
    }

    /** Any block in {@code #minecraft:leaves}. */
    public Ingredient leaves() {
        return Ingredient.of(ItemTags.LEAVES);
    }

    public ItemStack iconStack() {
        return icon.get().copy();
    }

    /**
     * Mannequin stack for the sewing GUI: selected dye when valid, otherwise the
     * catalog icon (never empty for restricted colors like coat / shoes / masks).
     */
    public ItemStack previewStack(@Nullable TShirtColor dye) {
        if (!needsDye) {
            return iconStack();
        }
        if (dye != null && acceptsDye(dye)) {
            ItemStack dyed = resultForDye(dye);
            if (!dyed.isEmpty()) {
                return dyed;
            }
        }
        return iconStack();
    }

    public ItemStack resultForDye(TShirtColor color) {
        return switch (id) {
            case "tshirt" -> new ItemStack(ModItems.TSHIRTS.get(color).get());
            case "tank_top" -> color.hasTankTop()
                    ? new ItemStack(ModItems.TANK_TOPS.get(color).get())
                    : ItemStack.EMPTY;
            case "vest" -> color.hasVest()
                    ? new ItemStack(ModItems.VESTS.get(color).get())
                    : ItemStack.EMPTY;
            case "helmet" -> new ItemStack(ModItems.HELMET.get());
            case "pants" -> new ItemStack(ModItems.PANTS.get(color).get());
            case "shorts" -> new ItemStack(ModItems.SHORTS.get(color).get());
            case "hoodie" -> new ItemStack(ModItems.HOODIES.get(color).get());
            case "jacket" -> new ItemStack(ModItems.JACKETS.get(color).get());
            case "coat" -> color.hasCoat()
                    ? new ItemStack(ModItems.COATS.get(color).get())
                    : ItemStack.EMPTY;
            case "sneakers" -> new ItemStack(ModItems.SNEAKERS.get(color).get());
            case "shoes" -> color.hasShoes()
                    ? new ItemStack(ModItems.SHOES.get(color).get())
                    : ItemStack.EMPTY;
            case "hat" -> new ItemStack(ModItems.HATS.get(color).get());
            case "cowboy_hat" -> color.hasCowboyHat()
                    ? new ItemStack(ModItems.COWBOY_HATS.get(color).get())
                    : ItemStack.EMPTY;
            case "straw_hat" -> new ItemStack(ModItems.STRAW_HAT.get());
            case "cap" -> new ItemStack(ModItems.CAPS.get(color).get());
            case "bandana" -> color.hasBandana()
                    ? new ItemStack(ModItems.BANDANAS.get(color).get())
                    : ItemStack.EMPTY;
            case "balaclava" -> color.hasBalaclava()
                    ? new ItemStack(ModItems.BALACLAVAS.get(color).get())
                    : ItemStack.EMPTY;
            case "nvg" -> new ItemStack(ModItems.NVG.get());
            case "gas_mask" -> new ItemStack(ModItems.GAS_MASK.get());
            case "glasses" -> new ItemStack(ModItems.GLASSES.get());
            case "black_glasses" -> new ItemStack(ModItems.BLACK_GLASSES.get());
            case "clutch" -> color.hasClutch()
                    ? new ItemStack(ModItems.CLUTCHES.get(color).get())
                    : ItemStack.EMPTY;
            case "sports_bag" -> color.hasSportsBag()
                    ? new ItemStack(ModItems.SPORTS_BAGS.get(color).get())
                    : ItemStack.EMPTY;
            case "backpack" -> color.hasBackpack()
                    ? new ItemStack(ModItems.BACKPACKS.get(color).get())
                    : ItemStack.EMPTY;
            case "ghillie_hood" -> new ItemStack(ModItems.GHILLIE_HOOD.get());
            case "ghillie_top" -> new ItemStack(ModItems.GHILLIE_TOP.get());
            case "ghillie_pants" -> new ItemStack(ModItems.GHILLIE_PANTS.get());
            case "ghillie_boots" -> new ItemStack(ModItems.GHILLIE_BOOTS.get());
            default -> iconStack();
        };
    }

    /** Shoes / cowboy hat only accept black / brown / orange (light brown). */
    public boolean acceptsDye(TShirtColor color) {
        if (!needsDye) {
            return true;
        }
        if ("tank_top".equals(id)) {
            return color != null && color.hasTankTop();
        }
        if ("vest".equals(id)) {
            return color != null && color.hasVest();
        }
        if ("shoes".equals(id)) {
            return color != null && color.hasShoes();
        }
        if ("cowboy_hat".equals(id)) {
            return color != null && color.hasCowboyHat();
        }
        if ("coat".equals(id)) {
            return color != null && color.hasCoat();
        }
        if ("bandana".equals(id)) {
            return color != null && color.hasBandana();
        }
        if ("balaclava".equals(id)) {
            return color != null && color.hasBalaclava();
        }
        if ("clutch".equals(id)) {
            return color != null && color.hasClutch();
        }
        if ("sports_bag".equals(id)) {
            return color != null && color.hasSportsBag();
        }
        if ("backpack".equals(id)) {
            return color != null && color.hasBackpack();
        }
        return color != null;
    }
}
