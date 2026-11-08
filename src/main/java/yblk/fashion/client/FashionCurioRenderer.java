package yblk.fashion.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;
import yblk.fashion.Fashion;
import yblk.fashion.compat.CuriosClothing;
import yblk.fashion.client.model.BackpackModel;
import yblk.fashion.client.model.BalaclavaModel;
import yblk.fashion.client.model.BandanaModel;
import yblk.fashion.client.model.ClutchModel;
import yblk.fashion.client.model.GasMaskModel;
import yblk.fashion.client.model.GlassesModel;
import yblk.fashion.client.model.CoatModel;
import yblk.fashion.client.model.HoodieModel;
import yblk.fashion.client.model.JacketModel;
import yblk.fashion.client.model.NvgModel;
import yblk.fashion.client.model.ScrapChestModel;
import yblk.fashion.client.model.ScrapPantsModel;
import yblk.fashion.client.model.SportsBagModel;
import yblk.fashion.client.model.VestModel;
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

/**
 * Curios mesh for non-players (NPC Studio {@code NpcCuriosLayer} looks up
 * {@link top.theillusivec4.curios.api.client.CuriosRendererRegistry}).
 * Players keep {@link ClothingRenderLayer} / dedicated face layers — this no-ops for them.
 */
public final class FashionCurioRenderer implements ICurioRenderer {
    public static final FashionCurioRenderer INSTANCE = new FashionCurioRenderer();

    private static final ResourceLocation SCRAP_TEX =
            new ResourceLocation(Fashion.MODID, "textures/models/scrap_armor.png");

    private VestModel<LivingEntity> vest;
    private ScrapChestModel<LivingEntity> scrapChest;
    private ScrapPantsModel<LivingEntity> scrapPants;
    private HoodieModel<LivingEntity> hoodieClosed;
    private HoodieModel<LivingEntity> hoodieOpen;
    private JacketModel<LivingEntity> jacketClosed;
    private JacketModel<LivingEntity> jacketOpen;
    private CoatModel<LivingEntity> coat;
    private BandanaModel<LivingEntity> bandana;
    private BalaclavaModel<LivingEntity> balaclava;
    private BalaclavaModel<LivingEntity> balaclavaUnderHat;
    private NvgModel<LivingEntity> nvg;
    private GasMaskModel<LivingEntity> gasMaskZero;
    private GasMaskModel<LivingEntity> gasMaskOne;
    private GasMaskModel<LivingEntity> gasMaskTwo;
    private GlassesModel<LivingEntity> glasses;
    private SportsBagModel<LivingEntity> sportsBag;
    private BackpackModel<LivingEntity> backpack;
    private ClutchModel<LivingEntity> clutch;

