package yblk.fashion;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import yblk.fashion.compat.RoleplayItems;
import yblk.fashion.item.GasMaskItem;
import yblk.fashion.item.NvgItem;

/**
 * Apply RPM gas filter / battery onto Fashion accessories.
 * <ul>
 *   <li>Inventory: RMB (secondary click) battery/filter onto the accessory stack</li>
 *   <li>World: RMB with filter/battery while the accessory is worn in Curios</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = Fashion.MODID)
public final class AccessoryChargeEvents {
    /** Durability restored per RPM battery on NVG. */
    public static final int NVG_BATTERY_CHARGE = 100;

    private AccessoryChargeEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        ItemStack held = event.getItemStack();
        if (held.isEmpty()) {
            return;
        }

        if (RoleplayItems.isGasFilter(held)) {
            ItemStack mask = GasMaskItem.findWorn(player);
            if (mask.isEmpty() || !GasMaskItem.tryInsertFilter(mask)) {
                return;
            }
            if (!player.level().isClientSide && !player.getAbilities().instabuild) {
                held.shrink(1);
            }
            playFilter(player);
            event.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide));
            event.setCanceled(true);
            return;
        }

        if (RoleplayItems.isBattery(held)) {
            ItemStack nvg = findWornNvg(player);
            if (nvg.isEmpty() || !chargeNvg(nvg)) {
                return;
            }
            if (!player.level().isClientSide && !player.getAbilities().instabuild) {
                held.shrink(1);
            }
            playBattery(player);
            event.setCancellationResult(InteractionResult.sidedSuccess(player.level().isClientSide));
            event.setCanceled(true);
        }
    }

    /**
     * Inventory / Curios GUI: right-click accessory with filter or battery on the cursor.
     * {@code ClickAction.SECONDARY} = RMB.
     */
    public static boolean tryApplyOntoAccessory(
            ItemStack accessory,
            ItemStack carried,
            ClickAction action,
            Player player) {
        if (action != ClickAction.SECONDARY || carried.isEmpty() || accessory.isEmpty()) {
            return false;
        }
        if (GasMaskItem.isGasMask(accessory) && RoleplayItems.isGasFilter(carried)) {
            if (!GasMaskItem.tryInsertFilter(accessory)) {
                return false;
            }
            if (!player.getAbilities().instabuild) {
                carried.shrink(1);
            }
            playFilter(player);
            return true;
        }
        if (NvgItem.isNvg(accessory) && RoleplayItems.isBattery(carried)) {
            if (!chargeNvg(accessory)) {
                return false;
            }
            if (!player.getAbilities().instabuild) {
                carried.shrink(1);
            }
            playBattery(player);
            return true;
        }
        return false;
    }

    public static boolean chargeNvg(ItemStack nvg) {
        if (!NvgItem.isNvg(nvg)) {
            return false;
        }
        int damage = nvg.getDamageValue();
        if (damage <= 0) {
            return false;
        }
        nvg.setDamageValue(Math.max(0, damage - NVG_BATTERY_CHARGE));
        return true;
    }

    private static ItemStack findWornNvg(Player player) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(inv -> inv.findFirstCurio(NvgItem::isNvg))
                .map(r -> r.stack())
                .orElse(ItemStack.EMPTY);
    }

    private static void playFilter(Player player) {
        if (player.level().isClientSide) {
            return;
        }
        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.ARMOR_EQUIP_LEATHER,
                SoundSource.PLAYERS,
                0.8f,
                1.1f);
    }

    private static void playBattery(Player player) {
        if (player.level().isClientSide) {
            return;
        }
        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.EXPERIENCE_ORB_PICKUP,
                SoundSource.PLAYERS,
                0.5f,
                1.2f);
    }
}
