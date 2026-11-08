package yblk.fashion.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import yblk.fashion.Fashion;
import yblk.fashion.init.ModItems;
import yblk.fashion.init.ModRecipes;
import yblk.fashion.item.ClothingFabric;
import yblk.fashion.item.TShirtColor;

/**
 * Documents the sewing pattern for JEI / data packs: product + fabric_count + dye.
 * Crafting itself is handled by {@link yblk.fashion.menu.SewingStationMenu}.
 */
public class SewingRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final String product;
    private final TShirtColor color;
    private final int fabricCount;

    public SewingRecipe(ResourceLocation id, String product, TShirtColor color, int fabricCount) {
        this.id = id;
        this.product = product;
        this.color = color;
        this.fabricCount = fabricCount;
    }

    public String getProduct() {
        return product;
    }

    public TShirtColor getColor() {
        return color;
    }

    public int getFabricCount() {
        return fabricCount;
    }

    @Override
    public boolean matches(Container container, Level level) {
        int fabric = 0;
        boolean hasDye = false;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (ClothingFabric.isSewingFabric(stack)) {
                fabric += stack.getCount();
            } else if (stack.getItem() == color.dyeItem()) {
                hasDye = true;
            } else {
                return false;
            }
        }
        return fabric >= fabricCount && hasDye;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess access) {
        return resultStack();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return resultStack();
    }

    private ItemStack resultStack() {
        return switch (product) {
            case "pants" -> new ItemStack(ModItems.PANTS.get(color).get());
            case "shorts" -> new ItemStack(ModItems.SHORTS.get(color).get());
            case "hoodie" -> new ItemStack(ModItems.HOODIES.get(color).get());
            case "jacket" -> new ItemStack(ModItems.JACKETS.get(color).get());
            case "tank_top" -> color.hasTankTop()
                    ? new ItemStack(ModItems.TANK_TOPS.get(color).get())
                    : new ItemStack(ModItems.TANK_TOPS.get(TShirtColor.WHITE).get());
            case "vest" -> color.hasVest()
                    ? new ItemStack(ModItems.VESTS.get(color).get())
                    : new ItemStack(ModItems.VESTS.get(TShirtColor.BLACK).get());
            case "helmet" -> new ItemStack(ModItems.HELMET.get());
            case "coat" -> new ItemStack(ModItems.COATS.get(color).get());
            case "sneakers" -> new ItemStack(ModItems.SNEAKERS.get(color).get());
            case "shoes" -> new ItemStack(ModItems.SHOES.get(color).get());
            case "hat" -> new ItemStack(ModItems.HATS.get(color).get());
            case "cowboy_hat" -> new ItemStack(ModItems.COWBOY_HATS.get(color).get());
            case "straw_hat" -> new ItemStack(ModItems.STRAW_HAT.get());
            case "cap" -> new ItemStack(ModItems.CAPS.get(color).get());
            case "bandana" -> color.hasBandana()
                    ? new ItemStack(ModItems.BANDANAS.get(color).get())
                    : new ItemStack(ModItems.blackBandana());
            case "balaclava" -> color.hasBalaclava()
                    ? new ItemStack(ModItems.BALACLAVAS.get(color).get())
                    : new ItemStack(ModItems.blackBalaclava());
            case "nvg" -> new ItemStack(ModItems.NVG.get());
            case "clutch" -> color.hasClutch()
                    ? new ItemStack(ModItems.CLUTCHES.get(color).get())
                    : new ItemStack(ModItems.CLUTCHES.get(TShirtColor.BLACK).get());
            case "sports_bag" -> color.hasSportsBag()
                    ? new ItemStack(ModItems.SPORTS_BAGS.get(color).get())
                    : new ItemStack(ModItems.SPORTS_BAGS.get(TShirtColor.RED).get());
            case "backpack" -> color.hasBackpack()
                    ? new ItemStack(ModItems.BACKPACKS.get(color).get())
                    : new ItemStack(ModItems.BACKPACKS.get(TShirtColor.RED).get());
            default -> new ItemStack(ModItems.TSHIRTS.get(color).get());
        };
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SEWING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.SEWING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<SewingRecipe> {
        @Override
        public SewingRecipe fromJson(ResourceLocation id, JsonObject json) {
            String product = GsonHelper.getAsString(json, "product", "tshirt");
            String colorId = GsonHelper.getAsString(json, "color", "white");
            TShirtColor color = null;
            for (TShirtColor c : TShirtColor.values()) {
                if (c.id.equals(colorId)) {
                    color = c;
                    break;
                }
            }
            if (color == null) {
                throw new IllegalArgumentException("Unknown sewing color: " + colorId);
            }
            int fabric = GsonHelper.getAsInt(json, "fabric_count", switch (product) {
                case "pants" -> 4;
                case "shorts" -> 2;
                case "hoodie" -> 5;
                case "jacket" -> 8;
                case "coat", "sports_bag" -> 6;
                case "backpack" -> 8;
                case "sneakers", "cap" -> 1;
                case "shoes", "cowboy_hat", "straw_hat" -> 0;
                case "hat", "clutch" -> 4;
                case "bandana" -> 2;
                case "balaclava" -> 3;
                case "nvg", "helmet", "vest" -> 1;
                default -> 3;
            });
            if ("shoes".equals(product) && !color.hasShoes()) {
                throw new IllegalArgumentException("Shoes only support black/brown/orange, got: " + colorId);
            }
            if ("cowboy_hat".equals(product) && !color.hasCowboyHat()) {
                throw new IllegalArgumentException("Cowboy hat only support black/brown/orange, got: " + colorId);
            }
            if ("coat".equals(product) && !color.hasCoat()) {
                throw new IllegalArgumentException("Coat only support black/brown, got: " + colorId);
            }
            if ("clutch".equals(product) && !color.hasClutch()) {
                throw new IllegalArgumentException("Clutch only support black/blue/orange/yellow, got: " + colorId);
            }
            if ("sports_bag".equals(product) && !color.hasSportsBag()) {
                throw new IllegalArgumentException("Sports bag only support purple/red/gray/green, got: " + colorId);
            }
            if ("backpack".equals(product) && !color.hasBackpack()) {
                throw new IllegalArgumentException("Backpack only support black/red/green, got: " + colorId);
            }
            return new SewingRecipe(id, product, color, fabric);
        }

        @Override
        public SewingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String product = buf.readUtf();
            TShirtColor color = buf.readEnum(TShirtColor.class);
            int fabric = buf.readVarInt();
            return new SewingRecipe(id, product, color, fabric);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SewingRecipe recipe) {
            buf.writeUtf(recipe.product);
            buf.writeEnum(recipe.color);
            buf.writeVarInt(recipe.fabricCount);
        }
    }

    public static ResourceLocation typeId() {
        return new ResourceLocation(Fashion.MODID, "sewing");
    }
}
