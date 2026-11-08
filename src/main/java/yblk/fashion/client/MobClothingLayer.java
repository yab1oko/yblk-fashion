package yblk.fashion.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import yblk.fashion.Fashion;
import yblk.fashion.compat.CuriosClothing;
import yblk.fashion.client.model.CapModel;
import yblk.fashion.client.model.CoatModel;
import yblk.fashion.client.model.CowboyHatModel;
import yblk.fashion.client.model.GhillieBootsModel;
import yblk.fashion.client.model.GhillieHoodModel;
import yblk.fashion.client.model.GhilliePantsModel;
import yblk.fashion.client.model.GhillieTopModel;
import yblk.fashion.client.model.HatModel;
import yblk.fashion.client.model.HelmetModel;
import yblk.fashion.client.model.ScrapChestModel;
import yblk.fashion.client.model.ScrapHelmetModel;
import yblk.fashion.client.model.ScrapPantsModel;
import yblk.fashion.client.model.StrawHatModel;
import yblk.fashion.client.model.HoodieModel;
import yblk.fashion.client.model.JacketModel;
import yblk.fashion.client.model.PantsModel;
import yblk.fashion.client.model.ShoesModel;
import yblk.fashion.client.model.ShortsModel;
import yblk.fashion.client.model.SneakersModel;
import yblk.fashion.client.model.TankTopModel;
import yblk.fashion.client.model.TShirtModel;
import yblk.fashion.client.model.VestModel;
import yblk.fashion.client.model.BackpackModel;
import yblk.fashion.client.model.BalaclavaModel;
import yblk.fashion.client.model.BandanaModel;
import yblk.fashion.client.model.ClutchModel;
import yblk.fashion.client.model.GasMaskModel;
import yblk.fashion.client.model.GlassesModel;
import yblk.fashion.client.model.NvgModel;
import yblk.fashion.client.model.SportsBagModel;
import yblk.fashion.item.BackpackItem;
import yblk.fashion.item.BalaclavaItem;
import yblk.fashion.item.BandanaItem;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClothingType;
import yblk.fashion.item.ClutchItem;
import yblk.fashion.item.GasMaskItem;
import yblk.fashion.item.GlassesItem;
import yblk.fashion.item.NvgItem;
import yblk.fashion.item.SportsBagItem;
import yblk.fashion.item.TShirtColor;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

/**
 * Curios fashion on non-player humanoids when no {@link FashionCurioRenderer} is registered.
 * NPC Studio / Curios layers prefer {@link CuriosRendererRegistry}; this layer is the fallback.
 * Vanilla armor-slot pieces use {@link net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer}
 * — players still suppress that pass and use {@link ClothingRenderLayer}.
 */
