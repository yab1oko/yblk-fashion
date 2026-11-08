package yblk.fashion.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import yblk.fashion.Fashion;
import yblk.fashion.compat.FeaturesCompat;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Curios {@code body} portable bag — vanilla-bundle click UX (no GUI).
 * Subclasses set slot count and texture id.
 */
public abstract class PortableBagItem extends CuriosAccessoryItem {
    public static final String CURIOS_SLOT = "body";
    private static final String TAG_ITEMS = "Items";
    private static final String TAG_SELECTED = "Selected";
    private static final int BAR_COLOR = 0x6666FF;

    private final TShirtColor color;
    private final int bagSlots;
    private final int featuresSlots;
    private final String textureSuffix;

    protected PortableBagItem(TShirtColor color, int bagSlots, int featuresSlots, String textureSuffix) {
        super(new Properties().stacksTo(1), CURIOS_SLOT);
        this.color = color;
        this.bagSlots = bagSlots;
        this.featuresSlots = featuresSlots;
        this.textureSuffix = textureSuffix;
    }

    public TShirtColor getColor() {
        return color;
    }

    public int bagSlots() {
        return bagSlots;
    }

    public int featuresSlots() {
        return featuresSlots;
    }

    public ResourceLocation modelTexture() {
        return new ResourceLocation(Fashion.MODID, "textures/models/" + color.id + "_" + textureSuffix + ".png");
    }

    public static boolean isBag(ItemStack stack) {
        return stack.getItem() instanceof PortableBagItem;
    }

