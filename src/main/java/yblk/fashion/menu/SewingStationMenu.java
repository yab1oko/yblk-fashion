package yblk.fashion.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import yblk.fashion.block.entity.SewingStationBlockEntity;
import yblk.fashion.init.ModBlocks;
import yblk.fashion.init.ModMenus;
import yblk.fashion.item.ClothingFabric;
import yblk.fashion.item.ClothingMods;
import yblk.fashion.item.TShirtColor;
import yblk.fashion.sewing.SewingCategory;
import yblk.fashion.sewing.SewingProduct;

/**
 * CGM-style workbench: fabric from player inventory, dye in station slot.
 * Buttons: 0 = prev, 1 = next, 2 = sew, 10+ = category rail.
 */
public class SewingStationMenu extends AbstractContainerMenu {
    public static final int BTN_PREV = 0;
    public static final int BTN_NEXT = 1;
    public static final int BTN_SEW = 2;
    public static final int BTN_CATEGORY_BASE = 10;

    /** Full GUI width: 256 content + 3px frame on each side. */
    public static final int GUI_WIDTH = 262;
    /** Custom top panel height; vanilla inventory blit starts here. */
    public static final int TOP_HEIGHT = 105;
    public static final int INV_ORIGIN_X = 43;
    public static final int INV_ORIGIN_Y = TOP_HEIGHT;
    public static final int INV_X = INV_ORIGIN_X + 8;
    public static final int INV_Y = INV_ORIGIN_Y;
    public static final int HOTBAR_Y = INV_ORIGIN_Y + 58;

    /** Sew button bounds — material row is left-aligned under it. */
    public static final int SEW_X = 152;
    public static final int SEW_W = 96;
    public static final int MAT_SLOT_SIZE = 18;
    public static final int MAT_SLOT_STRIDE = 20;
    public static final int MAT_SLOT_Y = 56;
    /** Dye leftmost under Sew; fabric / kit / leather follow to the right. */
    public static final int DYE_SLOT_X = SEW_X;
    public static final int DYE_SLOT_Y = MAT_SLOT_Y;
    /** Item origin inside the 18×18 chrome (1px inset, same as material ghosts). */
    public static final int DYE_ITEM_X = DYE_SLOT_X + 1;
    public static final int DYE_ITEM_Y = DYE_SLOT_Y + 1;

    private final SewingStationBlockEntity station;
    private final ContainerLevelAccess access;
    private final ContainerData data;
    private final Inventory playerInv;

