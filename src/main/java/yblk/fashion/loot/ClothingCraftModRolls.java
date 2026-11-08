package yblk.fashion.loot;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yblk.fashion.Fashion;
import yblk.fashion.item.ClothingMods;

/** Crafting-table clothing also rolls mod slot count. */
@Mod.EventBusSubscriber(modid = Fashion.MODID)
public final class ClothingCraftModRolls {
    private ClothingCraftModRolls() {
    }

    @SubscribeEvent
    public static void onCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }
        ClothingMods.maybeRollSlots(event.getCrafting(), event.getEntity().getRandom());
    }
}
