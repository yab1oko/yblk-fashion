package yblk.fashion.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yblk.fashion.init.ModBlockEntities;
import yblk.fashion.item.TShirtColor;
import yblk.fashion.menu.SewingStationMenu;
import yblk.fashion.sewing.SewingCategory;
import yblk.fashion.sewing.SewingProduct;

import java.util.List;

/** Workbench: one dye slot; fabric is pulled from the player inventory on sew. */
public class SewingStationBlockEntity extends BlockEntity implements MenuProvider {
    public static final int SLOT_DYE = 0;

    private final ItemStackHandler items = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return TShirtColor.fromDye(stack.getItem()) != null;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }
    };

    private final LazyOptional<IItemHandler> itemCap = LazyOptional.of(() -> items);
    private int selectedProduct;
    private int selectedCategory = SewingCategory.CHEST.ordinal();

    public SewingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SEWING_STATION.get(), pos, state);
    }

    public ItemStackHandler getItems() {
        return items;
    }

    public ItemStack getDyeStack() {
        return items.getStackInSlot(SLOT_DYE);
    }

    public int getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(int index) {
        int size = SewingProduct.all().size();
        if (size == 0 || index < 0 || index >= size) {
            return;
        }
        this.selectedProduct = index;
        setChanged();
    }

    public void cycleProduct(int delta) {
        SewingCategory category = currentCategory();
        List<Integer> indices = category.productIndices();
        if (indices.isEmpty()) {
            return;
        }
        int pos = indices.indexOf(selectedProduct);
        if (pos < 0) {
            setSelectedProduct(indices.get(0));
            return;
        }
        setSelectedProduct(indices.get(Math.floorMod(pos + delta, indices.size())));
    }

    public void selectCategory(SewingCategory category) {
        this.selectedCategory = category.ordinal();
        int first = category.firstProductIndex();
        if (first >= 0) {
            this.selectedProduct = first;
        }
        setChanged();
    }

    public int getSelectedCategoryIndex() {
        return selectedCategory;
    }

    public void setSelectedCategoryIndex(int index) {
        SewingCategory[] values = SewingCategory.values();
        if (index < 0 || index >= values.length) {
            return;
        }
        this.selectedCategory = index;
        setChanged();
    }

    public SewingCategory currentCategory() {
        SewingCategory[] values = SewingCategory.values();
        int i = Math.floorMod(selectedCategory, values.length);
        return values[i];
    }

    public SewingProduct currentProduct() {
        return SewingProduct.byIndex(selectedProduct);
    }

    public void dropContents(Level level, BlockPos pos) {
        ItemStack dye = items.getStackInSlot(SLOT_DYE);
        if (!dye.isEmpty()) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), dye);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.fashion.sewing_station");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new SewingStationMenu(id, inv, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Items", items.serializeNBT());
        tag.putInt("SelectedProduct", selectedProduct);
        tag.putInt("SelectedCategory", selectedCategory);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        items.deserializeNBT(tag.getCompound("Items"));
        selectedProduct = tag.getInt("SelectedProduct");
        if (tag.contains("SelectedCategory")) {
            selectedCategory = tag.getInt("SelectedCategory");
        } else {
            selectedCategory = SewingCategory.ofProduct(SewingProduct.byIndex(selectedProduct)).ordinal();
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemCap.invalidate();
    }
}
