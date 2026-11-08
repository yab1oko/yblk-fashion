package yblk.fashion.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

/** Soft Roleplay Materials item lookups. */
public final class RoleplayItems {
    public static final ResourceLocation GAS_FILTER_ID =
            new ResourceLocation("roleplay_materials", "gas_filter");
    public static final ResourceLocation BATTERY_ID =
            new ResourceLocation("roleplay_materials", "battery");

    private RoleplayItems() {
    }

    @Nullable
    public static Item gasFilter() {
        return resolve(GAS_FILTER_ID);
    }

    @Nullable
    public static Item battery() {
        return resolve(BATTERY_ID);
    }

    public static boolean isGasFilter(ItemStack stack) {
        Item item = gasFilter();
        return item != null && !stack.isEmpty() && stack.is(item);
    }

    public static boolean isBattery(ItemStack stack) {
        Item item = battery();
        return item != null && !stack.isEmpty() && stack.is(item);
    }

    @Nullable
    private static Item resolve(ResourceLocation id) {
        Item item = ForgeRegistries.ITEMS.getValue(id);
        return item != null && item != Items.AIR ? item : null;
    }
}
