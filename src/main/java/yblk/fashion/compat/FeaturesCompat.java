package yblk.fashion.compat;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import yblk.fashion.init.ModItems;
import yblk.fashion.item.BackpackItem;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClutchItem;
import yblk.fashion.item.SportsBagItem;
import yblk.fashion.item.TShirtColor;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Soft link to YBLK Features inventory-revamp:
 * clothing registers slot bonuses into the same map as armor pieces.
 */
public final class FeaturesCompat {
    public static final String FEATURES_MODID = "features";
    private static final Logger LOGGER = LogUtils.getLogger();

    private static boolean present;

    private FeaturesCompat() {
    }

    public static void bootstrap() {
        present = ModList.get().isLoaded(FEATURES_MODID);
        if (!present) {
            return;
        }
        try {
            Class<?> config = Class.forName("yblk.features.config.FeaturesConfig");
            Method setBonus = config.getMethod("setArmorBonus", String.class, int.class);
            int count = 0;
            // Clear former bonuses (hats / shoes / t-shirt no longer grant slots).
            clearMap(setBonus, ModItems.TSHIRTS);
            clearMap(setBonus, ModItems.TANK_TOPS);
            clearMap(setBonus, ModItems.SNEAKERS);
            clearMap(setBonus, ModItems.SHOES);
            clearMap(setBonus, ModItems.HATS);
            clearMap(setBonus, ModItems.COWBOY_HATS);
            clearMap(setBonus, ModItems.CAPS);
            setBonus.invoke(null, ModItems.STRAW_HAT.getId().toString(), 0);
            setBonus.invoke(null, ModItems.HELMET.getId().toString(), 0);
            setBonus.invoke(null, ModItems.SCRAP_HELMET.getId().toString(), 0);
            setBonus.invoke(null, ModItems.SCRAP_CHESTPLATE.getId().toString(), 0);
            setBonus.invoke(null, ModItems.SCRAP_PANTS.getId().toString(), 0);

            count += registerMap(setBonus, ModItems.PANTS);
            count += registerMap(setBonus, ModItems.SHORTS);
            count += registerMap(setBonus, ModItems.HOODIES);
            count += registerMap(setBonus, ModItems.JACKETS);
            count += registerMap(setBonus, ModItems.COATS);
            count += registerMap(setBonus, ModItems.VESTS);
            for (var entry : ModItems.CLUTCHES.entrySet()) {
                setBonus.invoke(null, entry.getValue().getId().toString(), ClutchItem.FEATURES_SLOTS);
                count++;
            }
            for (var entry : ModItems.SPORTS_BAGS.entrySet()) {
                setBonus.invoke(null, entry.getValue().getId().toString(), SportsBagItem.FEATURES_SLOTS);
                count++;
            }
            for (var entry : ModItems.BACKPACKS.entrySet()) {
                setBonus.invoke(null, entry.getValue().getId().toString(), BackpackItem.FEATURES_SLOTS);
                count++;
            }
            setBonus.invoke(null, ModItems.GHILLIE_HOOD.getId().toString(),
                    ((ClothingItem) ModItems.GHILLIE_HOOD.get()).getClothingType().featuresSlotBonus());
            setBonus.invoke(null, ModItems.GHILLIE_TOP.getId().toString(),
                    ((ClothingItem) ModItems.GHILLIE_TOP.get()).getClothingType().featuresSlotBonus());
            setBonus.invoke(null, ModItems.GHILLIE_PANTS.getId().toString(),
                    ((ClothingItem) ModItems.GHILLIE_PANTS.get()).getClothingType().featuresSlotBonus());
            setBonus.invoke(null, ModItems.GHILLIE_BOOTS.getId().toString(),
                    ((ClothingItem) ModItems.GHILLIE_BOOTS.get()).getClothingType().featuresSlotBonus());
            count += 4;
            LOGGER.info("Registered Fashion → Features inventory slots for {} clothing items", count);
        } catch (ReflectiveOperationException e) {
            LOGGER.warn("Features present but slot-bonus API unavailable: {}", e.toString());
        }
    }

    private static int registerMap(Method setBonus, Map<TShirtColor, RegistryObject<net.minecraft.world.item.Item>> map)
            throws ReflectiveOperationException {
        int n = 0;
        for (var entry : map.entrySet()) {
            var item = entry.getValue();
            String id = item.getId().toString();
            int slots = ((ClothingItem) item.get()).getClothingType().featuresSlotBonus();
            if (slots <= 0) {
                continue;
            }
            setBonus.invoke(null, id, slots);
            n++;
        }
        return n;
    }

    private static void clearMap(Method setBonus, Map<TShirtColor, RegistryObject<net.minecraft.world.item.Item>> map)
            throws ReflectiveOperationException {
        for (var entry : map.entrySet()) {
            setBonus.invoke(null, entry.getValue().getId().toString(), 0);
        }
    }

    public static boolean isFeaturesPresent() {
        return present;
    }
}
