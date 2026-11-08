package yblk.fashion.compat;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import yblk.fashion.Fashion;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Vanilla {@code hurtArmor} only walks equipment slots — Curios outerwear / vest / pants
 * that grant armor never lose durability. Mirror chest/legs cadence here.
 */
@Mod.EventBusSubscriber(modid = Fashion.MODID)
public final class OuterwearDamage {
    private OuterwearDamage() {
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || event.getAmount() <= 0.0F) {
            return;
        }
        if (event.getSource().is(DamageTypeTags.BYPASSES_ARMOR)) {
            return;
        }
        // Same as Inventory#hurtArmor: each piece takes max(1, amount/4) of the pre-absorb hit.
        int hurt = Math.max(1, Math.round(event.getAmount() / 4.0F));
        damageCurio(entity, CuriosClothing::isOuterwearStack, hurt);
        damageCurio(entity, CuriosClothing::isVestStack, hurt);
        damageCurio(entity, CuriosClothing::isPantsStack, hurt);
    }

    private static void damageCurio(LivingEntity entity, Predicate<ItemStack> match, int hurt) {
        Optional<SlotResult> found = CuriosApi.getCuriosInventory(entity)
                .resolve()
                .flatMap(inv -> inv.findFirstCurio(match));
        if (found.isEmpty()) {
            return;
        }
        SlotResult result = found.get();
        ItemStack stack = result.stack();
        if (!stack.isDamageableItem()) {
            return;
        }
        stack.hurtAndBreak(hurt, entity, broken ->
                CuriosApi.broadcastCurioBreakEvent(result.slotContext()));
    }
}
