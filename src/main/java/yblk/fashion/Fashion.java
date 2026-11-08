package yblk.fashion;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import yblk.fashion.compat.FeaturesCompat;
import yblk.fashion.init.ModBlockEntities;
import yblk.fashion.init.ModBlocks;
import yblk.fashion.init.ModCreativeTabs;
import yblk.fashion.init.ModItems;
import yblk.fashion.init.ModLootModifiers;
import yblk.fashion.init.ModMenus;
import yblk.fashion.init.ModRecipes;
import yblk.fashion.network.FashionNetwork;

@Mod(Fashion.MODID)
public class Fashion {
    public static final String MODID = "fashion";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Fashion() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModRecipes.RECIPE_TYPES.register(modBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modBus);
        ModLootModifiers.SERIALIZERS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        modBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            FeaturesCompat.bootstrap();
            FashionNetwork.register();
        });
        LOGGER.info("YBLK Fashion ready (Features inventory compat: {})", FeaturesCompat.isFeaturesPresent());
    }
}