public class MobClothingLayer<T extends LivingEntity, M extends HumanoidModel<T>>
        extends RenderLayer<T, M> {

    private HatModel<LivingEntity> hat;
    private CowboyHatModel<LivingEntity> cowboyHat;
    private StrawHatModel<LivingEntity> strawHat;
    private CapModel<LivingEntity> cap;
    private HelmetModel<LivingEntity> helmet;
    private ScrapHelmetModel<LivingEntity> scrapHelmet;
    private ScrapChestModel<LivingEntity> scrapChest;
    private ScrapPantsModel<LivingEntity> scrapPantsModel;
    private TShirtModel<LivingEntity> tshirtWide;
    private TankTopModel<LivingEntity> tankTop;
    private VestModel<LivingEntity> vest;
    private HoodieModel<LivingEntity> hoodieClosed;
    private HoodieModel<LivingEntity> hoodieOpen;
    private JacketModel<LivingEntity> jacketClosed;
    private JacketModel<LivingEntity> jacketOpen;
    private GhillieHoodModel<LivingEntity> ghillieHood;
    private GhillieTopModel<LivingEntity> ghillieTop;
    private GhilliePantsModel<LivingEntity> ghilliePants;
    private GhillieBootsModel<LivingEntity> ghillieBoots;
    private static final ResourceLocation GHILLIE_TEX =
            new ResourceLocation(Fashion.MODID, "textures/models/ghillie.png");
    private static final ResourceLocation SCRAP_TEX =
            new ResourceLocation(Fashion.MODID, "textures/models/scrap_armor.png");
    private CoatModel<LivingEntity> coat;
    private PantsModel<LivingEntity> pants;
    private ShortsModel<LivingEntity> shorts;
    private SneakersModel<LivingEntity> sneakers;
    private ShoesModel<LivingEntity> shoes;
    private SportsBagModel<LivingEntity> sportsBag;
    private BackpackModel<LivingEntity> backpack;
    private ClutchModel<LivingEntity> clutch;
    private BandanaModel<LivingEntity> bandana;
    private BalaclavaModel<LivingEntity> balaclava;
    private BalaclavaModel<LivingEntity> balaclavaUnderHat;
    private NvgModel<LivingEntity> nvg;
    private GasMaskModel<LivingEntity> gasMaskZero;
    private GasMaskModel<LivingEntity> gasMaskOne;
    private GasMaskModel<LivingEntity> gasMaskTwo;
    private GlassesModel<LivingEntity> glasses;

    public MobClothingLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            T entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch) {

        if (entity.isInvisible()) {
            return;
        }

        // Curios: skip stacks that FashionCurioRenderer already handles (NpcCuriosLayer / CuriosLayer).
        if (!GhillieCover.hidesExtras(entity)) {
            ItemStack outerwear = CuriosClothing.getOuterwear(entity);
            if (outerwear.isEmpty()) {
                outerwear = findAnyCurio(entity, CuriosClothing::isOuterwearStack);
            }
            if (!outerwear.isEmpty()
                    && !hasCurioRenderer(outerwear)
                    && outerwear.getItem() instanceof ClothingItem outer) {
                renderChest(poseStack, buffer, light, entity, outer);
            }
            ItemStack vestStack = CuriosClothing.getVest(entity);
            if (vestStack.isEmpty()) {
                vestStack = findAnyCurio(entity, CuriosClothing::isVestStack);
            }
            if (!vestStack.isEmpty()
                    && !hasCurioRenderer(vestStack)
                    && vestStack.getItem() instanceof ClothingItem vestWear) {
                renderChest(poseStack, buffer, light, entity, vestWear);
            }
            ItemStack scrapPantsStack = CuriosClothing.getPants(entity);
            if (scrapPantsStack.isEmpty()) {
                scrapPantsStack = findAnyCurio(entity, CuriosClothing::isPantsStack);
            }
            if (!scrapPantsStack.isEmpty()
                    && !hasCurioRenderer(scrapPantsStack)
                    && scrapPantsStack.getItem() instanceof ClothingItem scrapLeg
                    && scrapLeg.getClothingType() == ClothingType.SCRAP_PANTS) {
                renderScrapPants(poseStack, buffer, light);
            }
            renderBodyBags(poseStack, buffer, light, entity);
        }
        if (!GhillieCover.hidesExtras(entity) && !ClothingLayerHider.hidesFaceCosmetics(entity)) {
            renderMasks(poseStack, buffer, light, entity);
        }
    }

    private static boolean hasCurioRenderer(ItemStack stack) {
        return !stack.isEmpty() && CuriosRendererRegistry.getRenderer(stack.getItem()).isPresent();
    }

    private void renderMasks(PoseStack poseStack, MultiBufferSource buffer, int light, T entity) {
        ItemStack bandanaStack = findAnyCurio(entity, BandanaItem::isBandana);
        if (!bandanaStack.isEmpty()
                && !hasCurioRenderer(bandanaStack)
                && bandanaStack.getItem() instanceof BandanaItem bandanaItem) {
            BandanaModel<LivingEntity> model = bandanaModel();
            copyPose(model);
            model.setAllVisible(false);
            model.head.visible = true;
            draw(poseStack, buffer, light, model, bandanaItem.modelTexture(), false);
        }
        ItemStack balaclavaStack = findAnyCurio(entity, BalaclavaItem::isBalaclava);
        if (!balaclavaStack.isEmpty()
                && !hasCurioRenderer(balaclavaStack)
                && balaclavaStack.getItem() instanceof BalaclavaItem balaclavaItem) {
            boolean underHat = entity.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ClothingItem wear
                    && wear.getClothingType().isHelmet();
            BalaclavaModel<LivingEntity> model = underHat ? balaclavaUnderHatModel() : balaclavaModel();
            copyPose(model);
            model.setAllVisible(false);
            model.head.visible = true;
            draw(poseStack, buffer, light, model, balaclavaItem.modelTexture(), false);
        }
        ItemStack nvgStack = findAnyCurio(entity, NvgItem::isNvg);
        if (!nvgStack.isEmpty()
                && !hasCurioRenderer(nvgStack)
                && nvgStack.getItem() instanceof NvgItem nvgItem) {
            NvgModel<LivingEntity> model = nvgModel();
            copyPose(model);
            model.setAllVisible(false);
            model.head.visible = true;
            draw(poseStack, buffer, light, model, nvgItem.modelTexture(), false);
            VertexConsumer glow = buffer.getBuffer(
                    RenderType.entityTranslucentEmissive(nvgItem.emissiveTexture()));
            model.renderToBuffer(poseStack, glow, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
        }
        ItemStack gasMaskStack = findAnyCurio(entity, GasMaskItem::isGasMask);
        if (!gasMaskStack.isEmpty()
                && !hasCurioRenderer(gasMaskStack)
                && gasMaskStack.getItem() instanceof GasMaskItem gasMaskItem) {
            GasMaskModel<LivingEntity> model = gasMaskModel(GasMaskItem.filterCount(gasMaskStack));
            copyPose(model);
            model.setAllVisible(false);
            model.head.visible = true;
            VertexConsumer mask = buffer.getBuffer(
                    RenderType.entityTranslucent(gasMaskItem.modelTexture()));
            model.renderToBuffer(
                    poseStack, mask, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
        }
        ItemStack glassesStack = findAnyCurio(entity, GlassesItem::isGlasses);
        if (!glassesStack.isEmpty()
                && !hasCurioRenderer(glassesStack)
                && glassesStack.getItem() instanceof GlassesItem glassesItem) {
            GlassesModel<LivingEntity> model = glassesModel();
            copyPose(model);
            model.setAllVisible(false);
            model.head.visible = true;
            VertexConsumer glass = buffer.getBuffer(
                    RenderType.entityTranslucent(glassesItem.modelTexture()));
            model.renderToBuffer(
                    poseStack, glass, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
        }
    }

    private void renderBodyBags(
            PoseStack poseStack, MultiBufferSource buffer, int light, T entity) {
        ItemStack sports = findAnyCurio(entity, SportsBagItem::isSportsBag);
        if (!sports.isEmpty()
                && !hasCurioRenderer(sports)
                && sports.getItem() instanceof SportsBagItem bag) {
            SportsBagModel<LivingEntity> model = sportsBagModel();
            copyPose(model);
            model.setAllVisible(false);
            model.body.visible = true;
            draw(poseStack, buffer, light, model, bag.modelTexture(), false);
        }
        ItemStack pack = findAnyCurio(entity, BackpackItem::isBackpack);
        if (!pack.isEmpty()
                && !hasCurioRenderer(pack)
                && pack.getItem() instanceof BackpackItem bag) {
            BackpackModel<LivingEntity> model = backpackModel();
            copyPose(model);
            model.setAllVisible(false);
            model.body.visible = true;
            draw(poseStack, buffer, light, model, bag.modelTexture(), false);
        }
        ItemStack clutchStack = findAnyCurio(entity, ClutchItem::isClutch);
        if (!clutchStack.isEmpty()
                && !hasCurioRenderer(clutchStack)
                && clutchStack.getItem() instanceof ClutchItem bag) {
            ClutchModel<LivingEntity> model = clutchModel();
            copyPose(model);
            model.setAllVisible(false);
            model.body.visible = true;
            if (!CuriosClothing.getVest(entity).isEmpty()) {
                model.body.yRot += (float) Math.PI;
            }
            draw(poseStack, buffer, light, model, bag.modelTexture(), false);
        }
    }

    private static ItemStack findAnyCurio(
            LivingEntity entity, java.util.function.Predicate<ItemStack> match) {
        return CuriosApi.getCuriosInventory(entity)
                .resolve()
                .map(inv -> {
                    for (var entry : inv.getCurios().entrySet()) {
                        var stacks = entry.getValue().getStacks();
                        for (int i = 0; i < stacks.getSlots(); i++) {
                            ItemStack stack = stacks.getStackInSlot(i);
                            if (match.test(stack)) {
                                return stack;
                            }
                        }
                    }
                    return ItemStack.EMPTY;
                })
                .orElse(ItemStack.EMPTY);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void copyPose(HumanoidModel<?> model) {
        getParentModel().copyPropertiesTo((HumanoidModel) model);
    }

    private void renderHat(
            PoseStack poseStack, MultiBufferSource buffer, int light, T entity, ClothingItem clothing) {
        HatModel<LivingEntity> model = hatModel();
        copyPose(model);
        model.setAllVisible(false);
        model.head.visible = true;
        model.setSuppressArmorPass(false);
        ItemStack hooded = CuriosClothing.hoodedPiece(entity);
        boolean hoodUp = !hooded.isEmpty() && ClothingItem.isHoodOpen(hooded);
        model.setKnobVisible(!hoodUp);
        draw(poseStack, buffer, light, model, hatTex(clothing), true);
    }

    private void renderCowboyHat(
            PoseStack poseStack, MultiBufferSource buffer, int light, ClothingItem clothing) {
        CowboyHatModel<LivingEntity> model = cowboyHatModel();
        copyPose(model);
        model.setAllVisible(false);
        model.head.visible = true;
        model.setSuppressArmorPass(false);
        draw(poseStack, buffer, light, model, cowboyHatTex(clothing), false);
    }

    private void renderStrawHat(PoseStack poseStack, MultiBufferSource buffer, int light) {
        StrawHatModel<LivingEntity> model = strawHatModel();
        copyPose(model);
        model.setAllVisible(false);
        model.head.visible = true;
        model.setSuppressArmorPass(false);
        draw(poseStack, buffer, light, model,
                new ResourceLocation(Fashion.MODID, "textures/models/straw_hat.png"), false);
    }

    private void renderCap(
            PoseStack poseStack, MultiBufferSource buffer, int light, ClothingItem clothing) {
        CapModel<LivingEntity> model = capModel();
        copyPose(model);
        model.setAllVisible(false);
        model.head.visible = true;
        model.setSuppressArmorPass(false);
        draw(poseStack, buffer, light, model, capTex(clothing), true);
    }

    private void renderHelmet(PoseStack poseStack, MultiBufferSource buffer, int light) {
        HelmetModel<LivingEntity> model = helmetModel();
        copyPose(model);
        model.setAllVisible(false);
        model.head.visible = true;
        model.setSuppressArmorPass(false);
        draw(poseStack, buffer, light, model,
                new ResourceLocation(Fashion.MODID, "textures/models/helmet.png"), true);
    }

    private void renderScrapHelmet(PoseStack poseStack, MultiBufferSource buffer, int light) {
        ScrapHelmetModel<LivingEntity> model = scrapHelmetModel();
        copyPose(model);
        model.setAllVisible(false);
        model.head.visible = true;
        model.setSuppressArmorPass(false);
        draw(poseStack, buffer, light, model, SCRAP_TEX, true);
    }

    private void renderScrapPants(PoseStack poseStack, MultiBufferSource buffer, int light) {
        ScrapPantsModel<LivingEntity> model = scrapPantsModel();
        copyPose(model);
        model.setAllVisible(false);
        model.leftLeg.visible = true;
        model.rightLeg.visible = true;
        model.setSuppressArmorPass(false);
        draw(poseStack, buffer, light, model, SCRAP_TEX, true);
    }

    private void renderChest(
            PoseStack poseStack, MultiBufferSource buffer, int light, T entity, ClothingItem clothing) {
        ClothingType type = clothing.getClothingType();
        HumanoidModel<LivingEntity> model;
        ResourceLocation tex;
        if (type == ClothingType.COAT) {
            CoatModel<LivingEntity> coatModel = coatModel();
            ResourceLocation coatTexture = coatTex(clothing);
            coatModel.setSuppressArmorPass(false);
            copyPose(coatModel);
            coatModel.setAllVisible(false);
            coatModel.body.visible = true;
            coatModel.leftArm.visible = true;
            coatModel.rightArm.visible = true;
            coatModel.leftLeg.visible = true;
            coatModel.rightLeg.visible = true;
            coatModel.head.visible = true;
            draw(poseStack, buffer, light, coatModel, coatTexture, true);
            return;
        }
        if (type == ClothingType.GHILLIE_TOP) {
            renderGhillieTop(poseStack, buffer, light, entity);
            return;
        }
        if (type == ClothingType.TANK_TOP) {
            TankTopModel<LivingEntity> tank = tankTopModel();
            tank.setSuppressArmorPass(false);
            copyPose(tank);
            tank.setAllVisible(false);
            tank.body.visible = true;
            draw(poseStack, buffer, light, tank, tankTopTex(clothing), true);
            return;
        }
        if (type == ClothingType.VEST) {
            VestModel<LivingEntity> vestModel = vestModel();
            vestModel.setSuppressArmorPass(false);
            copyPose(vestModel);
            vestModel.setAllVisible(false);
            vestModel.body.visible = true;
            draw(poseStack, buffer, light, vestModel, vestTex(clothing), true);
            return;
        }
        if (type == ClothingType.SCRAP_VEST) {
            ScrapChestModel<LivingEntity> scrap = scrapChestModel();
            scrap.setSuppressArmorPass(false);
            copyPose(scrap);
            scrap.setAllVisible(false);
            scrap.body.visible = true;
            scrap.leftArm.visible = true;
            scrap.rightArm.visible = true;
            draw(poseStack, buffer, light, scrap, SCRAP_TEX, true);
            return;
        }
        if (type.hasHood()) {
            ItemStack hooded = CuriosClothing.hoodedPiece(entity);
            boolean open = !hooded.isEmpty() && ClothingItem.isHoodOpen(hooded);
            model = type == ClothingType.JACKET
                    ? (open ? jacketOpenModel() : jacketClosedModel())
                    : (open ? hoodieOpenModel() : hoodieClosedModel());
            tex = type == ClothingType.JACKET ? jacketTex(clothing) : hoodieTex(clothing);
            clearArmorSuppress(model);
            copyPose(model);
            model.setAllVisible(false);
            model.body.visible = true;
            model.leftArm.visible = true;
            model.rightArm.visible = true;
            draw(poseStack, buffer, light, model, tex, true);
            model.body.visible = false;
            model.leftArm.visible = false;
            model.rightArm.visible = false;
            model.head.visible = true;
            draw(poseStack, buffer, light, model, tex, false);
        } else {
            model = tshirtModel();
            tex = tshirtTex(clothing);
            clearArmorSuppress(model);
            copyPose(model);
            model.setAllVisible(false);
            model.body.visible = true;
            model.leftArm.visible = true;
            model.rightArm.visible = true;
            draw(poseStack, buffer, light, model, tex, true);
        }
    }

    private void renderPants(
            PoseStack poseStack, MultiBufferSource buffer, int light, ClothingItem clothing) {
        PantsModel<LivingEntity> model = pantsModel();
        copyPose(model);
        model.setAllVisible(false);
        model.leftLeg.visible = true;
        model.rightLeg.visible = true;
        model.setSuppressArmorPass(false);
        draw(poseStack, buffer, light, model, pantsTex(clothing), true);
    }

    private void renderShorts(
            PoseStack poseStack, MultiBufferSource buffer, int light, ClothingItem clothing) {
        ShortsModel<LivingEntity> model = shortsModel();
        copyPose(model);
        model.setAllVisible(false);
        model.leftLeg.visible = true;
        model.rightLeg.visible = true;
        model.setSuppressArmorPass(false);
        draw(poseStack, buffer, light, model, shortsTex(clothing), true);
    }

    private void renderSneakers(
            PoseStack poseStack, MultiBufferSource buffer, int light, ClothingItem clothing) {
        SneakersModel<LivingEntity> model = sneakersModel();
        copyPose(model);
        model.setAllVisible(false);
        model.leftLeg.visible = true;
        model.rightLeg.visible = true;
        model.setSuppressArmorPass(false);
        draw(poseStack, buffer, light, model, sneakersTex(clothing), true);
    }

    private void renderShoes(
            PoseStack poseStack, MultiBufferSource buffer, int light, ClothingItem clothing) {
        ShoesModel<LivingEntity> model = shoesModel();
        copyPose(model);
        model.setAllVisible(false);
        model.leftLeg.visible = true;
        model.rightLeg.visible = true;
        model.setSuppressArmorPass(false);
        draw(poseStack, buffer, light, model, shoesTex(clothing), true);
    }

    private void renderGhillieHood(
            PoseStack poseStack, MultiBufferSource buffer, int light, T entity) {
        GhillieHoodModel<LivingEntity> model = ghillieHoodModel();
        copyPose(model);
        model.setAllVisible(false);
        model.head.visible = true;
        model.setSuppressArmorPass(false);
        drawGhillie(poseStack, buffer, light, entity, model);
    }

    private void renderGhillieTop(
            PoseStack poseStack, MultiBufferSource buffer, int light, T entity) {
        GhillieTopModel<LivingEntity> model = ghillieTopModel();
        copyPose(model);
        model.setAllVisible(false);
        model.body.visible = true;
        model.leftArm.visible = true;
        model.rightArm.visible = true;
        model.setSuppressArmorPass(false);
        drawGhillie(poseStack, buffer, light, entity, model);
    }

    private void renderGhilliePants(
            PoseStack poseStack, MultiBufferSource buffer, int light, T entity) {
        GhilliePantsModel<LivingEntity> model = ghilliePantsModel();
        copyPose(model);
        model.setAllVisible(false);
        model.leftLeg.visible = true;
        model.rightLeg.visible = true;
        model.setSuppressArmorPass(false);
        drawGhillie(poseStack, buffer, light, entity, model);
    }

    private void renderGhillieBoots(
            PoseStack poseStack, MultiBufferSource buffer, int light, T entity) {
        GhillieBootsModel<LivingEntity> model = ghillieBootsModel();
        copyPose(model);
        model.setAllVisible(false);
        model.leftLeg.visible = true;
        model.rightLeg.visible = true;
        model.setSuppressArmorPass(false);
        drawGhillie(poseStack, buffer, light, entity, model);
    }

    private static void drawGhillie(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            LivingEntity entity,
            HumanoidModel<LivingEntity> model) {
        int rgb = GhillieColors.foliage(entity);
        VertexConsumer vc = new FlatShadeVertexConsumer(
                buffer.getBuffer(RenderType.entityCutoutNoCull(GHILLIE_TEX)));
        model.renderToBuffer(
                poseStack,
                vc,
                light,
                OverlayTexture.NO_OVERLAY,
                GhillieColors.red(rgb),
                GhillieColors.green(rgb),
                GhillieColors.blue(rgb),
                1f);
    }

    private static void clearArmorSuppress(HumanoidModel<?> model) {
        if (model instanceof HoodieModel<?> hoodie) {
            hoodie.setSuppressArmorPass(false);
        } else if (model instanceof JacketModel<?> jacket) {
            jacket.setSuppressArmorPass(false);
        } else if (model instanceof CoatModel<?> coatModel) {
            coatModel.setSuppressArmorPass(false);
        } else if (model instanceof TShirtModel<?> tshirt) {
            tshirt.setSuppressArmorPass(false);
        }
    }

    private static void draw(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            HumanoidModel<?> model,
            ResourceLocation tex,
            boolean cull) {
        RenderType renderType = cull
                ? RenderType.entityCutout(tex)
                : RenderType.entityCutoutNoCull(tex);
        VertexConsumer vc = buffer.getBuffer(renderType);
        model.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private static ResourceLocation hatTex(ClothingItem c) {
        TShirtColor color = c.getColor();
        return new ResourceLocation(Fashion.MODID,
                color != null ? color.hatModelTexturePath() : "textures/models/red_hat.png");
    }

    private static ResourceLocation cowboyHatTex(ClothingItem c) {
        TShirtColor color = c.getColor();
        return new ResourceLocation(Fashion.MODID,
                color != null && color.hasCowboyHat()
                        ? color.cowboyHatModelTexturePath()
                        : "textures/models/brown_cowboy_hat.png");
    }

    private static ResourceLocation capTex(ClothingItem c) {
        TShirtColor color = c.getColor();
        return new ResourceLocation(Fashion.MODID,
                color != null ? color.capModelTexturePath() : "textures/models/red_cap.png");
    }

    private static ResourceLocation tshirtTex(ClothingItem c) {
        TShirtColor color = c.getTShirtColor();
        return new ResourceLocation(Fashion.MODID,
                color != null ? color.modelTexturePath() : "textures/models/red_tshirt.png");
    }

    private static ResourceLocation tankTopTex(ClothingItem c) {
        TShirtColor color = c.getColor();
        return new ResourceLocation(Fashion.MODID,
                color != null && color.hasTankTop()
                        ? color.tankTopModelTexturePath()
                        : "textures/models/white_tank_top.png");
    }

    private static ResourceLocation vestTex(ClothingItem c) {
        TShirtColor color = c.getColor();
        return new ResourceLocation(Fashion.MODID,
                color != null && color.hasVest()
                        ? color.vestModelTexturePath()
                        : "textures/models/black_vest.png");
    }

    private static ResourceLocation hoodieTex(ClothingItem c) {
        TShirtColor color = c.getColor();
        return new ResourceLocation(Fashion.MODID,
                color != null ? color.hoodieModelTexturePath() : "textures/models/red_hoodie.png");
    }

    private static ResourceLocation jacketTex(ClothingItem c) {
        TShirtColor color = c.getColor();
        return new ResourceLocation(Fashion.MODID,
                color != null ? color.jacketModelTexturePath() : "textures/models/red_jacket.png");
    }

    private static ResourceLocation coatTex(ClothingItem c) {
        TShirtColor color = c.getColor();
        return new ResourceLocation(Fashion.MODID,
                color != null ? color.coatModelTexturePath() : "textures/models/brown_coat.png");
    }

    private static ResourceLocation pantsTex(ClothingItem c) {
        TShirtColor color = c.getColor();
        return new ResourceLocation(Fashion.MODID,
                color != null ? color.pantsModelTexturePath() : "textures/models/red_pants.png");
    }

    private static ResourceLocation shortsTex(ClothingItem c) {
        TShirtColor color = c.getColor();
        return new ResourceLocation(Fashion.MODID,
                color != null ? color.shortsModelTexturePath() : "textures/models/red_shorts.png");
    }

    private static ResourceLocation sneakersTex(ClothingItem c) {
        TShirtColor color = c.getColor();
        return new ResourceLocation(Fashion.MODID,
                color != null ? color.sneakersModelTexturePath() : "textures/models/red_sneakers.png");
    }

    private static ResourceLocation shoesTex(ClothingItem c) {
        TShirtColor color = c.getColor();
        return new ResourceLocation(Fashion.MODID,
                color != null && color.hasShoes()
                        ? color.shoesModelTexturePath()
                        : "textures/models/brown_shoes.png");
    }

    private HatModel<LivingEntity> hatModel() {
        if (hat == null) {
            hat = new HatModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(HatModel.LAYER));
        }
        return hat;
    }

    private CowboyHatModel<LivingEntity> cowboyHatModel() {
        if (cowboyHat == null) {
            cowboyHat = new CowboyHatModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(CowboyHatModel.LAYER));
        }
        return cowboyHat;
    }

    private StrawHatModel<LivingEntity> strawHatModel() {
        if (strawHat == null) {
            strawHat = new StrawHatModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(StrawHatModel.LAYER));
        }
        return strawHat;
    }

    private CapModel<LivingEntity> capModel() {
        if (cap == null) {
            cap = new CapModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(CapModel.LAYER));
        }
        return cap;
    }

    private HelmetModel<LivingEntity> helmetModel() {
        if (helmet == null) {
            helmet = new HelmetModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(HelmetModel.LAYER));
        }
        return helmet;
    }

    private ScrapHelmetModel<LivingEntity> scrapHelmetModel() {
        if (scrapHelmet == null) {
            scrapHelmet = new ScrapHelmetModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ScrapHelmetModel.LAYER));
        }
        return scrapHelmet;
    }

    private ScrapChestModel<LivingEntity> scrapChestModel() {
        if (scrapChest == null) {
            scrapChest = new ScrapChestModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ScrapChestModel.LAYER));
        }
        return scrapChest;
    }

    private ScrapPantsModel<LivingEntity> scrapPantsModel() {
        if (scrapPantsModel == null) {
            scrapPantsModel = new ScrapPantsModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ScrapPantsModel.LAYER));
        }
        return scrapPantsModel;
    }

    private TShirtModel<LivingEntity> tshirtModel() {
        if (tshirtWide == null) {
            tshirtWide = new TShirtModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(TShirtModel.LAYER_WIDE));
        }
        return tshirtWide;
    }

    private TankTopModel<LivingEntity> tankTopModel() {
        if (tankTop == null) {
            tankTop = new TankTopModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(TankTopModel.LAYER));
        }
        return tankTop;
    }

    private VestModel<LivingEntity> vestModel() {
        if (vest == null) {
            vest = new VestModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(VestModel.LAYER));
        }
        return vest;
    }

    private HoodieModel<LivingEntity> hoodieClosedModel() {
        if (hoodieClosed == null) {
            hoodieClosed = new HoodieModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(HoodieModel.LAYER_CLOSED_WIDE));
        }
        return hoodieClosed;
    }

    private HoodieModel<LivingEntity> hoodieOpenModel() {
        if (hoodieOpen == null) {
            hoodieOpen = new HoodieModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(HoodieModel.LAYER_OPEN_WIDE));
        }
        return hoodieOpen;
    }

    private JacketModel<LivingEntity> jacketClosedModel() {
        if (jacketClosed == null) {
            jacketClosed = new JacketModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(JacketModel.LAYER_CLOSED_WIDE));
        }
        return jacketClosed;
    }

    private JacketModel<LivingEntity> jacketOpenModel() {
        if (jacketOpen == null) {
            jacketOpen = new JacketModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(JacketModel.LAYER_OPEN_WIDE));
        }
        return jacketOpen;
    }

    private CoatModel<LivingEntity> coatModel() {
        if (coat == null) {
            coat = new CoatModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(CoatModel.LAYER_WIDE));
        }
        return coat;
    }

    private PantsModel<LivingEntity> pantsModel() {
        if (pants == null) {
            pants = new PantsModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(PantsModel.LAYER));
        }
        return pants;
    }

    private ShortsModel<LivingEntity> shortsModel() {
        if (shorts == null) {
            shorts = new ShortsModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ShortsModel.LAYER));
        }
        return shorts;
    }

    private SneakersModel<LivingEntity> sneakersModel() {
        if (sneakers == null) {
            sneakers = new SneakersModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(SneakersModel.LAYER));
        }
        return sneakers;
    }

    private ShoesModel<LivingEntity> shoesModel() {
        if (shoes == null) {
            shoes = new ShoesModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ShoesModel.LAYER));
        }
        return shoes;
    }

    private GhillieHoodModel<LivingEntity> ghillieHoodModel() {
        if (ghillieHood == null) {
            ghillieHood = new GhillieHoodModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(GhillieHoodModel.LAYER));
        }
        return ghillieHood;
    }

    private GhillieTopModel<LivingEntity> ghillieTopModel() {
        if (ghillieTop == null) {
            ghillieTop = new GhillieTopModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(GhillieTopModel.LAYER_WIDE));
        }
        return ghillieTop;
    }

    private GhilliePantsModel<LivingEntity> ghilliePantsModel() {
        if (ghilliePants == null) {
            ghilliePants = new GhilliePantsModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(GhilliePantsModel.LAYER));
        }
        return ghilliePants;
    }

    private GhillieBootsModel<LivingEntity> ghillieBootsModel() {
        if (ghillieBoots == null) {
            ghillieBoots = new GhillieBootsModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(GhillieBootsModel.LAYER));
        }
        return ghillieBoots;
    }

    private SportsBagModel<LivingEntity> sportsBagModel() {
        if (sportsBag == null) {
            sportsBag = new SportsBagModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(SportsBagModel.LAYER));
        }
        return sportsBag;
    }

    private BackpackModel<LivingEntity> backpackModel() {
        if (backpack == null) {
            backpack = new BackpackModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(BackpackModel.LAYER));
        }
        return backpack;
    }

    private ClutchModel<LivingEntity> clutchModel() {
        if (clutch == null) {
            clutch = new ClutchModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ClutchModel.LAYER));
        }
        return clutch;
    }

    private BandanaModel<LivingEntity> bandanaModel() {
        if (bandana == null) {
            bandana = new BandanaModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(BandanaModel.LAYER));
        }
        return bandana;
    }

    private BalaclavaModel<LivingEntity> balaclavaModel() {
        if (balaclava == null) {
            balaclava = new BalaclavaModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(BalaclavaModel.LAYER));
        }
        return balaclava;
    }

    private BalaclavaModel<LivingEntity> balaclavaUnderHatModel() {
        if (balaclavaUnderHat == null) {
            balaclavaUnderHat = new BalaclavaModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(BalaclavaModel.LAYER_UNDER_HAT));
        }
        return balaclavaUnderHat;
    }

    private NvgModel<LivingEntity> nvgModel() {
        if (nvg == null) {
            nvg = new NvgModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(NvgModel.LAYER));
        }
        return nvg;
    }

    private GasMaskModel<LivingEntity> gasMaskModel(int filters) {
        if (filters >= 2) {
            if (gasMaskTwo == null) {
                gasMaskTwo = new GasMaskModel<>(
                        Minecraft.getInstance().getEntityModels().bakeLayer(GasMaskModel.LAYER_TWO));
            }
            return gasMaskTwo;
        }
        if (filters == 1) {
            if (gasMaskOne == null) {
                gasMaskOne = new GasMaskModel<>(
                        Minecraft.getInstance().getEntityModels().bakeLayer(GasMaskModel.LAYER_ONE));
            }
            return gasMaskOne;
        }
        if (gasMaskZero == null) {
            gasMaskZero = new GasMaskModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(GasMaskModel.LAYER_ZERO));
        }
        return gasMaskZero;
    }

    private GlassesModel<LivingEntity> glassesModel() {
        if (glasses == null) {
            glasses = new GlassesModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(GlassesModel.LAYER));
        }
        return glasses;
    }
}
