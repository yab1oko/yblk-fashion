package yblk.fashion.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import yblk.fashion.Fashion;

public final class ModTags {
    public static final TagKey<Item> SEWING_FABRIC =
            ItemTags.create(new ResourceLocation(Fashion.MODID, "sewing_fabric"));

    /** All wearable fashion items (armor + Curios). */
    public static final TagKey<Item> CLOTHING =
            ItemTags.create(new ResourceLocation(Fashion.MODID, "clothing"));
    public static final TagKey<Item> CLOTHING_HEAD =
            ItemTags.create(new ResourceLocation(Fashion.MODID, "clothing/head"));
    public static final TagKey<Item> CLOTHING_CHEST =
            ItemTags.create(new ResourceLocation(Fashion.MODID, "clothing/chest"));
    public static final TagKey<Item> CLOTHING_LEGS =
            ItemTags.create(new ResourceLocation(Fashion.MODID, "clothing/legs"));
    public static final TagKey<Item> CLOTHING_FEET =
            ItemTags.create(new ResourceLocation(Fashion.MODID, "clothing/feet"));
    public static final TagKey<Item> CLOTHING_OUTERWEAR =
            ItemTags.create(new ResourceLocation(Fashion.MODID, "clothing/outerwear"));
    public static final TagKey<Item> CLOTHING_VEST =
            ItemTags.create(new ResourceLocation(Fashion.MODID, "clothing/vest"));
    public static final TagKey<Item> CLOTHING_MASK =
            ItemTags.create(new ResourceLocation(Fashion.MODID, "clothing/mask"));
    public static final TagKey<Item> CLOTHING_BODY =
            ItemTags.create(new ResourceLocation(Fashion.MODID, "clothing/body"));

    /** Farmer's Delight / common Forge knife tools. */
    public static final TagKey<Item> KNIVES =
            ItemTags.create(new ResourceLocation("forge", "tools/knives"));

    /** Older / alternate knife tag used by some packs. */
    public static final TagKey<Item> KNIVES_ALT =
            ItemTags.create(new ResourceLocation("forge", "knives"));

    /** Typo-tolerant tag some packs use. */
    public static final TagKey<Item> KNIFES =
            ItemTags.create(new ResourceLocation("forge", "knifes"));

    private ModTags() {
    }

    public static boolean isKnife(ItemStack stack) {
        return stack.is(Items.SHEARS)
                || stack.is(KNIVES)
                || stack.is(KNIVES_ALT)
                || stack.is(KNIFES);
    }
}
