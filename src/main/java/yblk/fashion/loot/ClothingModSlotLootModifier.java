package yblk.fashion.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;
import yblk.fashion.item.ClothingMods;

/** Rolls 1–2 fashion mod slots onto clothing found in any loot table. */
public class ClothingModSlotLootModifier extends LootModifier {
    public static final Codec<ClothingModSlotLootModifier> CODEC = RecordCodecBuilder.create(inst ->
            codecStart(inst).apply(inst, ClothingModSlotLootModifier::new));

    public ClothingModSlotLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(
            ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for (ItemStack stack : generatedLoot) {
            ClothingMods.maybeRollSlots(stack, context.getRandom());
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
