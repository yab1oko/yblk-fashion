package yblk.fashion.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import yblk.fashion.init.ModRecipes;
import yblk.fashion.init.ModTags;
import yblk.fashion.item.BalaclavaItem;
import yblk.fashion.item.BandanaItem;
import yblk.fashion.item.NvgItem;
import yblk.fashion.item.ClothingFabric;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.PortableBagItem;

/**
 * Shapeless: any Fashion garment + knife/shears → fabric/leather (craft cost − 1).
 * Knife / shears are returned with 1 durability lost (removed when broken).
 */
public class ClothingUncraftRecipe extends CustomRecipe {
    private static final RandomSource DAMAGE_RANDOM = RandomSource.create();
    public ClothingUncraftRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        ItemStack clothing = ItemStack.EMPTY;
        ItemStack knife = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (isGarment(stack)) {
                if (!clothing.isEmpty()) {
                    return false;
                }
                clothing = stack;
            } else if (ModTags.isKnife(stack)) {
                if (!knife.isEmpty()) {
                    return false;
                }
                knife = stack;
            } else {
                return false;
            }
        }

        return !clothing.isEmpty() && !knife.isEmpty() && !uncraftResult(clothing).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (isGarment(stack)) {
                return uncraftResult(stack);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CLOTHING_UNCRAFT_SERIALIZER.get();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> remains = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (ModTags.isKnife(stack)) {
                remains.set(i, damagedTool(stack));
            } else if (stack.hasCraftingRemainingItem()) {
                remains.set(i, stack.getCraftingRemainingItem());
            }
        }
        return remains;
    }

    /** Return knife/shears with −1 durability, or empty if the tool breaks. */
    private static ItemStack damagedTool(ItemStack stack) {
        ItemStack tool = stack.copy();
        if (!tool.isDamageableItem()) {
            return tool;
        }
        if (tool.hurt(1, DAMAGE_RANDOM, null)) {
            return ItemStack.EMPTY;
        }
        return tool;
    }

    private static boolean isGarment(ItemStack stack) {
        return stack.getItem() instanceof ClothingItem
                || stack.getItem() instanceof PortableBagItem
                || stack.getItem() instanceof BandanaItem
                || stack.getItem() instanceof BalaclavaItem
                || stack.getItem() instanceof NvgItem;
    }

    private static ItemStack uncraftResult(ItemStack garment) {
        if (garment.getItem() instanceof ClothingItem clothing) {
            return ClothingFabric.uncraftOutput(clothing);
        }
        if (garment.getItem() instanceof PortableBagItem bag) {
            return ClothingFabric.uncraftBagOutput(bag);
        }
        if (garment.getItem() instanceof BandanaItem) {
            return ClothingFabric.uncraftSimpleFabric(1);
        }
        if (garment.getItem() instanceof BalaclavaItem) {
            return ClothingFabric.uncraftSimpleFabric(2);
        }
        if (garment.getItem() instanceof NvgItem) {
            return ClothingFabric.uncraftSimpleFabric(1);
        }
        return ItemStack.EMPTY;
    }
}
