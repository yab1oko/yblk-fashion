package yblk.fashion.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import yblk.fashion.Fashion;

import java.util.List;

/**
 * Thigh pouch modification — one on vanilla CHEST, one on LEGS (not Curios).
 * Both worn → {@code pouch_double} mesh; otherwise {@code pouch_single}.
 * Adds +1 Features inventory slot while installed.
 */
public class PouchItem extends Item {
    public static final int FEATURES_SLOTS = 1;

    private static final ResourceLocation MODEL_TEXTURE =
            new ResourceLocation(Fashion.MODID, "textures/models/pouch.png");

    public PouchItem() {
        super(new Properties().stacksTo(16));
    }

    public static ResourceLocation modelTexture() {
        return MODEL_TEXTURE;
    }

    public static boolean isPouch(ItemStack stack) {
        return stack.getItem() instanceof PouchItem;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tip, TooltipFlag flag) {
        tip.add(Component.translatable("item.fashion.mod.pouch")
                .withStyle(Style.EMPTY.withColor(0x1A63F7)));
        super.appendHoverText(stack, level, tip, flag);
    }
}
