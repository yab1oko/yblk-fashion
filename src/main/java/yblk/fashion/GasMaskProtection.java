package yblk.fashion;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import yblk.fashion.item.GasMaskItem;

/**
 * While a charged gas mask is worn, blocks poison / wither / YBLK Zones radiation
 * and drains 1 filter charge per second of protection.
 */
@Mod.EventBusSubscriber(modid = Fashion.MODID)
public final class GasMaskProtection {
    private static final ResourceLocation RADIATION_ID =
            new ResourceLocation("yblk_zones", "radiation");
    private static final String DRAIN_GAME_TIME = "FashionGasMaskDrainAt";

    private GasMaskProtection() {
    }

    @SubscribeEvent
    public static void onEffectApplicable(MobEffectEvent.Applicable event) {
        LivingEntity entity = event.getEntity();
        MobEffectInstance instance = event.getEffectInstance();
        if (instance == null || !isBlocked(instance.getEffect())) {
            return;
        }
        ItemStack mask = GasMaskItem.findWorn(entity);
        if (!GasMaskItem.hasFilterCharge(mask)) {
            return;
        }
        event.setResult(Event.Result.DENY);
        tryDrain(entity, mask);
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide || entity.tickCount % 20 != 0) {
            return;
        }
        ItemStack mask = GasMaskItem.findWorn(entity);
        if (!GasMaskItem.hasFilterCharge(mask)) {
            return;
        }
        boolean cleared = false;
        if (entity.hasEffect(MobEffects.POISON)) {
            entity.removeEffect(MobEffects.POISON);
            cleared = true;
        }
        if (entity.hasEffect(MobEffects.WITHER)) {
            entity.removeEffect(MobEffects.WITHER);
            cleared = true;
        }
        MobEffect radiation = radiationEffect();
        if (radiation != null && entity.hasEffect(radiation)) {
            entity.removeEffect(radiation);
            cleared = true;
        }
        if (cleared) {
            tryDrain(entity, mask);
        }
    }

    private static void tryDrain(LivingEntity entity, ItemStack mask) {
        if (entity.level().isClientSide) {
            return;
        }
        long now = entity.level().getGameTime();
        long last = entity.getPersistentData().getLong(DRAIN_GAME_TIME);
        if (now - last < 20L) {
            return;
        }
        entity.getPersistentData().putLong(DRAIN_GAME_TIME, now);
        GasMaskItem.hurtFilter(mask, entity, 1);
    }

    private static boolean isBlocked(MobEffect effect) {
        if (effect == MobEffects.POISON || effect == MobEffects.WITHER) {
            return true;
        }
        MobEffect radiation = radiationEffect();
        return radiation != null && effect == radiation;
    }

    @org.jetbrains.annotations.Nullable
    private static MobEffect radiationEffect() {
        return ForgeRegistries.MOB_EFFECTS.getValue(RADIATION_ID);
    }
}
