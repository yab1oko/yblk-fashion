package yblk.fashion.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Chest / legs clothing mod — +1 armor, no mesh. */
public class ArmorPlateItem extends Item {
    public ArmorPlateItem() {
        super(new Properties().stacksTo(16));
    }

    public static boolean isArmorPlate(ItemStack stack) {
        return stack.getItem() instanceof ArmorPlateItem;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tip, TooltipFlag flag) {
        tip.add(Component.translatable("item.fashion.mod.plate")
                .withStyle(Style.EMPTY.withColor(0x1A63F7)));
        super.appendHoverText(stack, level, tip, flag);
    }
}
