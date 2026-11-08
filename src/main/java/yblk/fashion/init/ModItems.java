package yblk.fashion.init;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import yblk.fashion.Fashion;
import yblk.fashion.item.BalaclavaItem;
import yblk.fashion.item.BandanaItem;
import yblk.fashion.item.CigaretteItem;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClothingType;
import yblk.fashion.item.BackpackItem;
import yblk.fashion.item.ClutchItem;
import yblk.fashion.item.GasMaskItem;
import yblk.fashion.item.GlassesItem;
import yblk.fashion.item.NvgItem;
import yblk.fashion.item.ArmorPlateItem;
import yblk.fashion.item.PouchItem;
import yblk.fashion.item.SportsBagItem;
import yblk.fashion.item.TShirtColor;

import java.util.EnumMap;
import java.util.Map;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Fashion.MODID);

    public static final Map<TShirtColor, RegistryObject<Item>> TSHIRTS = new EnumMap<>(TShirtColor.class);
    /** White / black only. */
    public static final Map<TShirtColor, RegistryObject<Item>> TANK_TOPS = new EnumMap<>(TShirtColor.class);
    /** Black / green only — Curios {@code vest}. */
    public static final Map<TShirtColor, RegistryObject<Item>> VESTS = new EnumMap<>(TShirtColor.class);
    public static final Map<TShirtColor, RegistryObject<Item>> HOODIES = new EnumMap<>(TShirtColor.class);
    public static final Map<TShirtColor, RegistryObject<Item>> JACKETS = new EnumMap<>(TShirtColor.class);
    /** Black / brown only. */
    public static final Map<TShirtColor, RegistryObject<Item>> COATS = new EnumMap<>(TShirtColor.class);
    public static final Map<TShirtColor, RegistryObject<Item>> PANTS = new EnumMap<>(TShirtColor.class);
    public static final Map<TShirtColor, RegistryObject<Item>> SHORTS = new EnumMap<>(TShirtColor.class);
    public static final Map<TShirtColor, RegistryObject<Item>> SNEAKERS = new EnumMap<>(TShirtColor.class);
    /** Black / brown / light_brown (orange dye) only. */
    public static final Map<TShirtColor, RegistryObject<Item>> SHOES = new EnumMap<>(TShirtColor.class);
    public static final Map<TShirtColor, RegistryObject<Item>> HATS = new EnumMap<>(TShirtColor.class);
    /** Black / brown / light_brown (orange dye) only. */
    public static final Map<TShirtColor, RegistryObject<Item>> COWBOY_HATS = new EnumMap<>(TShirtColor.class);
    public static final Map<TShirtColor, RegistryObject<Item>> CAPS = new EnumMap<>(TShirtColor.class);

    public static final RegistryObject<Item> STRAW_HAT = ITEMS.register("straw_hat",
            () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.STRAW_HAT));

    /** Tactical helmet — HELMET slot, no Features slots. */
    public static final RegistryObject<Item> HELMET = ITEMS.register("helmet",
            () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.HELMET));

    /** Scrap helmet — HELMET slot, iron stats, craft from forged iron plate. */
    public static final RegistryObject<Item> SCRAP_HELMET = ITEMS.register("scrap_helmet",
            () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.SCRAP_HELMET));

    /** Scrap chestplate — Curios {@code vest}, iron stats. */
    public static final RegistryObject<Item> SCRAP_CHESTPLATE = ITEMS.register("scrap_chestplate",
            () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.SCRAP_VEST));

    /** Scrap pants — Curios {@code pants}, iron stats. */
    public static final RegistryObject<Item> SCRAP_PANTS = ITEMS.register("scrap_pants",
            () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.SCRAP_PANTS));

    /** Black / blue / red — Curios {@code mask}, durability {@link BandanaItem#DURABILITY}. */
    public static final Map<TShirtColor, RegistryObject<Item>> BANDANAS = new EnumMap<>(TShirtColor.class);

    /** Black / white — Curios {@code mask}, durability {@link BalaclavaItem#DURABILITY}. */
    public static final Map<TShirtColor, RegistryObject<Item>> BALACLAVAS = new EnumMap<>(TShirtColor.class);

    /** Night-vision goggles — Curios {@code mask}, no armor, {@link NvgItem#DURABILITY}. */
    public static final RegistryObject<Item> NVG = ITEMS.register("nvg", NvgItem::new);
    public static final RegistryObject<Item> GAS_MASK = ITEMS.register("gas_mask", GasMaskItem::new);
    public static final RegistryObject<Item> GLASSES = ITEMS.register("glasses",
            () -> new GlassesItem("glasses"));
    public static final RegistryObject<Item> BLACK_GLASSES = ITEMS.register("black_glasses",
            () -> new GlassesItem("black_glasses"));

    /** Black / blue / orange / yellow — Curios {@code body} (backpack). */
    public static final Map<TShirtColor, RegistryObject<Item>> CLUTCHES = new EnumMap<>(TShirtColor.class);

    /** Purple / red / gray / green — Curios {@code body} sports bag. */
    public static final Map<TShirtColor, RegistryObject<Item>> SPORTS_BAGS = new EnumMap<>(TShirtColor.class);

    /** Black / red / green — Curios {@code body} backpack. */
    public static final Map<TShirtColor, RegistryObject<Item>> BACKPACKS = new EnumMap<>(TShirtColor.class);

    /** Helmet modification (not Curios); +1 attack damage while on worn hat. */
    public static final RegistryObject<Item> CIGARETTE = ITEMS.register("cigarette", CigaretteItem::new);

    /** Body/legs thigh pouch modification (not Curios). */
    public static final RegistryObject<Item> POUCH = ITEMS.register("pouch", PouchItem::new);

    /** Chest/legs armor plate modification — +1 armor, no mesh. */
    public static final RegistryObject<Item> ARMOR_PLATE = ITEMS.register("armor_plate", ArmorPlateItem::new);

    /** Ghillie set — grayscale mesh tinted by biome foliage. */
    public static final RegistryObject<Item> GHILLIE_HOOD = ITEMS.register("ghillie_hood",
            () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.GHILLIE_HOOD));
    public static final RegistryObject<Item> GHILLIE_TOP = ITEMS.register("ghillie_top",
            () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.GHILLIE_TOP));
    public static final RegistryObject<Item> GHILLIE_PANTS = ITEMS.register("ghillie_pants",
            () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.GHILLIE_PANTS));
    public static final RegistryObject<Item> GHILLIE_BOOTS = ITEMS.register("ghillie_boots",
            () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.GHILLIE_BOOTS));

    public static final RegistryObject<Item> SEWING_STATION = ITEMS.register("sewing_station",
            () -> new BlockItem(ModBlocks.SEWING_STATION.get(), new Item.Properties()));

    static {
        for (TShirtColor color : TShirtColor.values()) {
            TSHIRTS.put(color, ITEMS.register(color.itemId(),
                    () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.TSHIRT, color)));
            if (color.hasTankTop()) {
                TANK_TOPS.put(color, ITEMS.register(color.tankTopItemId(),
                        () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.TANK_TOP, color)));
            }
            if (color.hasVest()) {
                VESTS.put(color, ITEMS.register(color.vestItemId(),
                        () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.VEST, color)));
            }
            HOODIES.put(color, ITEMS.register(color.hoodieItemId(),
                    () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.HOODIE, color)));
            JACKETS.put(color, ITEMS.register(color.jacketItemId(),
                    () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.JACKET, color)));
            if (color.hasCoat()) {
                COATS.put(color, ITEMS.register(color.coatItemId(),
                        () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.COAT, color)));
            }
            PANTS.put(color, ITEMS.register(color.pantsItemId(),
                    () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.PANTS, color)));
            SHORTS.put(color, ITEMS.register(color.shortsItemId(),
                    () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.SHORTS, color)));
            SNEAKERS.put(color, ITEMS.register(color.sneakersItemId(),
                    () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.SNEAKERS, color)));
            if (color.hasShoes()) {
                SHOES.put(color, ITEMS.register(color.shoesItemId(),
                        () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.SHOES, color)));
            }
            HATS.put(color, ITEMS.register(color.hatItemId(),
                    () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.HAT, color)));
            if (color.hasCowboyHat()) {
                COWBOY_HATS.put(color, ITEMS.register(color.cowboyHatItemId(),
                        () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.COWBOY_HAT, color)));
            }
            CAPS.put(color, ITEMS.register(color.capItemId(),
                    () -> new ClothingItem(new Item.Properties().stacksTo(1), ClothingType.CAP, color)));
            if (color.hasBandana()) {
                final TShirtColor bandanaColor = color;
                BANDANAS.put(color, ITEMS.register(color.bandanaItemId(),
                        () -> new BandanaItem(bandanaColor)));
            }
            if (color.hasBalaclava()) {
                final TShirtColor balaclavaColor = color;
                BALACLAVAS.put(color, ITEMS.register(color.balaclavaItemId(),
                        () -> new BalaclavaItem(balaclavaColor)));
            }
            if (color.hasClutch()) {
                final TShirtColor clutchColor = color;
                CLUTCHES.put(color, ITEMS.register(color.clutchItemId(),
                        () -> new ClutchItem(clutchColor)));
            }
            if (color.hasSportsBag()) {
                final TShirtColor bagColor = color;
                SPORTS_BAGS.put(color, ITEMS.register(color.sportsBagItemId(),
                        () -> new SportsBagItem(bagColor)));
            }
            if (color.hasBackpack()) {
                final TShirtColor bagColor = color;
                BACKPACKS.put(color, ITEMS.register(color.backpackItemId(),
                        () -> new BackpackItem(bagColor)));
            }
        }
    }

    public static Item blackBandana() {
        return BANDANAS.get(TShirtColor.BLACK).get();
    }

    public static Item blackBalaclava() {
        return BALACLAVAS.get(TShirtColor.BLACK).get();
    }

    /** Convenience — red is the tab icon. */
    public static Item redTshirt() {
        return TSHIRTS.get(TShirtColor.RED).get();
    }

    public static Item clothing(ClothingType type, TShirtColor color) {
        return switch (type) {
            case TSHIRT -> TSHIRTS.get(color).get();
            case TANK_TOP -> {
                RegistryObject<Item> tank = TANK_TOPS.get(color);
                yield tank != null ? tank.get() : TANK_TOPS.get(TShirtColor.WHITE).get();
            }
            case VEST -> {
                RegistryObject<Item> vest = VESTS.get(color);
                yield vest != null ? vest.get() : VESTS.get(TShirtColor.BLACK).get();
            }
            case HOODIE -> HOODIES.get(color).get();
            case JACKET -> JACKETS.get(color).get();
            case COAT -> {
                RegistryObject<Item> coat = COATS.get(color);
                yield coat != null ? coat.get() : COATS.get(TShirtColor.BROWN).get();
            }
            case PANTS -> PANTS.get(color).get();
            case SHORTS -> SHORTS.get(color).get();
            case SNEAKERS -> SNEAKERS.get(color).get();
            case SHOES -> {
                RegistryObject<Item> shoes = SHOES.get(color);
                yield shoes != null ? shoes.get() : SHOES.get(TShirtColor.BROWN).get();
            }
            case HAT -> HATS.get(color).get();
            case COWBOY_HAT -> {
                RegistryObject<Item> hat = COWBOY_HATS.get(color);
                yield hat != null ? hat.get() : COWBOY_HATS.get(TShirtColor.BROWN).get();
            }
            case STRAW_HAT -> STRAW_HAT.get();
            case CAP -> CAPS.get(color).get();
            case HELMET -> HELMET.get();
            case SCRAP_HELMET -> SCRAP_HELMET.get();
            case SCRAP_VEST -> SCRAP_CHESTPLATE.get();
            case SCRAP_PANTS -> SCRAP_PANTS.get();
            case GHILLIE_HOOD -> GHILLIE_HOOD.get();
            case GHILLIE_TOP -> GHILLIE_TOP.get();
            case GHILLIE_PANTS -> GHILLIE_PANTS.get();
            case GHILLIE_BOOTS -> GHILLIE_BOOTS.get();
        };
    }

    private ModItems() {
    }
}
