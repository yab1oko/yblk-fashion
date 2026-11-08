package yblk.fashion.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import yblk.fashion.Fashion;
import yblk.fashion.item.TShirtColor;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Fashion.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                    .title(Component.translatable("itemGroup." + Fashion.MODID))
                    .icon(() -> new ItemStack(ModItems.redTshirt()))
                    .displayItems((params, output) -> {
                        // Fallback / search index. Folder UI rebuilds the visible layout.
                        output.accept(ModItems.SEWING_STATION.get());
                        for (TShirtColor color : TShirtColor.values()) {
                            output.accept(ModItems.TSHIRTS.get(color).get());
                            if (color.hasTankTop()) {
                                output.accept(ModItems.TANK_TOPS.get(color).get());
                            }
                            output.accept(ModItems.PANTS.get(color).get());
                            output.accept(ModItems.SHORTS.get(color).get());
                            output.accept(ModItems.HOODIES.get(color).get());
                            output.accept(ModItems.JACKETS.get(color).get());
                            output.accept(ModItems.SNEAKERS.get(color).get());
                            output.accept(ModItems.HATS.get(color).get());
                            output.accept(ModItems.CAPS.get(color).get());
                        }
                        for (TShirtColor color : TShirtColor.values()) {
                            if (color.hasShoes()) {
                                output.accept(ModItems.SHOES.get(color).get());
                            }
                        }
                        for (TShirtColor color : TShirtColor.values()) {
                            if (color.hasCowboyHat()) {
                                output.accept(ModItems.COWBOY_HATS.get(color).get());
                            }
                        }
                        output.accept(ModItems.STRAW_HAT.get());
                        output.accept(ModItems.HELMET.get());
                        output.accept(ModItems.SCRAP_HELMET.get());
                        output.accept(ModItems.SCRAP_CHESTPLATE.get());
                        output.accept(ModItems.SCRAP_PANTS.get());
                        for (TShirtColor color : TShirtColor.values()) {
                            if (color.hasCoat()) {
                                output.accept(ModItems.COATS.get(color).get());
                            }
                        }
                        for (TShirtColor color : TShirtColor.values()) {
                            if (color.hasVest()) {
                                output.accept(ModItems.VESTS.get(color).get());
                            }
                        }
                        for (TShirtColor color : TShirtColor.values()) {
                            if (color.hasBandana()) {
                                output.accept(ModItems.BANDANAS.get(color).get());
                            }
                        }
                        for (TShirtColor color : TShirtColor.values()) {
                            if (color.hasBalaclava()) {
                                output.accept(ModItems.BALACLAVAS.get(color).get());
                            }
                        }
                        output.accept(ModItems.NVG.get());
                        output.accept(ModItems.GAS_MASK.get());
                        output.accept(ModItems.GLASSES.get());
                        output.accept(ModItems.BLACK_GLASSES.get());
                        for (TShirtColor color : TShirtColor.values()) {
                            if (color.hasClutch()) {
                                output.accept(ModItems.CLUTCHES.get(color).get());
                            }
                            if (color.hasSportsBag()) {
                                output.accept(ModItems.SPORTS_BAGS.get(color).get());
                            }
                            if (color.hasBackpack()) {
                                output.accept(ModItems.BACKPACKS.get(color).get());
                            }
                        }
                        output.accept(ModItems.CIGARETTE.get());
                        output.accept(ModItems.POUCH.get());
                        output.accept(ModItems.ARMOR_PLATE.get());
                    })
                    .build());

    private ModCreativeTabs() {
    }
}
