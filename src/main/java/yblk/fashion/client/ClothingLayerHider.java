package yblk.fashion.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import yblk.fashion.Fashion;
import yblk.fashion.compat.CuriosClothing;
import yblk.fashion.item.BalaclavaItem;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClothingType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Hide 2nd skin layers covered by clothing (players + PlayerModel NPCs).
 */
@Mod.EventBusSubscriber(modid = Fashion.MODID, value = Dist.CLIENT)
public final class ClothingLayerHider {
    private static final ThreadLocal<LayerState> PREV = new ThreadLocal<>();
    private static final ThreadLocal<LayerState> PREV_MOB = new ThreadLocal<>();
    /** Saved Curios {@code getRenders()} flags while ghillie hides cosmetics. */
    private static final ThreadLocal<List<CurioRenderSnap>> CURIO_RENDERS = new ThreadLocal<>();
    /**
     * NPC Studio {@code NpcCuriosLayer} only draws slots with {@code getRenders()==true}
     * (or a cosmetic stack). Fashion slots often default off for non-players — force on for the frame.
     */
    private static final ThreadLocal<List<CurioRenderSnap>> FORCED_CURIO_RENDERS = new ThreadLocal<>();
    private static final Set<String> FASHION_CURIO_SLOTS = Set.of(
            CuriosClothing.OUTERWEAR_SLOT,
            CuriosClothing.VEST_SLOT,
            CuriosClothing.PANTS_SLOT,
            "mask",
            "body");

    private ClothingLayerHider() {
    }

