package yblk.fashion.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import yblk.fashion.AccessoryChargeEvents;
import yblk.fashion.Fashion;

import java.util.List;

/**
 * Night-vision goggles — Curios {@code mask}. Toggle on keybind; drains durability
 * while powered. No armor. Cannot power on at 1 durability remaining.
 */
public class NvgItem extends CuriosAccessoryItem {
    public static final int DURABILITY = 400;
    public static final String CURIOS_SLOT = "mask";
    /** 1 damage every 2 seconds while active. */
    public static final int DRAIN_INTERVAL_TICKS = 40;
    /** Remaining durability at or below this blocks power-on and forces off. */
    public static final int MIN_REMAINING_TO_RUN = 1;
    /** Long enough that the client NV fade (under ~200) never starts between refreshes. */
    private static final int NV_DURATION_TICKS = 300;

    private static final String TAG_ACTIVE = "NvgActive";

    private static final ResourceLocation MODEL_TEXTURE =
            new ResourceLocation(Fashion.MODID, "textures/models/nvg.png");
    private static final ResourceLocation EMISSIVE_TEXTURE =
            new ResourceLocation(Fashion.MODID, "textures/models/nvg_emissive.png");

    public NvgItem() {
        super(new Properties().durability(DURABILITY), CURIOS_SLOT);
    }

    public ResourceLocation modelTexture() {
        return MODEL_TEXTURE;
    }

    public ResourceLocation emissiveTexture() {
        return EMISSIVE_TEXTURE;
    }

    public static boolean isNvg(ItemStack stack) {
        return stack.getItem() instanceof NvgItem;
    }

    public static boolean isActive(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(TAG_ACTIVE);
    }

    public static int remaining(ItemStack stack) {
        return stack.getMaxDamage() - stack.getDamageValue();
    }

    public static void applyNightVision(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(
                MobEffects.NIGHT_VISION, NV_DURATION_TICKS, 0, false, false, true));
    }

    /** {@code true} if state changed. */
    public static boolean setActive(ItemStack stack, boolean active) {
        if (!(stack.getItem() instanceof NvgItem)) {
            return false;
        }
        if (active && remaining(stack) <= MIN_REMAINING_TO_RUN) {
            return false;
        }
        boolean was = isActive(stack);
        if (was == active) {
            return false;
        }
        stack.getOrCreateTag().putBoolean(TAG_ACTIVE, active);
        return true;
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        if (entity.level().isClientSide || !isActive(stack)) {
            return;
        }
        if (remaining(stack) <= MIN_REMAINING_TO_RUN) {
            setActive(stack, false);
            entity.removeEffect(MobEffects.NIGHT_VISION);
            return;
        }

        applyNightVision(entity);

        // Use entity tick — do not write drain counter NBT every tick (Curios treats
        // NBT updates as unequip and was clearing active + night vision).
        if (entity.tickCount % DRAIN_INTERVAL_TICKS != 0) {
            return;
        }

        int nextDamage = stack.getDamageValue() + 1;
        int nextRemaining = stack.getMaxDamage() - nextDamage;
        if (nextRemaining <= MIN_REMAINING_TO_RUN) {
            stack.setDamageValue(stack.getMaxDamage() - MIN_REMAINING_TO_RUN);
            setActive(stack, false);
            entity.removeEffect(MobEffects.NIGHT_VISION);
            if (entity instanceof ServerPlayer player) {
                player.inventoryMenu.broadcastChanges();
            }
            return;
        }
        stack.setDamageValue(nextDamage);
        if (entity instanceof ServerPlayer player) {
            player.inventoryMenu.broadcastChanges();
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        // Curios fires this on NBT-only stack updates — keep power if still an NVG.
        if (isNvg(newStack)) {
            return;
        }
        if (isActive(stack)) {
            setActive(stack, false);
            slotContext.entity().removeEffect(MobEffects.NIGHT_VISION);
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(
            ItemStack nvg,
            ItemStack other,
            Slot slot,
            ClickAction action,
            Player player,
            SlotAccess access) {
        // RMB = SECONDARY — pick up battery, right-click the NVG stack.
        return AccessoryChargeEvents.tryApplyOntoAccessory(nvg, other, action, player);
    }

    @Override
    public boolean overrideStackedOnOther(
            ItemStack nvg, Slot slot, ClickAction action, Player player) {
        // RMB NVG onto a battery stack in a slot.
        return AccessoryChargeEvents.tryApplyOntoAccessory(nvg, slot.getItem(), action, player);
    }

    @Override
    public void appendHoverText(
            ItemStack stack, @Nullable Level level, List<Component> tip, TooltipFlag flag) {
        if (isActive(stack)) {
            tip.add(Component.translatable("item.fashion.nvg.active"));
        }
        tip.add(Component.translatable("item.fashion.nvg.battery_hint"));
    }
}
