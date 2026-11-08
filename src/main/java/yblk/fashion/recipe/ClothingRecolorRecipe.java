package yblk.fashion.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import yblk.fashion.init.ModItems;
import yblk.fashion.init.ModRecipes;
import yblk.fashion.item.BalaclavaItem;
import yblk.fashion.item.BandanaItem;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClothingMods;
import yblk.fashion.item.ClothingType;
import yblk.fashion.item.TShirtColor;

/** Shapeless: clothing/bandana/balaclava + dye → same piece in the new color. */
public class ClothingRecolorRecipe extends CustomRecipe {
    public ClothingRecolorRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack clothing = ItemStack.EMPTY;
        ItemStack dye = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof ClothingItem
                    || stack.getItem() instanceof BandanaItem
                    || stack.getItem() instanceof BalaclavaItem) {
                if (!clothing.isEmpty()) {
                    return false;
                }
                clothing = stack;
            } else if (TShirtColor.fromDye(stack.getItem()) != null) {
                if (!dye.isEmpty()) {
                    return false;
                }
                dye = stack;
            } else {
                return false;
            }
        }

        if (clothing.isEmpty() || dye.isEmpty()) {
            return false;
        }
        TShirtColor next = TShirtColor.fromDye(dye.getItem());
        if (next == null) {
            return false;
        }
        if (clothing.getItem() instanceof BandanaItem bandana) {
            return next.hasBandana() && bandana.getColor() != next;
        }
        if (clothing.getItem() instanceof BalaclavaItem balaclava) {
            return next.hasBalaclava() && balaclava.getColor() != next;
        }
        ClothingItem item = (ClothingItem) clothing.getItem();
        if (item.getColor() == next) {
            return false;
        }
        ClothingType type = item.getClothingType();
        if (type.isGhillie() || type == ClothingType.STRAW_HAT) {
            return false;
        }
        if (type == ClothingType.COAT) {
            return next.hasCoat();
        }
        if (type == ClothingType.SHOES) {
            return next.hasShoes();
        }
        if (type == ClothingType.COWBOY_HAT) {
            return next.hasCowboyHat();
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
        ItemStack clothing = ItemStack.EMPTY;
        TShirtColor next = null;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.getItem() instanceof ClothingItem
                    || stack.getItem() instanceof BandanaItem
                    || stack.getItem() instanceof BalaclavaItem) {
                clothing = stack;
            } else {
                TShirtColor dye = TShirtColor.fromDye(stack.getItem());
                if (dye != null) {
                    next = dye;
                }
            }
        }

        if (clothing.isEmpty() || next == null) {
            return ItemStack.EMPTY;
        }

        if (clothing.getItem() instanceof BandanaItem) {
            if (!next.hasBandana()) {
                return ItemStack.EMPTY;
            }
            ItemStack result = new ItemStack(ModItems.BANDANAS.get(next).get());
            result.setDamageValue(clothing.getDamageValue());
            return result;
        }

        if (clothing.getItem() instanceof BalaclavaItem) {
            if (!next.hasBalaclava()) {
                return ItemStack.EMPTY;
            }
            ItemStack result = new ItemStack(ModItems.BALACLAVAS.get(next).get());
            result.setDamageValue(clothing.getDamageValue());
            return result;
        }

        if (!(clothing.getItem() instanceof ClothingItem item)) {
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(ModItems.clothing(item.getClothingType(), next));
        result.setDamageValue(clothing.getDamageValue());
        if (item.getClothingType().hasHood() && ClothingItem.isHoodOpen(clothing)) {
            ClothingItem.setHoodOpen(result, true);
        }
        ClothingMods.copyTo(clothing, result);
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CLOTHING_RECOLOR_SERIALIZER.get();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        return NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
    }
}