    private FashionCurioRenderer() {
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack,
            SlotContext slotContext,
            PoseStack poseStack,
            RenderLayerParent<T, M> parent,
            MultiBufferSource buffer,
            int light,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch) {
        LivingEntity entity = slotContext.entity();
        if (entity == null || entity instanceof Player || entity.isInvisible()) {
            return;
        }
        if (!(parent.getModel() instanceof HumanoidModel<?> humanoid)) {
            return;
        }
        if (stack.getItem() instanceof ClothingItem clothing) {
            if (GhillieCover.hidesExtras(entity) && clothing.getClothingType().isCurioWear()) {
                return;
            }
            renderClothing(poseStack, buffer, light, entity, humanoid, clothing);
            return;
        }
        if (GhillieCover.hidesExtras(entity) || ClothingLayerHider.hidesFaceCosmetics(entity)) {
            if (stack.getItem() instanceof BandanaItem
                    || stack.getItem() instanceof BalaclavaItem
                    || stack.getItem() instanceof NvgItem
                    || stack.getItem() instanceof GasMaskItem
                    || stack.getItem() instanceof GlassesItem) {
                return;
            }
        }
        if (stack.getItem() instanceof BandanaItem bandanaItem) {
            renderBandana(poseStack, buffer, light, humanoid, bandanaItem);
        } else if (stack.getItem() instanceof BalaclavaItem balaclavaItem) {
            renderBalaclava(poseStack, buffer, light, entity, humanoid, balaclavaItem);
        } else if (stack.getItem() instanceof GasMaskItem gasMaskItem) {
            renderGasMask(poseStack, buffer, light, humanoid, stack, gasMaskItem);
        } else if (stack.getItem() instanceof GlassesItem glassesItem) {
            renderGlasses(poseStack, buffer, light, humanoid, glassesItem);
        } else if (stack.getItem() instanceof NvgItem nvgItem) {
            renderNvg(poseStack, buffer, light, humanoid, nvgItem);
        } else if (!GhillieCover.hidesExtras(entity)) {
            if (stack.getItem() instanceof SportsBagItem bag) {
                renderBag(poseStack, buffer, light, humanoid, sportsBagModel(), bag.modelTexture());
            } else if (stack.getItem() instanceof BackpackItem bag) {
                renderBag(poseStack, buffer, light, humanoid, backpackModel(), bag.modelTexture());
            } else if (stack.getItem() instanceof ClutchItem bag) {
                ClutchModel<LivingEntity> model = clutchModel();
                copyPose(humanoid, model);
                model.setAllVisible(false);
                model.body.visible = true;
                if (!CuriosClothing.getVest(entity).isEmpty()) {
                    model.body.yRot += (float) Math.PI;
                }
                draw(poseStack, buffer, light, model, bag.modelTexture(), false);
            }
        }
    }

    private void renderClothing(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            LivingEntity entity,
            HumanoidModel<?> humanoid,
            ClothingItem clothing) {
        ClothingType type = clothing.getClothingType();
        if (type == ClothingType.COAT) {
            CoatModel<LivingEntity> model = coatModel();
            model.setSuppressArmorPass(false);
            copyPose(humanoid, model);
            model.setAllVisible(false);
            model.body.visible = true;
            model.leftArm.visible = true;
            model.rightArm.visible = true;
            model.leftLeg.visible = true;
            model.rightLeg.visible = true;
            model.head.visible = true;
            draw(poseStack, buffer, light, model, coatTex(clothing), true);
            return;
        }
        if (type == ClothingType.VEST) {
            VestModel<LivingEntity> model = vestModel();
            model.setSuppressArmorPass(false);
            copyPose(humanoid, model);
            model.setAllVisible(false);
            model.body.visible = true;
            draw(poseStack, buffer, light, model, vestTex(clothing), true);
            return;
        }
        if (type == ClothingType.SCRAP_VEST) {
            ScrapChestModel<LivingEntity> model = scrapChestModel();
            model.setSuppressArmorPass(false);
            copyPose(humanoid, model);
            model.setAllVisible(false);
            model.body.visible = true;
            model.leftArm.visible = true;
            model.rightArm.visible = true;
            draw(poseStack, buffer, light, model, SCRAP_TEX, true);
            return;
        }
        if (type == ClothingType.SCRAP_PANTS) {
            ScrapPantsModel<LivingEntity> model = scrapPantsModel();
            model.setSuppressArmorPass(false);
            copyPose(humanoid, model);
            model.setAllVisible(false);
            model.leftLeg.visible = true;
            model.rightLeg.visible = true;
            draw(poseStack, buffer, light, model, SCRAP_TEX, true);
            return;
        }
        if (type.hasHood()) {
            ItemStack hooded = CuriosClothing.hoodedPiece(entity);
            boolean open = !hooded.isEmpty() && ClothingItem.isHoodOpen(hooded);
            HumanoidModel<LivingEntity> model = type == ClothingType.JACKET
                    ? (open ? jacketOpenModel() : jacketClosedModel())
                    : (open ? hoodieOpenModel() : hoodieClosedModel());
            clearArmorSuppress(model);
            ResourceLocation tex = type == ClothingType.JACKET ? jacketTex(clothing) : hoodieTex(clothing);
            copyPose(humanoid, model);
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
        }
    }