    /** LOWEST so we run after other mods' Pre handlers, still before layers draw. */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderPre(RenderPlayerEvent.Pre event) {
        PlayerModel<?> model = event.getRenderer().getModel();
        applyPlayerModelHides(model, event.getEntity(), PREV);
        suppressCurioRenders(event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderPost(RenderPlayerEvent.Post event) {
        restoreCurioRenders();
        restorePlayerModel(event.getRenderer().getModel(), PREV);
    }

    /**
     * NPC Studio (and similar) use {@link PlayerModel} on non-players — apply the same
     * jacket/sleeve/hat/pants hides as for the local player.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        if (event.getEntity() instanceof AbstractClientPlayer) {
            return;
        }
        forceFashionCurioRenders(event.getEntity());
        if (!(event.getRenderer().getModel() instanceof PlayerModel<?> model)) {
            // Plain HumanoidModel only has hat — still blank when covered.
            if (event.getRenderer().getModel() instanceof HumanoidModel<?> humanoid) {
                hideHumanoidHat(humanoid, event.getEntity());
            }
            return;
        }
        applyPlayerModelHides(model, event.getEntity(), PREV_MOB);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLivingPost(RenderLivingEvent.Post<?, ?> event) {
        if (event.getEntity() instanceof AbstractClientPlayer) {
            return;
        }
        restoreForcedCurioRenders();
        if (event.getRenderer().getModel() instanceof PlayerModel<?> model) {
            restorePlayerModel(model, PREV_MOB);
            return;
        }
        Boolean prevHat = PREV_MOB_HAT.get();
        PREV_MOB_HAT.remove();
        if (prevHat != null && event.getRenderer().getModel() instanceof HumanoidModel<?> humanoid) {
            humanoid.hat.visible = prevHat;
        }
    }

    private static final ThreadLocal<Boolean> PREV_MOB_HAT = new ThreadLocal<>();

    private static void hideHumanoidHat(HumanoidModel<?> model, LivingEntity entity) {
        ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
        boolean hide = (head.getItem() instanceof ClothingItem wear && wear.getClothingType().hideHat)
                || wearingBalaclava(entity);
        if (!hide) {
            return;
        }
        PREV_MOB_HAT.set(model.hat.visible);
        model.hat.visible = false;
    }

    @SuppressWarnings("rawtypes")
    private static void applyPlayerModelHides(
            PlayerModel model, LivingEntity entity, ThreadLocal<LayerState> store) {
        store.set(new LayerState(
                model.jacket.visible,
                model.leftSleeve.visible,
                model.rightSleeve.visible,
                model.hat.visible,
                model.leftPants.visible,
                model.rightPants.visible
        ));

        ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (head.getItem() instanceof ClothingItem headWear && headWear.getClothingType().hideHat) {
            model.hat.visible = false;
        }
        if (wearingBalaclava(entity)) {
            model.hat.visible = false;
        }

        applyChestHide(model, entity.getItemBySlot(EquipmentSlot.CHEST));
        if (!GhillieCover.top(entity)) {
            applyChestHide(model, CuriosClothing.getOuterwear(entity));
        }
        ItemStack hooded = CuriosClothing.hoodedPiece(entity);
        if (!hooded.isEmpty() && ClothingItem.isHoodOpen(hooded) && !GhillieCover.hood(entity)) {
            boolean capOcclusion = head.getItem() instanceof ClothingItem hw
                    && hw.getClothingType() == ClothingType.CAP;
            if (!capOcclusion || wearingBalaclava(entity)) {
                model.hat.visible = false;
            }
        }

        ItemStack legs = entity.getItemBySlot(EquipmentSlot.LEGS);
        if (legs.getItem() instanceof ClothingItem legWear && legWear.getClothingType().hidePants) {
            model.leftPants.visible = false;
            model.rightPants.visible = false;
        }

        ItemStack feet = entity.getItemBySlot(EquipmentSlot.FEET);
        if (feet.getItem() instanceof ClothingItem shoes && shoes.getClothingType().hidePants) {
            model.leftPants.visible = false;
            model.rightPants.visible = false;
        }

        if (GhillieCover.hood(entity) && GhillieCover.top(entity)
                && GhillieCover.pants(entity) && GhillieCover.boots(entity)) {
            model.hat.visible = false;
            model.jacket.visible = false;
            model.leftSleeve.visible = false;
            model.rightSleeve.visible = false;
            model.leftPants.visible = false;
            model.rightPants.visible = false;
        }
    }

    @SuppressWarnings("rawtypes")
    private static void restorePlayerModel(PlayerModel model, ThreadLocal<LayerState> store) {
        LayerState prev = store.get();
        store.remove();
        if (prev == null) {
            return;
        }
        model.jacket.visible = prev.jacket;
        model.leftSleeve.visible = prev.leftSleeve;
        model.rightSleeve.visible = prev.rightSleeve;
        model.hat.visible = prev.hat;
        model.leftPants.visible = prev.leftPants;
        model.rightPants.visible = prev.rightPants;
    }

    /** Frame-local: force Curios cosmetic render flags off while ghillie is worn. */
    private static void suppressCurioRenders(LivingEntity entity) {
        CURIO_RENDERS.remove();
        if (!GhillieCover.hidesExtras(entity)) {
            return;
        }
        CuriosApi.getCuriosInventory(entity).ifPresent(inv -> {
            List<CurioRenderSnap> snaps = new ArrayList<>();
            for (Map.Entry<String, ICurioStacksHandler> entry : inv.getCurios().entrySet()) {
                NonNullList<Boolean> renders = entry.getValue().getRenders();
                for (int i = 0; i < renders.size(); i++) {
                    boolean was = Boolean.TRUE.equals(renders.get(i));
                    snaps.add(new CurioRenderSnap(renders, i, was));
                    renders.set(i, Boolean.FALSE);
                }
            }
            CURIO_RENDERS.set(snaps);
        });
    }

    private static void restoreCurioRenders() {
        restoreSnaps(CURIO_RENDERS);
    }

    /** Frame-local: enable fashion Curios render flags so NPC Curios layers see the stacks. */
    private static void forceFashionCurioRenders(LivingEntity entity) {
        FORCED_CURIO_RENDERS.remove();
        CuriosApi.getCuriosInventory(entity).ifPresent(inv -> {
            List<CurioRenderSnap> snaps = new ArrayList<>();
            for (Map.Entry<String, ICurioStacksHandler> entry : inv.getCurios().entrySet()) {
                if (!FASHION_CURIO_SLOTS.contains(entry.getKey())) {
                    continue;
                }
                NonNullList<Boolean> renders = entry.getValue().getRenders();
                for (int i = 0; i < renders.size(); i++) {
                    if (Boolean.TRUE.equals(renders.get(i))) {
                        continue;
                    }
                    snaps.add(new CurioRenderSnap(renders, i, false));
                    renders.set(i, Boolean.TRUE);
                }
            }
            if (!snaps.isEmpty()) {
                FORCED_CURIO_RENDERS.set(snaps);
            }
        });
    }

    private static void restoreForcedCurioRenders() {
        restoreSnaps(FORCED_CURIO_RENDERS);
    }

    private static void restoreSnaps(ThreadLocal<List<CurioRenderSnap>> store) {
        List<CurioRenderSnap> snaps = store.get();
        store.remove();
        if (snaps == null) {
            return;
        }
        for (CurioRenderSnap snap : snaps) {
            if (snap.index < snap.list.size()) {
                snap.list.set(snap.index, snap.was);
            }
        }
    }

    public static boolean wearingBalaclava(LivingEntity entity) {
        return CuriosApi.getCuriosInventory(entity)
                .resolve()
                .flatMap(inv -> inv.findFirstCurio(BalaclavaItem::isBalaclava))
                .isPresent();
    }

    /** Tactical / scrap / ghillie hood — suppress face cosmetics (mask, NVG, cigarette). */
    public static boolean hidesFaceCosmetics(LivingEntity entity) {
        ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
        return head.getItem() instanceof ClothingItem wear
                && wear.getClothingType().hidesFaceCosmetics();
    }

    @SuppressWarnings("rawtypes")
    private static void applyChestHide(PlayerModel model, ItemStack stack) {
        if (!(stack.getItem() instanceof ClothingItem clothing)) {
            return;
        }
        ClothingType type = clothing.getClothingType();
        if (type.hideJacket) {
            model.jacket.visible = false;
        }
        if (type.hideSleeves) {
            model.leftSleeve.visible = false;
            model.rightSleeve.visible = false;
        }
        if (type.hidePants) {
            model.leftPants.visible = false;
            model.rightPants.visible = false;
        }
    }

    private record LayerState(
            boolean jacket,
            boolean leftSleeve,
            boolean rightSleeve,
            boolean hat,
            boolean leftPants,
            boolean rightPants) {
    }

    private record CurioRenderSnap(NonNullList<Boolean> list, int index, boolean was) {
    }
}
