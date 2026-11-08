package yblk.fashion.init;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import yblk.fashion.Fashion;
import yblk.fashion.recipe.ClothingRecolorRecipe;
import yblk.fashion.recipe.ClothingUncraftRecipe;
import yblk.fashion.recipe.SewingRecipe;

public final class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Fashion.MODID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Fashion.MODID);

    public static final RegistryObject<RecipeType<SewingRecipe>> SEWING_TYPE =
            RECIPE_TYPES.register("sewing", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return Fashion.MODID + ":sewing";
                }
            });

    public static final RegistryObject<RecipeSerializer<SewingRecipe>> SEWING_SERIALIZER =
            RECIPE_SERIALIZERS.register("sewing", SewingRecipe.Serializer::new);

    public static final RegistryObject<RecipeSerializer<ClothingRecolorRecipe>> CLOTHING_RECOLOR_SERIALIZER =
            RECIPE_SERIALIZERS.register("clothing_recolor",
                    () -> new SimpleCraftingRecipeSerializer<>(ClothingRecolorRecipe::new));

    public static final RegistryObject<RecipeSerializer<ClothingUncraftRecipe>> CLOTHING_UNCRAFT_SERIALIZER =
            RECIPE_SERIALIZERS.register("clothing_uncraft",
                    () -> new SimpleCraftingRecipeSerializer<>(ClothingUncraftRecipe::new));

    private ModRecipes() {
    }
}