    private void renderBandana(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            HumanoidModel<?> humanoid,
            BandanaItem item) {
        BandanaModel<LivingEntity> model = bandanaModel();
        copyPose(humanoid, model);
        model.setAllVisible(false);
        model.head.visible = true;
        draw(poseStack, buffer, light, model, item.modelTexture(), false);
    }

    private void renderBalaclava(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            LivingEntity entity,
            HumanoidModel<?> humanoid,
            BalaclavaItem item) {
        boolean underHat = wearingHeadwear(entity);
        BalaclavaModel<LivingEntity> model = underHat ? balaclavaUnderHatModel() : balaclavaModel();
        copyPose(humanoid, model);
        model.setAllVisible(false);
        model.head.visible = true;
        draw(poseStack, buffer, light, model, item.modelTexture(), false);
    }

    private void renderNvg(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            HumanoidModel<?> humanoid,
            NvgItem item) {
        NvgModel<LivingEntity> model = nvgModel();
        copyPose(humanoid, model);
        model.setAllVisible(false);
        model.head.visible = true;
        draw(poseStack, buffer, light, model, item.modelTexture(), false);
        VertexConsumer glow = buffer.getBuffer(RenderType.entityTranslucentEmissive(item.emissiveTexture()));
        model.renderToBuffer(poseStack, glow, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private void renderGasMask(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            HumanoidModel<?> humanoid,
            ItemStack stack,
            GasMaskItem item) {
        GasMaskModel<LivingEntity> model = gasMaskModel(GasMaskItem.filterCount(stack));
        copyPose(humanoid, model);
        model.setAllVisible(false);
        model.head.visible = true;
        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(item.modelTexture()));
        model.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private void renderGlasses(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            HumanoidModel<?> humanoid,
            GlassesItem item) {
        GlassesModel<LivingEntity> model = glassesModel();
        copyPose(humanoid, model);
        model.setAllVisible(false);
        model.head.visible = true;
        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(item.modelTexture()));
        model.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private void renderBag(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            HumanoidModel<?> humanoid,
            HumanoidModel<LivingEntity> model,
            ResourceLocation tex) {
        copyPose(humanoid, model);
        model.setAllVisible(false);
        model.body.visible = true;
        draw(poseStack, buffer, light, model, tex, false);
    }

    private static boolean wearingHeadwear(LivingEntity entity) {
        ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
        return head.getItem() instanceof ClothingItem wear && wear.getClothingType().isHelmet();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void copyPose(HumanoidModel<?> from, HumanoidModel<?> to) {
        from.copyPropertiesTo((HumanoidModel) to);
    }

    private static void clearArmorSuppress(HumanoidModel<?> model) {
        if (model instanceof HoodieModel<?> hoodie) {
            hoodie.setSuppressArmorPass(false);
        } else if (model instanceof JacketModel<?> jacket) {
            jacket.setSuppressArmorPass(false);
        } else if (model instanceof CoatModel<?> coatModel) {
            coatModel.setSuppressArmorPass(false);
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

    private VestModel<LivingEntity> vestModel() {
        if (vest == null) {
            vest = new VestModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(VestModel.LAYER));
        }
        return vest;
    }

    private ScrapChestModel<LivingEntity> scrapChestModel() {
        if (scrapChest == null) {
            scrapChest = new ScrapChestModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ScrapChestModel.LAYER));
        }
        return scrapChest;
    }

    private ScrapPantsModel<LivingEntity> scrapPantsModel() {
        if (scrapPants == null) {
            scrapPants = new ScrapPantsModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ScrapPantsModel.LAYER));
        }
        return scrapPants;
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
}
