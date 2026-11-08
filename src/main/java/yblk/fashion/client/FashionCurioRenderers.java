package yblk.fashion.client;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import yblk.fashion.item.BackpackItem;
import yblk.fashion.item.BalaclavaItem;
import yblk.fashion.item.BandanaItem;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClutchItem;
import yblk.fashion.item.GasMaskItem;
import yblk.fashion.item.GlassesItem;
import yblk.fashion.item.NvgItem;
import yblk.fashion.item.SportsBagItem;

/** Registers {@link FashionCurioRenderer} for every fashion Curios item. */
public final class FashionCurioRenderers {
    private FashionCurioRenderers() {
    }

    public static void registerAll() {
        for (Item item : ForgeRegistries.ITEMS.getValues()) {
            if (item instanceof ClothingItem clothing && clothing.getClothingType().isCurioWear()) {
                CuriosRendererRegistry.register(item, () -> FashionCurioRenderer.INSTANCE);
            } else if (item instanceof BandanaItem
                    || item instanceof BalaclavaItem
                    || item instanceof NvgItem
                    || item instanceof GasMaskItem
                    || item instanceof GlassesItem
                    || item instanceof SportsBagItem
                    || item instanceof BackpackItem
                    || item instanceof ClutchItem) {
                CuriosRendererRegistry.register(item, () -> FashionCurioRenderer.INSTANCE);
            }
        }
    }
}