    @Nullable
    public static PortableBagItem of(ItemStack stack) {
        return stack.getItem() instanceof PortableBagItem bag ? bag : null;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return FeaturesCompat.isFeaturesPresent();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (FeaturesCompat.isFeaturesPresent()) {
            return InteractionResultHolder.pass(stack);
        }
        if (dropContents(stack, player)) {
            playDropContentsSound(player);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack bag, Slot slot, ClickAction action, Player player) {
        if (bag.getCount() != 1 || action != ClickAction.SECONDARY) {
            return false;
        }
        ItemStack inSlot = slot.getItem();
        if (inSlot.isEmpty()) {
            removeOne(bag).ifPresent(removed -> {
                playRemoveOneSound(player);
                ItemStack leftover = slot.safeInsert(removed);
                if (!leftover.isEmpty()) {
                    insert(bag, leftover);
                }
            });
        } else if (inSlot.getItem().canFitInsideContainerItems()) {
            ItemStack taken = slot.safeTake(inSlot.getCount(), inSlot.getCount(), player);
            int added = insert(bag, taken);
            if (added > 0) {
                playInsertSound(player);
                taken.shrink(added);
            }
            if (!taken.isEmpty()) {
                ItemStack bounced = slot.safeInsert(taken);
                if (!bounced.isEmpty() && !player.getInventory().add(bounced)) {
                    player.drop(bounced, false);
                }
            }
        }
        return true;
    }

    @Override
    public boolean overrideOtherStackedOnMe(
            ItemStack bag,
            ItemStack other,
            Slot slot,
            ClickAction action,
            Player player,
            SlotAccess access) {
        if (bag.getCount() != 1 || action != ClickAction.SECONDARY) {
            return false;
        }
        if (!slot.allowModification(player)) {
            return false;
        }
        if (other.isEmpty()) {
            removeOne(bag).ifPresent(removed -> {
                playRemoveOneSound(player);
                access.set(removed);
            });
        } else if (other.getItem().canFitInsideContainerItems()) {
            int added = insert(bag, other);
            if (added > 0) {
                playInsertSound(player);
                other.shrink(added);
            }
        }
        return true;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return !FeaturesCompat.isFeaturesPresent() && occupiedSlots(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int slots = bagSlots(stack);
        return Math.min(1 + 12 * occupiedSlots(stack) / Math.max(1, slots), 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return BAR_COLOR;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (FeaturesCompat.isFeaturesPresent()) {
            return Optional.empty();
        }
        NonNullList<ItemStack> items = NonNullList.create();
        getContents(stack).forEach(items::add);
        int slots = bagSlots(stack);
        int weight = occupiedSlots(stack) * 64 / Math.max(1, slots);
        return Optional.of(new BundleTooltip(items, weight));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tip, TooltipFlag flag) {
        PortableBagItem self = of(stack);
        int slots = self != null ? self.bagSlots : bagSlots;
        // Features ArmorSlotTooltip owns the "+N slots" line when Features is loaded.
        if (!FeaturesCompat.isFeaturesPresent()) {
            tip.add(Component.translatable("item.minecraft.bundle.fullness", occupiedSlots(stack), slots)
                    .withStyle(ChatFormatting.GRAY));
            if (occupiedSlots(stack) > 1) {
                tip.add(Component.translatable("item.fashion.clutch.scroll")
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }
        tip.add(Component.translatable("item.fashion.clutch.slot"));
    }

    public static boolean cycleSelected(ItemStack bag, int direction) {
        PortableBagItem item = of(bag);
        if (item == null || direction == 0 || FeaturesCompat.isFeaturesPresent()) {
            return false;
        }
        ItemStackHandler handler = readBag(bag);
        if (occupiedSlots(handler) <= 1) {
            return false;
        }
        int sign = direction > 0 ? 1 : -1;
        int current = getSelectedSlot(bag, handler);
        int size = item.bagSlots;
        for (int step = 1; step <= size; step++) {
            int idx = Math.floorMod(current + sign * step, size);
            if (!handler.getStackInSlot(idx).isEmpty()) {
                return setSelectedSlot(bag, idx);
            }
        }
        return false;
    }

    public static boolean setSelectedSlot(ItemStack bag, int slot) {
        PortableBagItem item = of(bag);
        if (item == null || FeaturesCompat.isFeaturesPresent()) {
            return false;
        }
        if (slot < 0 || slot >= item.bagSlots) {
            return false;
        }
        ItemStackHandler handler = readBag(bag);
        if (handler.getStackInSlot(slot).isEmpty()) {
            return false;
        }
        int before = getSelectedSlot(bag, handler);
        setSelected(bag, slot);
        return before != slot;
    }

    public static int getSelectedSlotIndex(ItemStack bag) {
        return getSelectedSlot(bag, readBag(bag));
    }

    @Override
    public void onDestroyed(ItemEntity entity) {
        ItemUtils.onContainerDestroyed(entity, getContents(entity.getItem()));
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    private static int bagSlots(ItemStack stack) {
        PortableBagItem item = of(stack);
        return item != null ? item.bagSlots : 6;
    }

    private static int insert(ItemStack bag, ItemStack inserted) {
        if (inserted.isEmpty() || !inserted.getItem().canFitInsideContainerItems() || isBag(inserted)) {
            return 0;
        }
        ItemStackHandler handler = readBag(bag);
        ItemStack leftover = ItemHandlerHelper.insertItemStacked(handler, inserted.copy(), false);
        writeBag(bag, handler);
        clampSelected(bag, handler);
        return inserted.getCount() - leftover.getCount();
    }

    private static Optional<ItemStack> removeOne(ItemStack bag) {
        ItemStackHandler handler = readBag(bag);
        int sel = getSelectedSlot(bag, handler);
        ItemStack stack = handler.getStackInSlot(sel);
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        ItemStack taken = handler.extractItem(sel, stack.getCount(), false);
        writeBag(bag, handler);
        clampSelected(bag, handler);
        return Optional.of(taken);
    }

    private static boolean dropContents(ItemStack bag, Player player) {
        ItemStackHandler handler = readBag(bag);
        boolean any = false;
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            any = true;
            if (!player.level().isClientSide) {
                player.drop(stack.copy(), true);
            }
            handler.setStackInSlot(i, ItemStack.EMPTY);
        }
        if (any) {
            writeBag(bag, handler);
            clearSelected(bag);
        }
        return any;
    }

    private static ItemStackHandler readBag(ItemStack bag) {
        int slots = bagSlots(bag);
        ItemStackHandler handler = new ItemStackHandler(slots);
        CompoundTag tag = bag.getTag();
        if (tag == null || !tag.contains(TAG_ITEMS, Tag.TAG_LIST)) {
            return handler;
        }
        ListTag list = tag.getList(TAG_ITEMS, Tag.TAG_COMPOUND);
        boolean slotted = !list.isEmpty() && list.getCompound(0).contains("Slot", Tag.TAG_BYTE);
        if (slotted) {
            CompoundTag wrap = new CompoundTag();
            wrap.putInt("Size", slots);
            wrap.put(TAG_ITEMS, list);
            handler.deserializeNBT(wrap);
        } else {
            int slot = 0;
            for (int i = 0; i < list.size() && slot < slots; i++) {
                ItemStack stack = ItemStack.of(list.getCompound(i));
                if (!stack.isEmpty()) {
                    handler.setStackInSlot(slot++, stack);
                }
            }
        }
        return handler;
    }

    private static void writeBag(ItemStack bag, ItemStackHandler handler) {
        ListTag list = new ListTag();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            CompoundTag entry = new CompoundTag();
            entry.putByte("Slot", (byte) i);
            stack.save(entry);
            list.add(entry);
        }
        CompoundTag tag = bag.getOrCreateTag();
        if (list.isEmpty()) {
            tag.remove(TAG_ITEMS);
            tag.remove(TAG_SELECTED);
            if (tag.isEmpty()) {
                bag.setTag(null);
            } else {
                bag.setTag(tag);
            }
        } else {
            tag.put(TAG_ITEMS, list);
            bag.setTag(tag);
        }
    }

    private static Stream<ItemStack> getContents(ItemStack bag) {
        ItemStackHandler handler = readBag(bag);
        int sel = getSelectedSlot(bag, handler);
        return IntStream.range(0, handler.getSlots())
                .map(i -> (sel + i) % handler.getSlots())
                .mapToObj(handler::getStackInSlot)
                .filter(s -> !s.isEmpty());
    }

    private static int occupiedSlots(ItemStack bag) {
        return occupiedSlots(readBag(bag));
    }

    private static int occupiedSlots(ItemStackHandler handler) {
        int n = 0;
        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty()) {
                n++;
            }
        }
        return n;
    }

    private static int getSelectedSlot(ItemStack bag, ItemStackHandler handler) {
        int size = handler.getSlots();
        CompoundTag tag = bag.getTag();
        int sel = tag != null && tag.contains(TAG_SELECTED) ? tag.getInt(TAG_SELECTED) : 0;
        if (sel >= 0 && sel < size && !handler.getStackInSlot(sel).isEmpty()) {
            return sel;
        }
        for (int i = 0; i < size; i++) {
            if (!handler.getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return 0;
    }

    private static void setSelected(ItemStack bag, int slot) {
        CompoundTag tag = bag.getOrCreateTag();
        tag.putInt(TAG_SELECTED, slot);
        bag.setTag(tag);
    }

    private static void clearSelected(ItemStack bag) {
        CompoundTag tag = bag.getTag();
        if (tag == null) {
            return;
        }
        tag.remove(TAG_SELECTED);
        if (tag.isEmpty()) {
            bag.setTag(null);
        }
    }

    private static void clampSelected(ItemStack bag, ItemStackHandler handler) {
        if (occupiedSlots(handler) == 0) {
            clearSelected(bag);
            return;
        }
        setSelected(bag, getSelectedSlot(bag, handler));
    }

    private static void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F,
                0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private static void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F,
                0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private static void playDropContentsSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F,
                0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }
}
