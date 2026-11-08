package yblk.fashion.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import yblk.fashion.Fashion;
import yblk.fashion.compat.CuriosClothing;
import yblk.fashion.network.FashionNetwork;
import yblk.fashion.network.ToggleHoodPacket;
import yblk.fashion.network.ToggleNvgPacket;
import yblk.fashion.client.model.BalaclavaModel;
import yblk.fashion.client.model.BandanaModel;
import yblk.fashion.client.model.GasMaskModel;
import yblk.fashion.client.model.GlassesModel;
import yblk.fashion.client.model.NvgModel;
import yblk.fashion.client.model.PouchModel;
import yblk.fashion.client.model.CigaretteModel;
import yblk.fashion.client.model.BackpackModel;
import yblk.fashion.client.model.ClutchModel;
import yblk.fashion.client.model.SportsBagModel;
import yblk.fashion.client.model.GhillieBootsModel;
import yblk.fashion.client.model.GhillieHoodModel;
import yblk.fashion.client.model.GhilliePantsModel;
import yblk.fashion.client.model.GhillieTopModel;
import yblk.fashion.client.model.CalfPantsModel;
import yblk.fashion.client.model.CalfShortsModel;
import yblk.fashion.client.model.ForearmSleeveModel;
import yblk.fashion.client.model.CapModel;
import yblk.fashion.client.model.CoatLegModel;
import yblk.fashion.client.model.CoatModel;
import yblk.fashion.client.model.CoatPlacketModel;
import yblk.fashion.client.model.CoatWristModel;
import yblk.fashion.client.model.CowboyHatModel;
import yblk.fashion.client.model.HatModel;
import yblk.fashion.client.model.HatOuterModel;
import yblk.fashion.client.model.HelmetModel;
import yblk.fashion.client.model.ScrapChestModel;
import yblk.fashion.client.model.ScrapHelmetModel;
import yblk.fashion.client.model.ScrapPantsModel;
import yblk.fashion.client.model.StrawHatModel;
import yblk.fashion.client.model.HoodFrontModel;
import yblk.fashion.client.model.HoodieModel;
import yblk.fashion.client.model.HoodieTearModel;
import yblk.fashion.client.model.JacketModel;
import yblk.fashion.client.model.JacketTearModel;
import yblk.fashion.client.model.PantsModel;
import yblk.fashion.client.model.ShortsModel;
import yblk.fashion.client.model.ShortsTearModel;
import yblk.fashion.client.model.PantsTearModel;
import yblk.fashion.client.model.ShinOuterModel;
import yblk.fashion.client.model.ShoesModel;
import yblk.fashion.client.model.SneakersModel;
import yblk.fashion.client.model.TankTopModel;
import yblk.fashion.client.model.TShirtModel;
import yblk.fashion.client.model.VestModel;
import yblk.fashion.client.model.TShirtTearModel;
import yblk.fashion.client.screen.SewingStationScreen;
import yblk.fashion.init.ModBlocks;
import yblk.fashion.init.ModMenus;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClothingType;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = Fashion.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {
    private ClientSetup() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        // Single registration site — nested Register classes double-fired hood and missed NVG.
        event.register(HoodieKeybinds.TOGGLE_HOOD);
        event.register(NvgKeybinds.TOGGLE_NVG);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Nested @EventBusSubscriber Handlers are not reliably discovered — register ticks here.
        MinecraftForge.EVENT_BUS.addListener(ClientSetup::onClientTick);
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenus.SEWING_STATION.get(), SewingStationScreen::new);
            // sewing_station_3 has alpha (fabric / scissors); solid layer paints those pixels black.
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.SEWING_STATION.get(), RenderType.cutout());
            // NPC Studio Curios layer looks up CuriosRendererRegistry — register fashion meshes.
            FashionCurioRenderers.registerAll();
            // After every mod's RegisterRenderers — catch NPC / custom humanoids AddLayers missed.
            attachMobClothingToDispatcher();
        });
    }

    /**
     * Armor pass draws fashion meshes on non-players (NPC Studio, zombies, …).
     * Players keep suppress + {@link ClothingRenderLayer} so layers don't double.
     */
    private static boolean suppressArmorPass(LivingEntity entity) {
        return entity instanceof Player;
    }

    /** Walk the live renderer map so custom NPC mods always get {@link MobClothingLayer}. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void attachMobClothingToDispatcher() {
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        if (dispatcher == null || dispatcher.renderers == null) {
            return;
        }
        for (EntityRenderer<?> renderer : dispatcher.renderers.values()) {
            if (!(renderer instanceof LivingEntityRenderer<?, ?> living)) {
                continue;
            }
            EntityModel<?> model = living.getModel();
            if (!(model instanceof HumanoidModel)) {
                continue;
            }
            if (hasMobClothingLayer(living)) {
                continue;
            }
            living.addLayer(new MobClothingLayer(living));
        }
    }

    /** Hood (G) + NVG (N) consumeClick → server packets. */
    private static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) {
            return;
        }
        while (HoodieKeybinds.TOGGLE_HOOD.consumeClick()) {
            ItemStack hooded = CuriosClothing.hoodedPiece(mc.player);
            if (hooded.getItem() instanceof ClothingItem clothing
                    && clothing.getClothingType().hasHood()) {
                FashionNetwork.CHANNEL.sendToServer(new ToggleHoodPacket());
            }
        }
        while (NvgKeybinds.TOGGLE_NVG.consumeClick()) {
            // Server validates worn NVG / durability.
            FashionNetwork.CHANNEL.sendToServer(new ToggleNvgPacket());
        }
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(TShirtModel.LAYER_WIDE, TShirtModel::createWide);
        event.registerLayerDefinition(TShirtModel.LAYER_SLIM, TShirtModel::createSlim);
        event.registerLayerDefinition(TankTopModel.LAYER, TankTopModel::createBodyLayer);
        event.registerLayerDefinition(VestModel.LAYER, VestModel::createBodyLayer);
        event.registerLayerDefinition(TShirtTearModel.LAYER_WIDE, TShirtTearModel::createWide);
        event.registerLayerDefinition(TShirtTearModel.LAYER_SLIM, TShirtTearModel::createSlim);
        event.registerLayerDefinition(ForearmSleeveModel.LAYER_WIDE, ForearmSleeveModel::createWide);
        event.registerLayerDefinition(ForearmSleeveModel.LAYER_SLIM, ForearmSleeveModel::createSlim);

        event.registerLayerDefinition(HoodieModel.LAYER_CLOSED_WIDE, HoodieModel::createClosedWide);
        event.registerLayerDefinition(HoodieModel.LAYER_CLOSED_SLIM, HoodieModel::createClosedSlim);
        event.registerLayerDefinition(HoodieModel.LAYER_OPEN_WIDE, HoodieModel::createOpenWide);
        event.registerLayerDefinition(HoodieModel.LAYER_OPEN_SLIM, HoodieModel::createOpenSlim);
        event.registerLayerDefinition(HoodieModel.LAYER_CLOSED_WIDE_HAT, HoodieModel::createClosedWideHat);
        event.registerLayerDefinition(HoodieModel.LAYER_CLOSED_SLIM_HAT, HoodieModel::createClosedSlimHat);
        event.registerLayerDefinition(HoodieModel.LAYER_OPEN_WIDE_HAT, HoodieModel::createOpenWideHat);
        event.registerLayerDefinition(HoodieModel.LAYER_OPEN_SLIM_HAT, HoodieModel::createOpenSlimHat);
        event.registerLayerDefinition(HoodieTearModel.LAYER_CLOSED_WIDE, HoodieTearModel::createClosedWide);
        event.registerLayerDefinition(HoodieTearModel.LAYER_CLOSED_SLIM, HoodieTearModel::createClosedSlim);
        event.registerLayerDefinition(HoodieTearModel.LAYER_OPEN_WIDE, HoodieTearModel::createOpenWide);
        event.registerLayerDefinition(HoodieTearModel.LAYER_OPEN_SLIM, HoodieTearModel::createOpenSlim);

        event.registerLayerDefinition(JacketModel.LAYER_CLOSED_WIDE, JacketModel::createClosedWide);
        event.registerLayerDefinition(JacketModel.LAYER_CLOSED_SLIM, JacketModel::createClosedSlim);
        event.registerLayerDefinition(JacketModel.LAYER_OPEN_WIDE, JacketModel::createOpenWide);
        event.registerLayerDefinition(JacketModel.LAYER_OPEN_SLIM, JacketModel::createOpenSlim);
        event.registerLayerDefinition(JacketModel.LAYER_CLOSED_WIDE_HAT, JacketModel::createClosedWideHat);
        event.registerLayerDefinition(JacketModel.LAYER_CLOSED_SLIM_HAT, JacketModel::createClosedSlimHat);
        event.registerLayerDefinition(JacketModel.LAYER_OPEN_WIDE_HAT, JacketModel::createOpenWideHat);
        event.registerLayerDefinition(JacketModel.LAYER_OPEN_SLIM_HAT, JacketModel::createOpenSlimHat);
        event.registerLayerDefinition(JacketTearModel.LAYER_CLOSED_WIDE, JacketTearModel::createClosedWide);
        event.registerLayerDefinition(JacketTearModel.LAYER_CLOSED_SLIM, JacketTearModel::createClosedSlim);
        event.registerLayerDefinition(JacketTearModel.LAYER_OPEN_WIDE, JacketTearModel::createOpenWide);
        event.registerLayerDefinition(JacketTearModel.LAYER_OPEN_SLIM, JacketTearModel::createOpenSlim);

        event.registerLayerDefinition(CoatModel.LAYER_WIDE, CoatModel::createWide);
        event.registerLayerDefinition(CoatModel.LAYER_SLIM, CoatModel::createSlim);
        event.registerLayerDefinition(CoatWristModel.LAYER_WIDE, CoatWristModel::createWide);
        event.registerLayerDefinition(CoatWristModel.LAYER_SLIM, CoatWristModel::createSlim);
        event.registerLayerDefinition(CoatPlacketModel.LAYER, CoatPlacketModel::createBodyLayer);
        event.registerLayerDefinition(CoatLegModel.LAYER, CoatLegModel::createBodyLayer);
        event.registerLayerDefinition(CoatLegModel.LAYER_TO_SHOE, CoatLegModel::createToShoe);
        event.registerLayerDefinition(CoatLegModel.LAYER_TO_BOOTS, CoatLegModel::createToBoots);

        event.registerLayerDefinition(HoodFrontModel.LAYER, HoodFrontModel::create);
        event.registerLayerDefinition(PantsModel.LAYER, PantsModel::createBodyLayer);
        event.registerLayerDefinition(PantsTearModel.LAYER, PantsTearModel::createBodyLayer);
        event.registerLayerDefinition(CalfPantsModel.LAYER, CalfPantsModel::createBodyLayer);
        event.registerLayerDefinition(CalfPantsModel.LAYER_TO_SHOE, CalfPantsModel::createToShoe);
        event.registerLayerDefinition(CalfPantsModel.LAYER_TO_BOOTS, CalfPantsModel::createToBoots);
        event.registerLayerDefinition(ShortsModel.LAYER, ShortsModel::createBodyLayer);
        event.registerLayerDefinition(ShortsTearModel.LAYER, ShortsTearModel::createBodyLayer);
        event.registerLayerDefinition(CalfShortsModel.LAYER, CalfShortsModel::createBodyLayer);
        event.registerLayerDefinition(CalfShortsModel.LAYER_TO_SHOE, CalfShortsModel::createToShoe);
        event.registerLayerDefinition(CalfShortsModel.LAYER_TO_BOOTS, CalfShortsModel::createToBoots);
        event.registerLayerDefinition(SneakersModel.LAYER, SneakersModel::createBodyLayer);
        event.registerLayerDefinition(ShoesModel.LAYER, ShoesModel::createBodyLayer);
        event.registerLayerDefinition(ShinOuterModel.LAYER, ShinOuterModel::createBodyLayer);
        event.registerLayerDefinition(ShinOuterModel.LAYER_SHOES, ShinOuterModel::createForShoes);
        event.registerLayerDefinition(HatModel.LAYER, HatModel::createBodyLayer);
        event.registerLayerDefinition(CowboyHatModel.LAYER, CowboyHatModel::createBodyLayer);
        event.registerLayerDefinition(StrawHatModel.LAYER, StrawHatModel::createBodyLayer);
        event.registerLayerDefinition(HatOuterModel.LAYER, HatOuterModel::create);
        event.registerLayerDefinition(HatOuterModel.LAYER_COWBOY, HatOuterModel::createForCowboy);
        event.registerLayerDefinition(HatOuterModel.LAYER_STRAW, HatOuterModel::createForStraw);
        event.registerLayerDefinition(CapModel.LAYER, CapModel::createBodyLayer);
        event.registerLayerDefinition(HelmetModel.LAYER, HelmetModel::createBodyLayer);
        event.registerLayerDefinition(ScrapHelmetModel.LAYER, ScrapHelmetModel::createBodyLayer);
        event.registerLayerDefinition(ScrapChestModel.LAYER, ScrapChestModel::createBodyLayer);
        event.registerLayerDefinition(ScrapPantsModel.LAYER, ScrapPantsModel::createBodyLayer);
        event.registerLayerDefinition(BandanaModel.LAYER, BandanaModel::createBodyLayer);
        event.registerLayerDefinition(BalaclavaModel.LAYER, BalaclavaModel::createBodyLayer);
        event.registerLayerDefinition(BalaclavaModel.LAYER_UNDER_HAT, BalaclavaModel::createUnderHatLayer);
        event.registerLayerDefinition(NvgModel.LAYER, NvgModel::createBodyLayer);
        event.registerLayerDefinition(GasMaskModel.LAYER_ZERO, GasMaskModel::createZero);
        event.registerLayerDefinition(GasMaskModel.LAYER_ONE, GasMaskModel::createOne);
        event.registerLayerDefinition(GasMaskModel.LAYER_TWO, GasMaskModel::createTwo);
        event.registerLayerDefinition(GlassesModel.LAYER, GlassesModel::createBodyLayer);
        event.registerLayerDefinition(ClutchModel.LAYER, ClutchModel::createBodyLayer);
        event.registerLayerDefinition(SportsBagModel.LAYER, SportsBagModel::createBodyLayer);
        event.registerLayerDefinition(BackpackModel.LAYER, BackpackModel::createBodyLayer);
        event.registerLayerDefinition(CigaretteModel.LAYER, CigaretteModel::createBodyLayer);
        event.registerLayerDefinition(PouchModel.LAYER_SINGLE, PouchModel::createSingle);
        event.registerLayerDefinition(PouchModel.LAYER_DOUBLE, PouchModel::createDouble);
        event.registerLayerDefinition(GhillieHoodModel.LAYER, GhillieHoodModel::createBodyLayer);
        event.registerLayerDefinition(GhillieTopModel.LAYER_WIDE, GhillieTopModel::createWide);
        event.registerLayerDefinition(GhillieTopModel.LAYER_SLIM, GhillieTopModel::createSlim);
        event.registerLayerDefinition(GhilliePantsModel.LAYER, GhilliePantsModel::createBodyLayer);
        event.registerLayerDefinition(GhillieBootsModel.LAYER, GhillieBootsModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void addPlayerLayers(EntityRenderersEvent.AddLayers event) {
        for (String skin : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(skin);
            if (renderer != null) {
                // Must run after setupAnim and before CapeLayer.
                insertLayerFirst(renderer, new CapePoseFixLayer(renderer));
                // Mask under hats — draw before ClothingRenderLayer so cowboy/hat cover balaclava.
                renderer.addLayer(new BalaclavaLayer(renderer));
                renderer.addLayer(new BandanaLayer(renderer));
                renderer.addLayer(new GasMaskLayer(renderer));
                renderer.addLayer(new GlassesLayer(renderer));
                renderer.addLayer(new ClothingRenderLayer(renderer));
                // After helmet so NVG lenses sit on the shell.
                renderer.addLayer(new NvgLayer(renderer));
                renderer.addLayer(new ForearmSleeveLayer(renderer));
                renderer.addLayer(new CoatWristLayer(renderer));
                renderer.addLayer(new CoatPlacketLayer(renderer));
                renderer.addLayer(new CoatLegLayer(renderer));
                renderer.addLayer(new CalfPantsLayer(renderer));
                renderer.addLayer(new CalfShortsLayer(renderer));
                renderer.addLayer(new ShinOuterLayer(renderer));
                renderer.addLayer(new HoodFrontLayer(renderer));
                renderer.addLayer(new HatOuterLayer(renderer));
                renderer.addLayer(new ClothingTearLayer(renderer));
                // After clothing so inflated strap sits on top of t-shirt / jacket / coat.
                renderer.addLayer(new ClutchLayer(renderer));
                renderer.addLayer(new SportsBagLayer(renderer));
                renderer.addLayer(new BackpackLayer(renderer));
                renderer.addLayer(new CigaretteLayer(renderer));
                renderer.addLayer(new PouchLayer(renderer));
            }
        }
        // Any humanoid LivingEntity renderer (vanilla + NPC mods), not only a whitelist.
        addMobClothingToAllHumanoids(event);
    }

    /**
     * Attach {@link MobClothingLayer} to every humanoid living renderer so fashion
     * armor/Curios clothing shows on custom NPCs, not only vanilla undead/piglin.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addMobClothingToAllHumanoids(EntityRenderersEvent.AddLayers event) {
        for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES.getValues()) {
            if (type == EntityType.PLAYER) {
                continue;
            }
            Class<?> base = type.getBaseClass();
            if (base == null || !LivingEntity.class.isAssignableFrom(base)) {
                continue;
            }
            LivingEntityRenderer<?, ?> renderer;
            try {
                renderer = event.getRenderer((EntityType) type);
            } catch (ClassCastException | IllegalArgumentException ignored) {
                continue;
            }
            if (renderer == null) {
                continue;
            }
            EntityModel<?> model = renderer.getModel();
            if (!(model instanceof HumanoidModel)) {
                continue;
            }
            if (hasMobClothingLayer(renderer)) {
                continue;
            }
            renderer.addLayer(new MobClothingLayer(renderer));
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean hasMobClothingLayer(LivingEntityRenderer<?, ?> renderer) {
        try {
            Field field;
            try {
                field = LivingEntityRenderer.class.getDeclaredField("layers");
            } catch (NoSuchFieldException e) {
                field = LivingEntityRenderer.class.getDeclaredField("f_115291_");
            }
            field.setAccessible(true);
            List layers = (List) field.get(renderer);
            for (Object layer : layers) {
                if (layer instanceof MobClothingLayer) {
                    return true;
                }
            }
        } catch (ReflectiveOperationException ignored) {
            // Fall through — duplicate layer is harmless vs missing clothing.
        }
        return false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void insertLayerFirst(PlayerRenderer renderer, RenderLayer<?, ?> layer) {
        try {
            Field field;
            try {
                field = LivingEntityRenderer.class.getDeclaredField("layers");
            } catch (NoSuchFieldException e) {
                field = LivingEntityRenderer.class.getDeclaredField("f_115291_");
            }
            field.setAccessible(true);
            List layers = (List) field.get(renderer);
            layers.add(0, layer);
        } catch (ReflectiveOperationException e) {
            // Fallback: still better than nothing — cape may sit high but clothing renders.
            renderer.addLayer((RenderLayer) layer);
        }
    }

    public static void initClothingClient(ClothingItem item, Consumer<IClientItemExtensions> consumer) {
        if (item.getClothingType() == ClothingType.HOODIE) {
            consumer.accept(hoodieExtensions());
        } else if (item.getClothingType() == ClothingType.JACKET) {
            consumer.accept(jacketExtensions());
        } else if (item.getClothingType() == ClothingType.COAT) {
            consumer.accept(coatExtensions());
        } else if (item.getClothingType() == ClothingType.PANTS) {
            consumer.accept(pantsExtensions());
        } else if (item.getClothingType() == ClothingType.SHORTS) {
            consumer.accept(shortsExtensions());
        } else if (item.getClothingType() == ClothingType.SNEAKERS) {
            consumer.accept(sneakersExtensions());
        } else if (item.getClothingType() == ClothingType.SHOES) {
            consumer.accept(shoesExtensions());
        } else if (item.getClothingType() == ClothingType.HAT) {
            consumer.accept(hatExtensions());
        } else if (item.getClothingType() == ClothingType.COWBOY_HAT) {
            consumer.accept(cowboyHatExtensions());
        } else if (item.getClothingType() == ClothingType.STRAW_HAT) {
            consumer.accept(strawHatExtensions());
        } else if (item.getClothingType() == ClothingType.CAP) {
            consumer.accept(capExtensions());
        } else if (item.getClothingType() == ClothingType.HELMET) {
            consumer.accept(helmetExtensions());
        } else if (item.getClothingType() == ClothingType.SCRAP_HELMET) {
            consumer.accept(scrapHelmetExtensions());
        } else if (item.getClothingType() == ClothingType.SCRAP_VEST) {
            consumer.accept(scrapChestExtensions());
        } else if (item.getClothingType() == ClothingType.SCRAP_PANTS) {
            consumer.accept(scrapPantsExtensions());
        } else if (item.getClothingType() == ClothingType.TANK_TOP) {
            consumer.accept(tankTopExtensions());
        } else if (item.getClothingType() == ClothingType.VEST) {
            consumer.accept(vestExtensions());
        } else if (item.getClothingType().isGhillie()) {
            consumer.accept(ghillieExtensions(item.getClothingType()));
        } else {
            consumer.accept(tshirtExtensions());
        }
    }

    private static IClientItemExtensions tshirtExtensions() {
        return new IClientItemExtensions() {
            private TShirtModel<LivingEntity> wide;
            private TShirtModel<LivingEntity> slim;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                boolean useSlim = entity instanceof AbstractClientPlayer player
                        && "slim".equals(player.getModelName());
                TShirtModel<LivingEntity> model = useSlim ? slimModel() : wideModel();
                // Players: ClothingRenderLayer (armor pass suppressed). NPCs/mobs: armor pass.
                model.setSuppressArmorPass(suppressArmorPass(entity));
                return model;
            }

            private TShirtModel<LivingEntity> wideModel() {
                if (wide == null) {
                    wide = new TShirtModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(TShirtModel.LAYER_WIDE));
                }
                return wide;
            }

            private TShirtModel<LivingEntity> slimModel() {
                if (slim == null) {
                    slim = new TShirtModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(TShirtModel.LAYER_SLIM));
                }
                return slim;
            }
        };
    }

    private static IClientItemExtensions tankTopExtensions() {
        return new IClientItemExtensions() {
            private TankTopModel<LivingEntity> model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                TankTopModel<LivingEntity> tank = tankModel();
                tank.setSuppressArmorPass(suppressArmorPass(entity));
                return tank;
            }

            private TankTopModel<LivingEntity> tankModel() {
                if (model == null) {
                    model = new TankTopModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(TankTopModel.LAYER));
                }
                return model;
            }
        };
    }

    private static IClientItemExtensions vestExtensions() {
        return new IClientItemExtensions() {
            private VestModel<LivingEntity> model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                VestModel<LivingEntity> vest = vestModel();
                vest.setSuppressArmorPass(suppressArmorPass(entity));
                return vest;
            }

            private VestModel<LivingEntity> vestModel() {
                if (model == null) {
                    model = new VestModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(VestModel.LAYER));
                }
                return model;
            }
        };
    }

    private static IClientItemExtensions hoodieExtensions() {
        return hoodedExtensions(true);
    }

    private static IClientItemExtensions jacketExtensions() {
        return hoodedExtensions(false);
    }

    private static IClientItemExtensions coatExtensions() {
        return new IClientItemExtensions() {
            private CoatModel<LivingEntity> wide;
            private CoatModel<LivingEntity> slim;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                boolean useSlim = entity instanceof AbstractClientPlayer player
                        && "slim".equals(player.getModelName());
                CoatModel<LivingEntity> model = useSlim ? slimModel() : wideModel();
                model.setSuppressArmorPass(suppressArmorPass(entity));
                return model;
            }

            private CoatModel<LivingEntity> wideModel() {
                if (wide == null) {
                    wide = new CoatModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(CoatModel.LAYER_WIDE));
                }
                return wide;
            }

            private CoatModel<LivingEntity> slimModel() {
                if (slim == null) {
                    slim = new CoatModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(CoatModel.LAYER_SLIM));
                }
                return slim;
            }
        };
    }

    private static IClientItemExtensions hoodedExtensions(boolean hoodie) {
        return new IClientItemExtensions() {
            private HoodieModel<LivingEntity> hoodieClosedWide;
            private HoodieModel<LivingEntity> hoodieClosedSlim;
            private HoodieModel<LivingEntity> hoodieOpenWide;
            private HoodieModel<LivingEntity> hoodieOpenSlim;
            private JacketModel<LivingEntity> jacketClosedWide;
            private JacketModel<LivingEntity> jacketClosedSlim;
            private JacketModel<LivingEntity> jacketOpenWide;
            private JacketModel<LivingEntity> jacketOpenSlim;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                boolean useSlim = entity instanceof AbstractClientPlayer player
                        && "slim".equals(player.getModelName());
                boolean open = ClothingItem.isHoodOpen(stack);
                HumanoidModel<LivingEntity> model = hoodie
                        ? hoodieModel(useSlim, open)
                        : jacketModel(useSlim, open);

                if (hoodie) {
                    ((HoodieModel<LivingEntity>) model).setSuppressArmorPass(suppressArmorPass(entity));
                } else {
                    ((JacketModel<LivingEntity>) model).setSuppressArmorPass(suppressArmorPass(entity));
                }
                return model;
            }

            private HoodieModel<LivingEntity> hoodieModel(boolean slim, boolean open) {
                if (open) {
                    if (slim) {
                        if (hoodieOpenSlim == null) {
                            hoodieOpenSlim = new HoodieModel<>(
                                    Minecraft.getInstance().getEntityModels().bakeLayer(HoodieModel.LAYER_OPEN_SLIM));
                        }
                        return hoodieOpenSlim;
                    }
                    if (hoodieOpenWide == null) {
                        hoodieOpenWide = new HoodieModel<>(
                                Minecraft.getInstance().getEntityModels().bakeLayer(HoodieModel.LAYER_OPEN_WIDE));
                    }
                    return hoodieOpenWide;
                }
                if (slim) {
                    if (hoodieClosedSlim == null) {
                        hoodieClosedSlim = new HoodieModel<>(
                                Minecraft.getInstance().getEntityModels().bakeLayer(HoodieModel.LAYER_CLOSED_SLIM));
                    }
                    return hoodieClosedSlim;
                }
                if (hoodieClosedWide == null) {
                    hoodieClosedWide = new HoodieModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(HoodieModel.LAYER_CLOSED_WIDE));
                }
                return hoodieClosedWide;
            }

            private JacketModel<LivingEntity> jacketModel(boolean slim, boolean open) {
                if (open) {
                    if (slim) {
                        if (jacketOpenSlim == null) {
                            jacketOpenSlim = new JacketModel<>(
                                    Minecraft.getInstance().getEntityModels().bakeLayer(JacketModel.LAYER_OPEN_SLIM));
                        }
                        return jacketOpenSlim;
                    }
                    if (jacketOpenWide == null) {
                        jacketOpenWide = new JacketModel<>(
                                Minecraft.getInstance().getEntityModels().bakeLayer(JacketModel.LAYER_OPEN_WIDE));
                    }
                    return jacketOpenWide;
                }
                if (slim) {
                    if (jacketClosedSlim == null) {
                        jacketClosedSlim = new JacketModel<>(
                                Minecraft.getInstance().getEntityModels().bakeLayer(JacketModel.LAYER_CLOSED_SLIM));
                    }
                    return jacketClosedSlim;
                }
                if (jacketClosedWide == null) {
                    jacketClosedWide = new JacketModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(JacketModel.LAYER_CLOSED_WIDE));
                }
                return jacketClosedWide;
            }
        };
    }

    private static IClientItemExtensions pantsExtensions() {
        return new IClientItemExtensions() {
            private PantsModel<LivingEntity> model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                PantsModel<LivingEntity> pants = pantsModel();
                pants.setSuppressArmorPass(suppressArmorPass(entity));
                return pants;
            }

            private PantsModel<LivingEntity> pantsModel() {
                if (model == null) {
                    model = new PantsModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(PantsModel.LAYER));
                }
                return model;
            }
        };
    }

    private static IClientItemExtensions shortsExtensions() {
        return new IClientItemExtensions() {
            private ShortsModel<LivingEntity> model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                ShortsModel<LivingEntity> shorts = shortsModel();
                shorts.setSuppressArmorPass(suppressArmorPass(entity));
                return shorts;
            }

            private ShortsModel<LivingEntity> shortsModel() {
                if (model == null) {
                    model = new ShortsModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(ShortsModel.LAYER));
                }
                return model;
            }
        };
    }

    private static IClientItemExtensions sneakersExtensions() {
        return new IClientItemExtensions() {
            private SneakersModel<LivingEntity> model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                SneakersModel<LivingEntity> shoes = sneakersModel();
                shoes.setSuppressArmorPass(suppressArmorPass(entity));
                return shoes;
            }

            private SneakersModel<LivingEntity> sneakersModel() {
                if (model == null) {
                    model = new SneakersModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(SneakersModel.LAYER));
                }
                return model;
            }
        };
    }

    private static IClientItemExtensions shoesExtensions() {
        return new IClientItemExtensions() {
            private ShoesModel<LivingEntity> model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                ShoesModel<LivingEntity> shoes = shoesModel();
                shoes.setSuppressArmorPass(suppressArmorPass(entity));
                return shoes;
            }

            private ShoesModel<LivingEntity> shoesModel() {
                if (model == null) {
                    model = new ShoesModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(ShoesModel.LAYER));
                }
                return model;
            }
        };
    }

    private static IClientItemExtensions hatExtensions() {
        return new IClientItemExtensions() {
            private HatModel<LivingEntity> model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                HatModel<LivingEntity> hat = hatModel();
                hat.setSuppressArmorPass(suppressArmorPass(entity));
                return hat;
            }

            private HatModel<LivingEntity> hatModel() {
                if (model == null) {
                    model = new HatModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(HatModel.LAYER));
                }
                return model;
            }
        };
    }

    private static IClientItemExtensions cowboyHatExtensions() {
        return new IClientItemExtensions() {
            private CowboyHatModel<LivingEntity> model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                CowboyHatModel<LivingEntity> hat = cowboyHatModel();
                hat.setSuppressArmorPass(suppressArmorPass(entity));
                return hat;
            }

            private CowboyHatModel<LivingEntity> cowboyHatModel() {
                if (model == null) {
                    model = new CowboyHatModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(CowboyHatModel.LAYER));
                }
                return model;
            }
        };
    }

    private static IClientItemExtensions strawHatExtensions() {
        return new IClientItemExtensions() {
            private StrawHatModel<LivingEntity> model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                StrawHatModel<LivingEntity> hat = strawHatModel();
                hat.setSuppressArmorPass(suppressArmorPass(entity));
                return hat;
            }

            private StrawHatModel<LivingEntity> strawHatModel() {
                if (model == null) {
                    model = new StrawHatModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(StrawHatModel.LAYER));
                }
                return model;
            }
        };
    }

    private static IClientItemExtensions capExtensions() {
        return new IClientItemExtensions() {
            private CapModel<LivingEntity> model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                CapModel<LivingEntity> cap = capModel();
                cap.setSuppressArmorPass(suppressArmorPass(entity));
                return cap;
            }

            private CapModel<LivingEntity> capModel() {
                if (model == null) {
                    model = new CapModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(CapModel.LAYER));
                }
                return model;
            }
        };
    }

    private static IClientItemExtensions helmetExtensions() {
        return new IClientItemExtensions() {
            private HelmetModel<LivingEntity> model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                HelmetModel<LivingEntity> helmet = helmetModel();
                helmet.setSuppressArmorPass(suppressArmorPass(entity));
                return helmet;
            }

            private HelmetModel<LivingEntity> helmetModel() {
                if (model == null) {
                    model = new HelmetModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(HelmetModel.LAYER));
                }
                return model;
            }
        };
    }

    private static IClientItemExtensions scrapHelmetExtensions() {
        return new IClientItemExtensions() {
            private ScrapHelmetModel<LivingEntity> model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                ScrapHelmetModel<LivingEntity> helmet = scrapHelmetModel();
                helmet.setSuppressArmorPass(suppressArmorPass(entity));
                return helmet;
            }

            private ScrapHelmetModel<LivingEntity> scrapHelmetModel() {
                if (model == null) {
                    model = new ScrapHelmetModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(ScrapHelmetModel.LAYER));
                }
                return model;
            }
        };
    }

    private static IClientItemExtensions scrapChestExtensions() {
        return new IClientItemExtensions() {
            private ScrapChestModel<LivingEntity> model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                ScrapChestModel<LivingEntity> chest = scrapChestModel();
                chest.setSuppressArmorPass(suppressArmorPass(entity));
                return chest;
            }

            private ScrapChestModel<LivingEntity> scrapChestModel() {
                if (model == null) {
                    model = new ScrapChestModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(ScrapChestModel.LAYER));
                }
                return model;
            }
        };
    }

    private static IClientItemExtensions scrapPantsExtensions() {
        return new IClientItemExtensions() {
            private ScrapPantsModel<LivingEntity> model;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                ScrapPantsModel<LivingEntity> pants = scrapPantsModel();
                pants.setSuppressArmorPass(suppressArmorPass(entity));
                return pants;
            }

            private ScrapPantsModel<LivingEntity> scrapPantsModel() {
                if (model == null) {
                    model = new ScrapPantsModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(ScrapPantsModel.LAYER));
                }
                return model;
            }
        };
    }

    private static IClientItemExtensions ghillieExtensions(ClothingType type) {
        return new IClientItemExtensions() {
            private GhillieHoodModel<LivingEntity> hood;
            private GhillieTopModel<LivingEntity> topWide;
            private GhillieTopModel<LivingEntity> topSlim;
            private GhilliePantsModel<LivingEntity> pants;
            private GhillieBootsModel<LivingEntity> boots;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                return switch (type) {
                    case GHILLIE_HOOD -> {
                        GhillieHoodModel<LivingEntity> m = hoodModel();
                        m.setSuppressArmorPass(suppressArmorPass(entity));
                        yield m;
                    }
                    case GHILLIE_TOP -> {
                        boolean slim = entity instanceof AbstractClientPlayer p
                                && "slim".equals(p.getModelName());
                        GhillieTopModel<LivingEntity> m = slim ? topSlimModel() : topWideModel();
                        m.setSuppressArmorPass(suppressArmorPass(entity));
                        yield m;
                    }
                    case GHILLIE_BOOTS -> {
                        GhillieBootsModel<LivingEntity> m = bootsModel();
                        m.setSuppressArmorPass(suppressArmorPass(entity));
                        yield m;
                    }
                    default -> {
                        GhilliePantsModel<LivingEntity> m = pantsModel();
                        m.setSuppressArmorPass(suppressArmorPass(entity));
                        yield m;
                    }
                };
            }

            private GhillieHoodModel<LivingEntity> hoodModel() {
                if (hood == null) {
                    hood = new GhillieHoodModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(GhillieHoodModel.LAYER));
                }
                return hood;
            }

            private GhillieTopModel<LivingEntity> topWideModel() {
                if (topWide == null) {
                    topWide = new GhillieTopModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(GhillieTopModel.LAYER_WIDE));
                }
                return topWide;
            }

            private GhillieTopModel<LivingEntity> topSlimModel() {
                if (topSlim == null) {
                    topSlim = new GhillieTopModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(GhillieTopModel.LAYER_SLIM));
                }
                return topSlim;
            }

            private GhilliePantsModel<LivingEntity> pantsModel() {
                if (pants == null) {
                    pants = new GhilliePantsModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(GhilliePantsModel.LAYER));
                }
                return pants;
            }

            private GhillieBootsModel<LivingEntity> bootsModel() {
                if (boots == null) {
                    boots = new GhillieBootsModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(GhillieBootsModel.LAYER));
                }
                return boots;
            }
        };
    }

    // Ghillie item icons are pre-colored like other clothing sprites (no inventory tint).
    // Worn mesh still uses {@link GhillieColors} foliage multiply in ClothingRenderLayer.

}
