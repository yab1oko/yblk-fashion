package yblk.fashion.compat;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClothingType;

import java.util.Optional;
import java.util.function.Predicate;

/** Resolve fashion outerwear / vest / pants from Curios slots. */
public final class CuriosClothing {
    public static final String OUTERWEAR_SLOT = "outerwear";
    public static final String VEST_SLOT = "vest";
    public static final String PANTS_SLOT = "pants";

    private CuriosClothing() {
    }

    public static ItemStack getOuterwear(LivingEntity entity) {
        return findFirst(entity, CuriosClothing::isOuterwearStack)
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }

    public static ItemStack getVest(LivingEntity entity) {
        return findFirst(entity, CuriosClothing::isVestStack)
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }

    public static ItemStack getPants(LivingEntity entity) {
        return findFirst(entity, CuriosClothing::isPantsStack)
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }

    /** Equipped outerwear only if Curios slot render is enabled. */
    public static ItemStack getRenderedOuterwear(LivingEntity entity) {
        return findFirstRendered(entity, CuriosClothing::isOuterwearStack)
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }

    public static ItemStack getRenderedVest(LivingEntity entity) {
        return findFirstRendered(entity, CuriosClothing::isVestStack)
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }

    public static ItemStack getRenderedPants(LivingEntity entity) {
        return findFirstRendered(entity, CuriosClothing::isPantsStack)
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }

    public static boolean isVestStack(ItemStack stack) {
        return stack.getItem() instanceof ClothingItem clothing
                && clothing.getClothingType().isVest();
    }

    public static boolean isPantsStack(ItemStack stack) {
        return stack.getItem() instanceof ClothingItem clothing
                && clothing.getClothingType().isCurioPants();
    }

    public static Optional<SlotResult> findOuterwear(LivingEntity entity) {
        return findFirst(entity, CuriosClothing::isOuterwearStack);
    }

    public static boolean isOuterwearStack(ItemStack stack) {
        return stack.getItem() instanceof ClothingItem clothing
                && clothing.getClothingType().isOuterwear();
    }

    public static boolean isHoodOpenOuterwear(LivingEntity entity) {
        ItemStack outer = getOuterwear(entity);
        return !outer.isEmpty()
                && outer.getItem() instanceof ClothingItem clothing
                && clothing.getClothingType().hasHood()
                && ClothingItem.isHoodOpen(outer);
    }

    /** Prefer Curios outerwear; fall back to chest for legacy worlds. */
    public static ItemStack hoodedPiece(LivingEntity entity) {
        ItemStack outer = getOuterwear(entity);
        if (!outer.isEmpty() && outer.getItem() instanceof ClothingItem c && c.getClothingType().hasHood()) {
            return outer;
        }
        ItemStack chest = entity.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
        if (chest.getItem() instanceof ClothingItem c && c.getClothingType().hasHood()) {
            return chest;
        }
        return ItemStack.EMPTY;
    }

    public static ClothingType outerwearType(LivingEntity entity) {
        ItemStack outer = getOuterwear(entity);
        if (outer.getItem() instanceof ClothingItem clothing) {
            return clothing.getClothingType();
        }
        return null;
    }

    /**
     * Whether Curios would draw this slot (visibility toggle + per-slot render flag).
     * Equipped-but-hidden cosmetics stay equipped for attributes / Features.
     */
    public static boolean isRendered(LivingEntity entity, SlotResult result) {
        if (entity == null || result == null) {
            return false;
        }
        Optional<ICuriosItemHandler> inv = CuriosApi.getCuriosInventory(entity).resolve();
        if (inv.isEmpty()) {
            return true;
        }
        Optional<ICurioStacksHandler> handler = inv.get().getStacksHandler(result.slotContext().identifier());
        if (handler.isEmpty()) {
            return true;
        }
        ICurioStacksHandler stacks = handler.get();
        if (!stacks.isVisible()) {
            return false;
        }
        int index = result.slotContext().index();
        NonNullList<Boolean> renders = stacks.getRenders();
        if (index < 0 || index >= renders.size()) {
            return true;
        }
        return Boolean.TRUE.equals(renders.get(index));
    }

    /** First matching curio that is currently set to render. */
    public static Optional<SlotResult> findFirstRendered(LivingEntity entity, Predicate<ItemStack> match) {
        return findFirst(entity, match).filter(r -> isRendered(entity, r));
    }

    private static Optional<SlotResult> findFirst(LivingEntity entity, Predicate<ItemStack> match) {
        return CuriosApi.getCuriosInventory(entity)
                .resolve()
                .flatMap(inv -> inv.findFirstCurio(match));
    }
}
