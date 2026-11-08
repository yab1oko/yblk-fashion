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
 * Helmet modification item — Shift+RMB on worn hat/cap, then place in the mod slot.
 * Not a Curios accessory.
 */
public class CigaretteItem extends Item {
    public static final int ANIM_FRAMES = 8;

    public CigaretteItem() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tip, TooltipFlag flag) {
        tip.add(Component.translatable("item.fashion.mod.cigarette")
                .withStyle(Style.EMPTY.withColor(0x1A63F7)));
        super.appendHoverText(stack, level, tip, flag);
    }

    public static ResourceLocation modelTexture(int frame) {
        int f = Math.floorMod(frame, ANIM_FRAMES);
        return new ResourceLocation(Fashion.MODID, "textures/models/cigarette_" + f + ".png");
    }

    public static ResourceLocation emissiveTexture(int frame) {
        int f = Math.floorMod(frame, ANIM_FRAMES);
        return new ResourceLocation(Fashion.MODID, "textures/models/cigarette_e_" + f + ".png");
    }

    public static boolean isCigarette(ItemStack stack) {
        return stack.getItem() instanceof CigaretteItem;
    }
}
