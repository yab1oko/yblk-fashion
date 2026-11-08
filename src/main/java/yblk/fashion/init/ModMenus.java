package yblk.fashion.init;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import yblk.fashion.Fashion;
import yblk.fashion.menu.SewingStationMenu;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Fashion.MODID);

    public static final RegistryObject<MenuType<SewingStationMenu>> SEWING_STATION =
            MENUS.register("sewing_station", () -> IForgeMenuType.create(SewingStationMenu::new));

    private ModMenus() {
    }
}