    public SewingStationMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, getStation(inv, buf.readBlockPos()));
    }

    public SewingStationMenu(int id, Inventory inv, SewingStationBlockEntity station) {
        super(ModMenus.SEWING_STATION.get(), id);
        this.station = station;
        this.playerInv = inv;
        this.access = ContainerLevelAccess.create(station.getLevel(), station.getBlockPos());
        this.data = new SimpleContainerData(2) {
            @Override
            public int get(int index) {
                return index == 0 ? station.getSelectedProduct() : station.getSelectedCategoryIndex();
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) {
                    station.setSelectedProduct(value);
                } else {
                    station.setSelectedCategoryIndex(value);
                }
            }
        };

        addSlot(new SlotItemHandler(station.getItems(), SewingStationBlockEntity.SLOT_DYE, DYE_ITEM_X, DYE_ITEM_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return TShirtColor.fromDye(stack.getItem()) != null;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, col + row * 9 + 9, INV_X + col * 18, INV_Y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, INV_X + col * 18, HOTBAR_Y));
        }

        addDataSlots(data);
    }

    /**
     * Material ghost X positions packed right of the dye under Sew.
     * Order L→R: dye, fabric, kit?, leather?, plates?, wheat?, leaves?.
     */
    public int[] materialSlotXs() {
        SewingProduct product = getSelectedProduct();
        boolean showFabric = product.fabricCount() > 0;
        boolean showKit = product.effectiveSewingKitCost() > 0;
        boolean showLeather = product.leatherCount() > 0;
        boolean showPlates = product.plateCount() > 0;
        boolean showWheat = product.wheatCount() > 0;
        boolean showLeaves = product.leavesCount() > 0;
        int cursor = DYE_SLOT_X + MAT_SLOT_STRIDE;
        int fabricX = -1;
        if (showFabric) {
            fabricX = cursor;
            cursor += MAT_SLOT_STRIDE;
        }
        int kitX = showKit ? cursor : -1;
        if (showKit) {
            cursor += MAT_SLOT_STRIDE;
        }
        int leatherX = showLeather ? cursor : -1;
        if (showLeather) {
            cursor += MAT_SLOT_STRIDE;
        }
        int plateX = showPlates ? cursor : -1;
        if (showPlates) {
            cursor += MAT_SLOT_STRIDE;
        }
        int wheatX = showWheat ? cursor : -1;
        if (showWheat) {
            cursor += MAT_SLOT_STRIDE;
        }
        int leavesX = showLeaves ? cursor : -1;
        return new int[]{fabricX, kitX, leatherX, plateX, wheatX, leavesX};
    }

    private static SewingStationBlockEntity getStation(Inventory inv, net.minecraft.core.BlockPos pos) {
        BlockEntity be = inv.player.level().getBlockEntity(pos);
        if (be instanceof SewingStationBlockEntity station) {
            return station;
        }
        throw new IllegalStateException("Sewing station missing at " + pos);
    }

    public SewingStationBlockEntity getStation() {
        return station;
    }

    public Inventory getPlayerInventory() {
        return playerInv;
    }

    public int getSelectedProductIndex() {
        return data.get(0);
    }

    public SewingProduct getSelectedProduct() {
        return SewingProduct.byIndex(getSelectedProductIndex());
    }

    public TShirtColor dyeInSlot() {
        return TShirtColor.fromDye(station.getDyeStack().getItem());
    }

    public int countFabric() {
        int total = 0;
        for (int i = 0; i < playerInv.getContainerSize(); i++) {
            ItemStack stack = playerInv.getItem(i);
            if (ClothingFabric.isSewingFabric(stack)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    public int countSewingKits() {
        Item kit = ClothingFabric.sewingKitItem();
        if (kit == null) {
            return 0;
        }
        int total = 0;
        for (int i = 0; i < playerInv.getContainerSize(); i++) {
            ItemStack stack = playerInv.getItem(i);
            if (stack.is(kit)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    public int countLeather() {
        int total = 0;
        for (int i = 0; i < playerInv.getContainerSize(); i++) {
            ItemStack stack = playerInv.getItem(i);
            if (stack.is(Items.LEATHER)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    public int countPlates() {
        return countMatching(ClothingFabric.sewingPlateIngredient());
    }

    public int countWheat() {
        int total = 0;
        for (int i = 0; i < playerInv.getContainerSize(); i++) {
            ItemStack stack = playerInv.getItem(i);
            if (stack.is(Items.WHEAT)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    public int countLeaves() {
        return countMatching(Ingredient.of(net.minecraft.tags.ItemTags.LEAVES));
    }

    public boolean canSew() {
        if (getSelectedCategory().productIndices().isEmpty()) {
            return false;
        }
        SewingProduct product = getSelectedProduct();
        if (countFabric() < product.fabricCount()) {
            return false;
        }
        if (product.leatherCount() > 0 && countLeather() < product.leatherCount()) {
            return false;
        }
        if (product.plateCount() > 0 && countPlates() < product.plateCount()) {
            return false;
        }
        if (product.wheatCount() > 0 && countWheat() < product.wheatCount()) {
            return false;
        }
        if (product.leavesCount() > 0 && countLeaves() < product.leavesCount()) {
            return false;
        }
        int kitCost = product.effectiveSewingKitCost();
        if (kitCost > 0 && countSewingKits() < kitCost) {
            return false;
        }
        if (!product.needsDye()) {
            return true;
        }
        TShirtColor dye = dyeInSlot();
        return dye != null && product.acceptsDye(dye);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == BTN_PREV) {
            station.cycleProduct(-1);
            broadcastChanges();
            return true;
        }
        if (id == BTN_NEXT) {
            station.cycleProduct(1);
            broadcastChanges();
            return true;
        }
        if (id == BTN_SEW) {
            return trySew(player);
        }
        if (id >= BTN_CATEGORY_BASE && id < BTN_CATEGORY_BASE + SewingCategory.values().length) {
            station.selectCategory(SewingCategory.values()[id - BTN_CATEGORY_BASE]);
            broadcastChanges();
            return true;
        }
        return false;
    }

    public SewingCategory getSelectedCategory() {
        SewingCategory[] values = SewingCategory.values();
        int i = Math.floorMod(data.get(1), values.length);
        return values[i];
    }

    private boolean trySew(Player player) {
        if (player.level().isClientSide) {
            return true;
        }
        SewingProduct product = getSelectedProduct();
        TShirtColor dyeColor = null;
        if (product.needsDye()) {
            dyeColor = dyeInSlot();
            if (dyeColor == null || !product.acceptsDye(dyeColor)) {
                return false;
            }
        }
        if (countMatching(product.fabric()) < product.fabricCount()) {
            return false;
        }
        if (product.leatherCount() > 0 && countLeather() < product.leatherCount()) {
            return false;
        }
        if (product.plateCount() > 0 && countPlates() < product.plateCount()) {
            return false;
        }
        if (product.wheatCount() > 0 && countWheat() < product.wheatCount()) {
            return false;
        }
        if (product.leavesCount() > 0 && countLeaves() < product.leavesCount()) {
            return false;
        }
        int kitCost = product.effectiveSewingKitCost();
        Item kit = ClothingFabric.sewingKitItem();
        if (kitCost > 0 && (kit == null || countSewingKits() < kitCost)) {
            return false;
        }

        if (!consumeMatching(product.fabric(), product.fabricCount())) {
            return false;
        }
        if (product.leatherCount() > 0 && !consumeItem(Items.LEATHER, product.leatherCount())) {
            return false;
        }
        if (product.plateCount() > 0 && !consumeMatching(product.plates(), product.plateCount())) {
            return false;
        }
        if (product.wheatCount() > 0 && !consumeItem(Items.WHEAT, product.wheatCount())) {
            return false;
        }
        if (product.leavesCount() > 0 && !consumeMatching(product.leaves(), product.leavesCount())) {
            return false;
        }
        if (kitCost > 0 && !consumeItem(kit, kitCost)) {
            return false;
        }
        if (product.needsDye()) {
            ItemStack dye = station.getDyeStack();
            dye.shrink(1);
            station.getItems().setStackInSlot(SewingStationBlockEntity.SLOT_DYE, dye);
        }

        ItemStack result = product.needsDye() && dyeColor != null
                ? product.resultForDye(dyeColor)
                : product.iconStack();
        ClothingMods.maybeRollSlots(result, player.getRandom());
        if (!player.getInventory().add(result)) {
            player.drop(result, false);
        }
        player.level().playSound(null, station.getBlockPos(), SoundEvents.UI_LOOM_TAKE_RESULT,
                SoundSource.BLOCKS, 1f, 1f);
        broadcastChanges();
        return true;
    }

    private int countMatching(Ingredient ingredient) {
        int total = 0;
        for (int i = 0; i < playerInv.getContainerSize(); i++) {
            ItemStack stack = playerInv.getItem(i);
            if (ingredient.test(stack)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private boolean consumeMatching(Ingredient ingredient, int amount) {
        int need = amount;
        for (int i = 0; i < playerInv.getContainerSize() && need > 0; i++) {
            ItemStack stack = playerInv.getItem(i);
            if (!ingredient.test(stack)) {
                continue;
            }
            int take = Math.min(need, stack.getCount());
            stack.shrink(take);
            need -= take;
        }
        return need == 0;
    }

    private boolean consumeItem(Item item, int amount) {
        int need = amount;
        for (int i = 0; i < playerInv.getContainerSize() && need > 0; i++) {
            ItemStack stack = playerInv.getItem(i);
            if (!stack.is(item)) {
                continue;
            }
            int take = Math.min(need, stack.getCount());
            stack.shrink(take);
            need -= take;
        }
        return need == 0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index == 0) {
                if (!this.moveItemStackTo(stack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (TShirtColor.fromDye(stack.getItem()) != null) {
                if (!this.moveItemStackTo(stack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 28) {
                if (!this.moveItemStackTo(stack, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 1, 28, false)) {
                return ItemStack.EMPTY;
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.SEWING_STATION.get());
    }
}
