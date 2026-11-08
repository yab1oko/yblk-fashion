package yblk.fashion.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

/** Dye-like t-shirt variants sharing one model + tear atlas. */
public enum TShirtColor {
    WHITE("white", "white_dye"),
    LIGHT_GRAY("light_gray", "light_gray_dye"),
    GRAY("gray", "gray_dye"),
    BLACK("black", "black_dye"),
    BROWN("brown", "brown_dye"),
    RED("red", "red_dye"),
    ORANGE("orange", "orange_dye"),
    YELLOW("yellow", "yellow_dye"),
    LIME("lime", "lime_dye"),
    GREEN("green", "green_dye"),
    CYAN("cyan", "cyan_dye"),
    LIGHT_BLUE("light_blue", "light_blue_dye"),
    BLUE("blue", "blue_dye"),
    PURPLE("purple", "purple_dye"),
    MAGENTA("magenta", "magenta_dye"),
    PINK("pink", "pink_dye");

    public final String id;
    private final String dyeId;

    TShirtColor(String id, String dyeId) {
        this.id = id;
        this.dyeId = dyeId;
    }

    /** Opaque ARGB swatch for sewing UI (approximate vanilla dye look). */
    public int swatchColor() {
        return switch (this) {
            case WHITE -> 0xFFF9FFFE;
            case LIGHT_GRAY -> 0xFF9D9D97;
            case GRAY -> 0xFF474F52;
            case BLACK -> 0xFF1D1D21;
            case BROWN -> 0xFF835432;
            case RED -> 0xFFB02E26;
            case ORANGE -> 0xFFF9801D;
            case YELLOW -> 0xFFFED83D;
            case LIME -> 0xFF80C71F;
            case GREEN -> 0xFF5E7C16;
            case CYAN -> 0xFF169C9C;
            case LIGHT_BLUE -> 0xFF3AB3DA;
            case BLUE -> 0xFF3C44AA;
            case PURPLE -> 0xFF8932B8;
            case MAGENTA -> 0xFFC74EBD;
            case PINK -> 0xFFF38BAA;
        };
    }

    public String itemId() {
        return id + "_tshirt";
    }

    /** Black / blue / red bandanas only. */
    public boolean hasBandana() {
        return this == BLACK || this == BLUE || this == RED;
    }

    public String bandanaItemId() {
        return id + "_bandana";
    }

    /** Black / white balaclavas only. */
    public boolean hasBalaclava() {
        return this == BLACK || this == WHITE;
    }

    public String balaclavaItemId() {
        return id + "_balaclava";
    }

    /** Black / blue / orange / yellow clutches — Curios {@code body} (backpack). */
    public boolean hasClutch() {
        return this == BLACK || this == BLUE || this == ORANGE || this == YELLOW;
    }

    public String clutchItemId() {
        return id + "_clutch";
    }

    /** Purple / red / gray / green sports bags — Curios {@code body}. */
    public boolean hasSportsBag() {
        return this == PURPLE || this == RED || this == GRAY || this == GREEN;
    }

    public String sportsBagItemId() {
        return id + "_sports_bag";
    }

    /** Black / red / green backpacks — Curios {@code body}. */
    public boolean hasBackpack() {
        return this == BLACK || this == RED || this == GREEN;
    }

    public String backpackItemId() {
        return id + "_backpack";
    }

    public String modelTexturePath() {
        return "textures/models/" + id + "_tshirt.png";
    }

    /** Per-color tear overlay atlas (matches shirt dye). */
    public String tearTexturePath() {
        return "textures/models/" + id + "_tshirt_tear.png";
    }

    public String hoodieItemId() {
        return id + "_hoodie";
    }

    public String hoodieModelTexturePath() {
        return "textures/models/" + id + "_hoodie.png";
    }

    public String hoodieTearTexturePath() {
        return "textures/models/" + id + "_hoodie_tear.png";
    }

    public String jacketItemId() {
        return id + "_jacket";
    }

    public String jacketModelTexturePath() {
        return "textures/models/" + id + "_jacket.png";
    }

    public String jacketTearTexturePath() {
        return "textures/models/" + id + "_jacket_tear.png";
    }

    public String pantsItemId() {
        return id + "_pants";
    }

    public String pantsModelTexturePath() {
        return "textures/models/" + id + "_pants.png";
    }

    public String pantsTearTexturePath() {
        return "textures/models/" + id + "_pants_tear.png";
    }

    public String shortsItemId() {
        return id + "_shorts";
    }

    public String shortsModelTexturePath() {
        return "textures/models/" + id + "_shorts.png";
    }

    public String shortsTearTexturePath() {
        return "textures/models/" + id + "_shorts_tear.png";
    }

    public String sneakersItemId() {
        return id + "_sneakers";
    }

    public String sneakersModelTexturePath() {
        return "textures/models/" + id + "_sneakers.png";
    }

    /** Leather shoes / cowboy hat: black / brown / light_brown (orange dye). */
    public boolean hasShoes() {
        return this == BLACK || this == BROWN || this == ORANGE;
    }

    public boolean hasCowboyHat() {
        return hasShoes();
    }

    /** Tank top: white / black only. */
    public boolean hasTankTop() {
        return this == WHITE || this == BLACK;
    }

    public String tankTopItemId() {
        return id + "_tank_top";
    }

    public String tankTopModelTexturePath() {
        return "textures/models/" + id + "_tank_top.png";
    }

    /** Vest (plate carrier): black / green only. */
    public boolean hasVest() {
        return this == BLACK || this == GREEN;
    }

    public String vestItemId() {
        return id + "_vest";
    }

    public String vestModelTexturePath() {
        return "textures/models/" + id + "_vest.png";
    }

    /** Coat: black / brown only. */
    public boolean hasCoat() {
        return this == BLACK || this == BROWN;
    }

    public String coatItemId() {
        return id + "_coat";
    }

    public String coatModelTexturePath() {
        return "textures/models/" + id + "_coat.png";
    }

    public String shoesItemId() {
        return this == ORANGE ? "light_brown_shoes" : id + "_shoes";
    }

    public String shoesModelTexturePath() {
        return this == ORANGE
                ? "textures/models/light_brown_shoes.png"
                : "textures/models/" + id + "_shoes.png";
    }

    public String cowboyHatItemId() {
        return this == ORANGE ? "light_brown_cowboy_hat" : id + "_cowboy_hat";
    }

    public String cowboyHatModelTexturePath() {
        return this == ORANGE
                ? "textures/models/light_brown_cowboy_hat.png"
                : "textures/models/" + id + "_cowboy_hat.png";
    }

    public String hatItemId() {
        return id + "_hat";
    }

    public String hatModelTexturePath() {
        return "textures/models/" + id + "_hat.png";
    }

    public String capItemId() {
        return id + "_cap";
    }

    public String capModelTexturePath() {
        return "textures/models/" + id + "_cap.png";
    }

    public String woolItemId() {
        return id + "_wool";
    }

    public Item dyeItem() {
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation("minecraft", dyeId));
        return item != null ? item : Items.AIR;
    }

    @Nullable
    public static TShirtColor fromDye(Item item) {
        if (item == null || item == Items.AIR) {
            return null;
        }
        for (TShirtColor color : values()) {
            if (color.dyeItem() == item) {
                return color;
            }
        }
        return null;
    }
}
