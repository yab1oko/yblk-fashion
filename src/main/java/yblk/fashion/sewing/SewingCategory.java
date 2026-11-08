package yblk.fashion.sewing;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import yblk.fashion.init.ModItems;
import yblk.fashion.item.TShirtColor;

import java.util.ArrayList;
import java.util.List;

/** Left-rail filter on the sewing station (armor slot groups + curios). */
public enum SewingCategory {
    HEAD,
    CHEST,
    LEGS,
    FEET,
    CURIOS;

    public Component title() {
        return Component.translatable("sewing.fashion.category." + name().toLowerCase());
    }

    public ItemStack icon() {
        return switch (this) {
            case HEAD -> new ItemStack(ModItems.HATS.get(TShirtColor.RED).get());
            case CHEST -> new ItemStack(ModItems.TSHIRTS.get(TShirtColor.WHITE).get());
            case LEGS -> new ItemStack(ModItems.PANTS.get(TShirtColor.RED).get());
            case FEET -> new ItemStack(ModItems.SNEAKERS.get(TShirtColor.BROWN).get());
            case CURIOS -> new ItemStack(ModItems.blackBandana());
        };
    }

    public static SewingCategory ofProduct(SewingProduct product) {
        return switch (product.id()) {
            case "hat", "cowboy_hat", "straw_hat", "cap", "helmet", "ghillie_hood" -> HEAD;
            case "tshirt", "tank_top", "ghillie_top" -> CHEST;
            case "hoodie", "jacket", "coat", "vest", "bandana", "balaclava", "nvg", "gas_mask", "glasses", "black_glasses", "clutch", "sports_bag", "backpack" -> CURIOS;
            case "pants", "shorts", "ghillie_pants" -> LEGS;
            case "sneakers", "shoes", "ghillie_boots" -> FEET;
            default -> CURIOS;
        };
    }

    /** Global product indices in this category. */
    public List<Integer> productIndices() {
        List<Integer> out = new ArrayList<>();
        List<SewingProduct> all = SewingProduct.all();
        for (int i = 0; i < all.size(); i++) {
            if (ofProduct(all.get(i)) == this) {
                out.add(i);
            }
        }
        return out;
    }

    public int firstProductIndex() {
        List<Integer> idx = productIndices();
        return idx.isEmpty() ? -1 : idx.get(0);
    }
}
