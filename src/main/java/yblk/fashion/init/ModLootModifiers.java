package yblk.fashion.init;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import yblk.fashion.Fashion;
import yblk.fashion.loot.ClothingModSlotLootModifier;

public final class ModLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Fashion.MODID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> CLOTHING_MOD_SLOTS =
            SERIALIZERS.register("clothing_mod_slots", () -> ClothingModSlotLootModifier.CODEC);

    private ModLootModifiers() {
    }
}
