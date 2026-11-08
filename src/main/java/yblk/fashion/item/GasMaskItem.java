package yblk.fashion.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import yblk.fashion.Fashion;

import java.util.List;

/**
 * Curios {@code mask} gas mask. Starts with 0 filters; RPM {@code gas_filter} inserts
 * up to 2 (150 charge each). Mask item never breaks — only filters deplete.
 */
public class GasMaskItem extends CuriosAccessoryItem {
    public static final String CURIOS_SLOT = "mask";
    public static final int MAX_FILTERS = 2;
    public static final int CHARGE_PER_FILTER = 150;

    private static final String TAG_FILTERS = "GasFilters";
    private static final String TAG_DAMAGE = "GasFilterDamage";

    private static final ResourceLocation MODEL_TEXTURE =
            new ResourceLocation(Fashion.MODID, "textures/models/gas_mask.png");

    public GasMaskItem() {
        // No vanilla durability — filter charge is NBT; bar drawn manually.
        super(new Properties().stacksTo(1), CURIOS_SLOT);
    }

    public ResourceLocation modelTexture() {
        return MODEL_TEXTURE;
    }

    public static boolean isGasMask(ItemStack stack) {
        return stack.getItem() instanceof GasMaskItem;
    }

    public static int filterCount(ItemStack stack) {
        if (!isGasMask(stack)) {
            return 0;
        }
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return 0;
        }
        return Math.max(0, Math.min(MAX_FILTERS, tag.getInt(TAG_FILTERS)));
    }

    public static int filterDamage(ItemStack stack) {
        if (!isGasMask(stack)) {
            return 0;
        }
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return 0;
        }
        return Math.max(0, tag.getInt(TAG_DAMAGE));
    }

    public static int maxCharge(ItemStack stack) {
        return filterCount(stack) * CHARGE_PER_FILTER;
    }

    public static int remainingCharge(ItemStack stack) {
        return Math.max(0, maxCharge(stack) - filterDamage(stack));
    }

    public static boolean hasFilterCharge(ItemStack stack) {
        return remainingCharge(stack) > 0;
    }

    /** {@code true} if a filter was inserted. */
    public static boolean tryInsertFilter(ItemStack mask) {
        if (!isGasMask(mask)) {
            return false;
        }
        int filters = filterCount(mask);
        if (filters >= MAX_FILTERS) {
            return false;
        }
        CompoundTag tag = mask.getOrCreateTag();
        tag.putInt(TAG_FILTERS, filters + 1);
        return true;
    }

    /**
     * Drain 1 filter charge. Breaks a filter every {@link #CHARGE_PER_FILTER} damage.
     * Mask stack is never destroyed.
     */
    public static void hurtFilter(ItemStack mask, @Nullable LivingEntity entity, int amount) {
        if (!isGasMask(mask) || amount <= 0 || !hasFilterCharge(mask)) {
            return;
        }
        CompoundTag tag = mask.getOrCreateTag();
        int filters = filterCount(mask);
        int damage = filterDamage(mask) + amount;
        while (filters > 0 && damage >= CHARGE_PER_FILTER) {
            damage -= CHARGE_PER_FILTER;
            filters--;
        }
        if (filters <= 0) {
            tag.putInt(TAG_FILTERS, 0);
            tag.putInt(TAG_DAMAGE, 0);
        } else {
            tag.putInt(TAG_FILTERS, filters);
            tag.putInt(TAG_DAMAGE, damage);
        }
        if (entity instanceof ServerPlayer player) {
            player.inventoryMenu.broadcastChanges();
        }
    }

    public static ItemStack findWorn(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .resolve()
                .flatMap(inv -> inv.findFirstCurio(GasMaskItem::isGasMask))
                .map(r -> r.stack())
                .orElse(ItemStack.EMPTY);
    }

    public static boolean protects(LivingEntity entity) {
        return hasFilterCharge(findWorn(entity));
    }

    @Override
    public boolean overrideOtherStackedOnMe(
            ItemStack mask,
            ItemStack other,
            Slot slot,
            ClickAction action,
            Player player,
            SlotAccess access) {
        // RMB = SECONDARY — pick up filter, right-click the gas mask stack.
        return yblk.fashion.AccessoryChargeEvents.tryApplyOntoAccessory(mask, other, action, player);
    }

    @Override
    public boolean overrideStackedOnOther(
            ItemStack mask, Slot slot, ClickAction action, Player player) {
        // RMB gas mask onto a filter stack in a slot.
        return yblk.fashion.AccessoryChargeEvents.tryApplyOntoAccessory(
                mask, slot.getItem(), action, player);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return filterCount(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int max = maxCharge(stack);
        if (max <= 0) {
            return 0;
        }
        return Math.round(13.0F * remainingCharge(stack) / (float) max);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float fill = maxCharge(stack) <= 0 ? 0f : remainingCharge(stack) / (float) maxCharge(stack);
        return net.minecraft.util.Mth.hsvToRgb(Math.max(0f, fill) / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public void appendHoverText(
            ItemStack stack, @Nullable Level level, List<Component> tip, TooltipFlag flag) {
        int filters = filterCount(stack);
        tip.add(Component.translatable("item.fashion.gas_mask.filters", filters, MAX_FILTERS));
        if (filters > 0) {
            tip.add(Component.translatable(
                    "item.fashion.gas_mask.charge", remainingCharge(stack), maxCharge(stack)));
        } else {
            tip.add(Component.translatable("item.fashion.gas_mask.no_filter"));
        }
        tip.add(Component.translatable("item.fashion.gas_mask.filter_hint"));
    }
}
