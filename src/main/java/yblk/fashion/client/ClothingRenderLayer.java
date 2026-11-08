package yblk.fashion.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
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
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClothingType;
import yblk.fashion.item.TShirtColor;

/**
 * Draws worn clothing on the player. Uses face culling so overlapping arm/body
 * cubes do not show dark inner edges.
 */
public class ClothingRenderLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private TShirtModel<AbstractClientPlayer> tshirtWide;
    private TShirtModel<AbstractClientPlayer> tshirtSlim;
    private TankTopModel<AbstractClientPlayer> tankTop;
    private VestModel<AbstractClientPlayer> vest;
    private HoodieModel<AbstractClientPlayer> hoodieClosedWide;
    private HoodieModel<AbstractClientPlayer> hoodieClosedSlim;
    private HoodieModel<AbstractClientPlayer> hoodieOpenWide;
    private HoodieModel<AbstractClientPlayer> hoodieOpenSlim;
    private HoodieModel<AbstractClientPlayer> hoodieClosedWideHat;
    private HoodieModel<AbstractClientPlayer> hoodieClosedSlimHat;
    private HoodieModel<AbstractClientPlayer> hoodieOpenWideHat;
    private HoodieModel<AbstractClientPlayer> hoodieOpenSlimHat;
    private JacketModel<AbstractClientPlayer> jacketClosedWide;
    private JacketModel<AbstractClientPlayer> jacketClosedSlim;
    private JacketModel<AbstractClientPlayer> jacketOpenWide;
    private JacketModel<AbstractClientPlayer> jacketOpenSlim;
    private JacketModel<AbstractClientPlayer> jacketClosedWideHat;
    private JacketModel<AbstractClientPlayer> jacketClosedSlimHat;
    private JacketModel<AbstractClientPlayer> jacketOpenWideHat;
    private JacketModel<AbstractClientPlayer> jacketOpenSlimHat;
    private CoatModel<AbstractClientPlayer> coatWide;
    private CoatModel<AbstractClientPlayer> coatSlim;
    private PantsModel<AbstractClientPlayer> pants;
    private ShortsModel<AbstractClientPlayer> shorts;
    private SneakersModel<AbstractClientPlayer> sneakers;
    private ShoesModel<AbstractClientPlayer> shoes;
    private HatModel<AbstractClientPlayer> hat;
    private CowboyHatModel<AbstractClientPlayer> cowboyHat;
    private StrawHatModel<AbstractClientPlayer> strawHat;
    private CapModel<AbstractClientPlayer> cap;
    private HelmetModel<AbstractClientPlayer> helmet;
    private ScrapHelmetModel<AbstractClientPlayer> scrapHelmet;
    private ScrapChestModel<AbstractClientPlayer> scrapChest;
    private ScrapPantsModel<AbstractClientPlayer> scrapPants;
    private GhillieHoodModel<AbstractClientPlayer> ghillieHood;
    private GhillieTopModel<AbstractClientPlayer> ghillieTopWide;
    private GhillieTopModel<AbstractClientPlayer> ghillieTopSlim;
    private GhilliePantsModel<AbstractClientPlayer> ghilliePants;
    private GhillieBootsModel<AbstractClientPlayer> ghillieBoots;

    private static final ResourceLocation GHILLIE_TEX =
            new ResourceLocation(Fashion.MODID, "textures/models/ghillie.png");
    private static final ResourceLocation SCRAP_TEX =
            new ResourceLocation(Fashion.MODID, "textures/models/scrap_armor.png");

    public ClothingRenderLayer(
            RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            AbstractClientPlayer player,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch) {

        if (ClothingRenderSkip.localFirstPersonWorld(player)) {
            return;
        }

        boolean useSlim = "slim".equals(player.getModelName());
        boolean hideHead = ClothingRenderSkip.hideHeadGarments(player);

        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!hideHead && head.getItem() instanceof ClothingItem headWear) {
            if (headWear.getClothingType() == ClothingType.HAT) {
                renderHat(poseStack, buffer, light, player, headWear);
            } else if (headWear.getClothingType() == ClothingType.COWBOY_HAT) {
                renderCowboyHat(poseStack, buffer, light, headWear);
            } else if (headWear.getClothingType() == ClothingType.STRAW_HAT) {
                renderStrawHat(poseStack, buffer, light);
            } else if (headWear.getClothingType() == ClothingType.CAP) {
                renderCap(poseStack, buffer, light, headWear);
            } else if (headWear.getClothingType() == ClothingType.HELMET) {
                renderHelmet(poseStack, buffer, light);
            } else if (headWear.getClothingType() == ClothingType.SCRAP_HELMET) {
                renderScrapHelmet(poseStack, buffer, light);
            } else if (headWear.getClothingType() == ClothingType.GHILLIE_HOOD) {
                renderGhillieHood(poseStack, buffer, light, player);
            }
        }

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack outerwear = CuriosClothing.getRenderedOuterwear(player);
        boolean ghillieTop = GhillieCover.top(player);
        // T-shirt under Curios outerwear — skip under ghillie top (camo covers torso).
        if (!ghillieTop && chest.getItem() instanceof ClothingItem under
                && under.getClothingType().isChestShirt()
                && under.getClothingType() != ClothingType.GHILLIE_TOP) {
            renderChest(poseStack, buffer, light, player, under, useSlim);
        }
        // Ghillie top is vanilla CHEST armor (not Curios outerwear).
        if (ghillieTop && chest.getItem() instanceof ClothingItem chestWear) {
            renderChest(poseStack, buffer, light, player, chestWear, useSlim);
        }
        // Outerwear / legacy hoodie stay hidden under ghillie camouflage.
        // Curios visibility toggle off → getRendered* is empty (item stays equipped).
        if (!ghillieTop && !GhillieCover.hidesExtras(player)) {
            if (!outerwear.isEmpty() && outerwear.getItem() instanceof ClothingItem outer) {
                renderChest(poseStack, buffer, light, player, outer, useSlim);
            } else if (chest.getItem() instanceof ClothingItem legacy
                    && legacy.getClothingType().isOuterwear()) {
                renderChest(poseStack, buffer, light, player, legacy, useSlim);
            }
            ItemStack vestStack = CuriosClothing.getRenderedVest(player);
            if (!vestStack.isEmpty() && vestStack.getItem() instanceof ClothingItem vestWear) {
                renderChest(poseStack, buffer, light, player, vestWear, useSlim);
            }
        }

        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        if (legs.getItem() instanceof ClothingItem legWear) {
            if (legWear.getClothingType() == ClothingType.PANTS) {
                renderPants(poseStack, buffer, light, player, legWear);
            } else if (legWear.getClothingType() == ClothingType.SHORTS) {
                renderShorts(poseStack, buffer, light, legWear);
            } else if (legWear.getClothingType() == ClothingType.GHILLIE_PANTS) {
                renderGhilliePants(poseStack, buffer, light, player);
            }
        }
        if (!GhillieCover.hidesExtras(player)) {
            ItemStack scrapPantsStack = CuriosClothing.getRenderedPants(player);
            if (!scrapPantsStack.isEmpty() && scrapPantsStack.getItem() instanceof ClothingItem scrapLeg
                    && scrapLeg.getClothingType() == ClothingType.SCRAP_PANTS) {
                renderScrapPants(poseStack, buffer, light);
            }
        }

        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        if (feet.getItem() instanceof ClothingItem shoes) {
            if (shoes.getClothingType() == ClothingType.SNEAKERS) {
                renderSneakers(poseStack, buffer, light, shoes);
            } else if (shoes.getClothingType() == ClothingType.SHOES) {
                renderShoes(poseStack, buffer, light, shoes);
            } else if (shoes.getClothingType() == ClothingType.GHILLIE_BOOTS) {
                renderGhillieBoots(poseStack, buffer, light, player);
            }
        }
    }

    private void renderHat(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            AbstractClientPlayer player,
            ClothingItem clothing) {
        HatModel<AbstractClientPlayer> model = hatModel();
        getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.head.visible = true;
        ItemStack hooded = CuriosClothing.hoodedPiece(player);
        boolean hoodUp = !hooded.isEmpty() && ClothingItem.isHoodOpen(hooded);
        model.setKnobVisible(!hoodUp);
        drawClothing(poseStack, buffer, light, model, hatTexture(clothing), true);
    }

    private void renderCowboyHat(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            ClothingItem clothing) {
        CowboyHatModel<AbstractClientPlayer> model = cowboyHatModel();
        getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.head.visible = true;
        // No cull: thin brim discs must stay visible from both sides.
        drawClothing(poseStack, buffer, light, model, cowboyHatTexture(clothing), false);
    }

    private void renderStrawHat(PoseStack poseStack, MultiBufferSource buffer, int light) {
        StrawHatModel<AbstractClientPlayer> model = strawHatModel();
        getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.head.visible = true;
        drawClothing(poseStack, buffer, light, model,
                new ResourceLocation(Fashion.MODID, "textures/models/straw_hat.png"), false);
    }

    private void renderCap(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            ClothingItem clothing) {
        CapModel<AbstractClientPlayer> model = capModel();
        getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.head.visible = true;
        drawClothing(poseStack, buffer, light, model, capTexture(clothing), true);
    }

    private void renderHelmet(PoseStack poseStack, MultiBufferSource buffer, int light) {
        HelmetModel<AbstractClientPlayer> model = helmetModel();
        getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.head.visible = true;
        drawClothing(poseStack, buffer, light, model,
                new ResourceLocation(Fashion.MODID, "textures/models/helmet.png"), true);
    }

    private void renderScrapHelmet(PoseStack poseStack, MultiBufferSource buffer, int light) {
        ScrapHelmetModel<AbstractClientPlayer> model = scrapHelmetModel();
        getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.head.visible = true;
        drawClothing(poseStack, buffer, light, model, SCRAP_TEX, true);
    }

    private void renderScrapPants(PoseStack poseStack, MultiBufferSource buffer, int light) {
        ScrapPantsModel<AbstractClientPlayer> model = scrapPantsModel();
        getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.leftLeg.visible = true;
        model.rightLeg.visible = true;
        drawClothing(poseStack, buffer, light, model, SCRAP_TEX, true);
    }

    private void renderChest(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            AbstractClientPlayer player,
            ClothingItem clothing,
            boolean useSlim) {
        HumanoidModel<AbstractClientPlayer> model;
        ResourceLocation tex;
        ClothingType type = clothing.getClothingType();

        if (type == ClothingType.COAT) {
            CoatModel<AbstractClientPlayer> coat = useSlim ? coatSlimModel() : coatWideModel();
            ResourceLocation coatTex = coatTexture(clothing);
            coat.setSuppressArmorPass(false);
            getParentModel().copyPropertiesTo(coat);
            coat.setAllVisible(false);
            // Solid cubes — cull OK. Alpha slit in the PNG opens the front.
            coat.body.visible = true;
            coat.leftArm.visible = true;
            coat.rightArm.visible = true;
            coat.leftLeg.visible = true;
            coat.rightLeg.visible = true;
            coat.head.visible = true;
            drawClothing(poseStack, buffer, light, coat, coatTex, true);
            return;
        }

        if (type == ClothingType.GHILLIE_TOP) {
            renderGhillieTop(poseStack, buffer, light, player, useSlim);
            return;
        }

        if (type == ClothingType.TANK_TOP) {
            TankTopModel<AbstractClientPlayer> tank = tankTopModel();
            getParentModel().copyPropertiesTo(tank);
            tank.setAllVisible(false);
            tank.body.visible = true;
            drawClothing(poseStack, buffer, light, tank, tankTopTexture(clothing), true);
            return;
        }

        if (type == ClothingType.VEST) {
            VestModel<AbstractClientPlayer> vestModel = vestModel();
            vestModel.setSuppressArmorPass(false);
            getParentModel().copyPropertiesTo(vestModel);
            vestModel.setAllVisible(false);
            vestModel.body.visible = true;
            drawClothing(poseStack, buffer, light, vestModel, vestTexture(clothing), true);
            return;
        }

        if (type == ClothingType.SCRAP_VEST) {
            ScrapChestModel<AbstractClientPlayer> scrap = scrapChestModel();
            getParentModel().copyPropertiesTo(scrap);
            scrap.setAllVisible(false);
            scrap.body.visible = true;
            scrap.leftArm.visible = true;
            scrap.rightArm.visible = true;
            drawClothing(poseStack, buffer, light, scrap, SCRAP_TEX, true);
            return;
        }

        if (type.hasHood()) {
            ItemStack hooded = CuriosClothing.hoodedPiece(player);
            boolean open = !hooded.isEmpty() && ClothingItem.isHoodOpen(hooded);
            boolean overHat = wearingFashionHat(player);
            model = type == ClothingType.JACKET
                    ? jacketModel(useSlim, open, overHat)
                    : hoodieModel(useSlim, open, overHat);
            tex = type == ClothingType.JACKET ? jacketTexture(clothing) : hoodieTexture(clothing);
            if (model instanceof HoodieModel<?> hoodie) {
                hoodie.setSuppressArmorPass(false);
            } else if (model instanceof JacketModel<?> jacket) {
                jacket.setSuppressArmorPass(false);
            }
            getParentModel().copyPropertiesTo(model);
            // Body/arms: cull hides dark cube seams. Hood: no-cull so F5 shows the lining.
            model.setAllVisible(false);
            model.body.visible = true;
            model.leftArm.visible = true;
            model.rightArm.visible = true;
            drawClothing(poseStack, buffer, light, model, tex, true);
            model.body.visible = false;
            model.leftArm.visible = false;
            model.rightArm.visible = false;
            // Hood head mesh clips the camera in Features RFP hide-head.
            if (!ClothingRenderSkip.hideHeadGarments(player)) {
                model.head.visible = true;
                drawClothing(poseStack, buffer, light, model, tex, false);
            }
        } else {
            model = useSlim ? tshirtSlimModel() : tshirtWideModel();
            tex = tshirtTexture(clothing);
            getParentModel().copyPropertiesTo(model);
            model.setAllVisible(false);
            model.body.visible = true;
            model.leftArm.visible = true;
            model.rightArm.visible = true;
            drawClothing(poseStack, buffer, light, model, tex, true);
        }
    }

    private void renderPants(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            AbstractClientPlayer player,
            ClothingItem clothing) {
        PantsModel<AbstractClientPlayer> model = pantsModel();
        getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.leftLeg.visible = true;
        model.rightLeg.visible = true;
        drawClothing(poseStack, buffer, light, model, pantsTexture(clothing), true);
    }

    private void renderShorts(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            ClothingItem clothing) {
        ShortsModel<AbstractClientPlayer> model = shortsModel();
        getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.leftLeg.visible = true;
        model.rightLeg.visible = true;
        drawClothing(poseStack, buffer, light, model, shortsTexture(clothing), true);
    }

    private void renderSneakers(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            ClothingItem clothing) {
        SneakersModel<AbstractClientPlayer> model = sneakersModel();
        getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.leftLeg.visible = true;
        model.rightLeg.visible = true;
        drawClothing(poseStack, buffer, light, model, sneakersTexture(clothing), true);
    }

    private void renderShoes(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            ClothingItem clothing) {
        ShoesModel<AbstractClientPlayer> model = shoesModel();
        getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.leftLeg.visible = true;
        model.rightLeg.visible = true;
        drawClothing(poseStack, buffer, light, model, shoesTexture(clothing), true);
    }

    /**
     * {@code cull=true} hides dark inner edges where inflated arm/body cubes
     * intersect. Hood uses {@code cull=false} so the lining stays visible in F5.
     */
    private void renderGhillieHood(
            PoseStack poseStack, MultiBufferSource buffer, int light, AbstractClientPlayer player) {
        GhillieHoodModel<AbstractClientPlayer> model = ghillieHoodModel();
        getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.head.visible = true;
        drawGhillie(poseStack, buffer, light, player, model);
    }

    private void renderGhillieTop(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            AbstractClientPlayer player,
            boolean useSlim) {
        GhillieTopModel<AbstractClientPlayer> model =
                useSlim ? ghillieTopSlimModel() : ghillieTopWideModel();
        getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.body.visible = true;
        model.leftArm.visible = true;
        model.rightArm.visible = true;
        drawGhillie(poseStack, buffer, light, player, model);
    }

    private void renderGhilliePants(
            PoseStack poseStack, MultiBufferSource buffer, int light, AbstractClientPlayer player) {
        GhilliePantsModel<AbstractClientPlayer> model = ghilliePantsModel();
        getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.leftLeg.visible = true;
        model.rightLeg.visible = true;
        drawGhillie(poseStack, buffer, light, player, model);
    }

    private void renderGhillieBoots(
            PoseStack poseStack, MultiBufferSource buffer, int light, AbstractClientPlayer player) {
        GhillieBootsModel<AbstractClientPlayer> model = ghillieBootsModel();
        getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.leftLeg.visible = true;
        model.rightLeg.visible = true;
        drawGhillie(poseStack, buffer, light, player, model);
    }

    private static void drawGhillie(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            AbstractClientPlayer player,
            HumanoidModel<AbstractClientPlayer> model) {
        int rgb = GhillieColors.foliage(player);
        // No cull — foliage flaps. Flat normals kill diffuse flicker on thin planes.
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

    private static void drawClothing(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            HumanoidModel<AbstractClientPlayer> model,
            ResourceLocation tex,
            boolean cull) {
        RenderType renderType = cull
                ? RenderType.entityCutout(tex)
                : RenderType.entityCutoutNoCull(tex);
        VertexConsumer vc = buffer.getBuffer(renderType);
        model.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private static ResourceLocation tshirtTexture(ClothingItem clothing) {
        TShirtColor color = clothing.getTShirtColor();
        if (color != null) {
            return new ResourceLocation(Fashion.MODID, color.modelTexturePath());
        }
        return new ResourceLocation(Fashion.MODID, "textures/models/red_tshirt.png");
    }

    private static ResourceLocation tankTopTexture(ClothingItem clothing) {
        TShirtColor color = clothing.getColor();
        if (color != null && color.hasTankTop()) {
            return new ResourceLocation(Fashion.MODID, color.tankTopModelTexturePath());
        }
        return new ResourceLocation(Fashion.MODID, "textures/models/white_tank_top.png");
    }

    private static ResourceLocation vestTexture(ClothingItem clothing) {
        TShirtColor color = clothing.getColor();
        if (color != null && color.hasVest()) {
            return new ResourceLocation(Fashion.MODID, color.vestModelTexturePath());
        }
        return new ResourceLocation(Fashion.MODID, "textures/models/black_vest.png");
    }

    private static ResourceLocation hoodieTexture(ClothingItem clothing) {
        TShirtColor color = clothing.getColor();
        if (color != null) {
            return new ResourceLocation(Fashion.MODID, color.hoodieModelTexturePath());
        }
        return new ResourceLocation(Fashion.MODID, "textures/models/red_hoodie.png");
    }

    private static ResourceLocation jacketTexture(ClothingItem clothing) {
        TShirtColor color = clothing.getColor();
        if (color != null) {
            return new ResourceLocation(Fashion.MODID, color.jacketModelTexturePath());
        }
        return new ResourceLocation(Fashion.MODID, "textures/models/red_jacket.png");
    }

    private static ResourceLocation coatTexture(ClothingItem clothing) {
        TShirtColor color = clothing.getColor();
        if (color != null) {
            return new ResourceLocation(Fashion.MODID, color.coatModelTexturePath());
        }
        return new ResourceLocation(Fashion.MODID, "textures/models/brown_coat.png");
    }

    private static ResourceLocation pantsTexture(ClothingItem clothing) {
        TShirtColor color = clothing.getColor();
        if (color != null) {
            return new ResourceLocation(Fashion.MODID, color.pantsModelTexturePath());
        }
        return new ResourceLocation(Fashion.MODID, "textures/models/red_pants.png");
    }

    private static ResourceLocation shortsTexture(ClothingItem clothing) {
        TShirtColor color = clothing.getColor();
        if (color != null) {
            return new ResourceLocation(Fashion.MODID, color.shortsModelTexturePath());
        }
        return new ResourceLocation(Fashion.MODID, "textures/models/red_shorts.png");
    }

    private static ResourceLocation sneakersTexture(ClothingItem clothing) {
        TShirtColor color = clothing.getColor();
        if (color != null) {
            return new ResourceLocation(Fashion.MODID, color.sneakersModelTexturePath());
        }
        return new ResourceLocation(Fashion.MODID, "textures/models/red_sneakers.png");
    }

    private static ResourceLocation shoesTexture(ClothingItem clothing) {
        TShirtColor color = clothing.getColor();
        if (color != null && color.hasShoes()) {
            return new ResourceLocation(Fashion.MODID, color.shoesModelTexturePath());
        }
        return new ResourceLocation(Fashion.MODID, "textures/models/brown_shoes.png");
    }

    private static ResourceLocation hatTexture(ClothingItem clothing) {
        TShirtColor color = clothing.getColor();
        if (color != null) {
            return new ResourceLocation(Fashion.MODID, color.hatModelTexturePath());
        }
        return new ResourceLocation(Fashion.MODID, "textures/models/red_hat.png");
    }

    private static ResourceLocation cowboyHatTexture(ClothingItem clothing) {
        TShirtColor color = clothing.getColor();
        if (color != null && color.hasCowboyHat()) {
            return new ResourceLocation(Fashion.MODID, color.cowboyHatModelTexturePath());
        }
        return new ResourceLocation(Fashion.MODID, "textures/models/brown_cowboy_hat.png");
    }

    private static ResourceLocation capTexture(ClothingItem clothing) {
        TShirtColor color = clothing.getColor();
        if (color != null) {
            return new ResourceLocation(Fashion.MODID, color.capModelTexturePath());
        }
        return new ResourceLocation(Fashion.MODID, "textures/models/red_cap.png");
    }

    private TShirtModel<AbstractClientPlayer> tshirtWideModel() {
        if (tshirtWide == null) {
            tshirtWide = new TShirtModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(TShirtModel.LAYER_WIDE));
        }
        return tshirtWide;
    }

    private TShirtModel<AbstractClientPlayer> tshirtSlimModel() {
        if (tshirtSlim == null) {
            tshirtSlim = new TShirtModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(TShirtModel.LAYER_SLIM));
        }
        return tshirtSlim;
    }

    private TankTopModel<AbstractClientPlayer> tankTopModel() {
        if (tankTop == null) {
            tankTop = new TankTopModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(TankTopModel.LAYER));
        }
        return tankTop;
    }

    private VestModel<AbstractClientPlayer> vestModel() {
        if (vest == null) {
            vest = new VestModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(VestModel.LAYER));
        }
        return vest;
    }

    private static boolean wearingFashionHat(AbstractClientPlayer player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        return head.getItem() instanceof ClothingItem wear && wear.getClothingType().isHelmet();
    }

    private HoodieModel<AbstractClientPlayer> hoodieModel(boolean slim, boolean open, boolean overHat) {
        if (overHat) {
            if (open) {
                if (slim) {
                    if (hoodieOpenSlimHat == null) {
                        hoodieOpenSlimHat = new HoodieModel<>(
                                Minecraft.getInstance().getEntityModels().bakeLayer(HoodieModel.LAYER_OPEN_SLIM_HAT));
                    }
                    return hoodieOpenSlimHat;
                }
                if (hoodieOpenWideHat == null) {
                    hoodieOpenWideHat = new HoodieModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(HoodieModel.LAYER_OPEN_WIDE_HAT));
                }
                return hoodieOpenWideHat;
            }
            if (slim) {
                if (hoodieClosedSlimHat == null) {
                    hoodieClosedSlimHat = new HoodieModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(HoodieModel.LAYER_CLOSED_SLIM_HAT));
                }
                return hoodieClosedSlimHat;
            }
            if (hoodieClosedWideHat == null) {
                hoodieClosedWideHat = new HoodieModel<>(
                        Minecraft.getInstance().getEntityModels().bakeLayer(HoodieModel.LAYER_CLOSED_WIDE_HAT));
            }
            return hoodieClosedWideHat;
        }
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

    private JacketModel<AbstractClientPlayer> jacketModel(boolean slim, boolean open, boolean overHat) {
        if (overHat) {
            if (open) {
                if (slim) {
                    if (jacketOpenSlimHat == null) {
                        jacketOpenSlimHat = new JacketModel<>(
                                Minecraft.getInstance().getEntityModels().bakeLayer(JacketModel.LAYER_OPEN_SLIM_HAT));
                    }
                    return jacketOpenSlimHat;
                }
                if (jacketOpenWideHat == null) {
                    jacketOpenWideHat = new JacketModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(JacketModel.LAYER_OPEN_WIDE_HAT));
                }
                return jacketOpenWideHat;
            }
            if (slim) {
                if (jacketClosedSlimHat == null) {
                    jacketClosedSlimHat = new JacketModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(JacketModel.LAYER_CLOSED_SLIM_HAT));
                }
                return jacketClosedSlimHat;
            }
            if (jacketClosedWideHat == null) {
                jacketClosedWideHat = new JacketModel<>(
                        Minecraft.getInstance().getEntityModels().bakeLayer(JacketModel.LAYER_CLOSED_WIDE_HAT));
            }
            return jacketClosedWideHat;
        }
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

    private CoatModel<AbstractClientPlayer> coatWideModel() {
        if (coatWide == null) {
            coatWide = new CoatModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(CoatModel.LAYER_WIDE));
        }
        return coatWide;
    }

    private CoatModel<AbstractClientPlayer> coatSlimModel() {
        if (coatSlim == null) {
            coatSlim = new CoatModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(CoatModel.LAYER_SLIM));
        }
        return coatSlim;
    }

    private PantsModel<AbstractClientPlayer> pantsModel() {
        if (pants == null) {
            pants = new PantsModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(PantsModel.LAYER));
        }
        return pants;
    }

    private ShortsModel<AbstractClientPlayer> shortsModel() {
        if (shorts == null) {
            shorts = new ShortsModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ShortsModel.LAYER));
        }
        return shorts;
    }

    private SneakersModel<AbstractClientPlayer> sneakersModel() {
        if (sneakers == null) {
            sneakers = new SneakersModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(SneakersModel.LAYER));
        }
        return sneakers;
    }

    private ShoesModel<AbstractClientPlayer> shoesModel() {
        if (shoes == null) {
            shoes = new ShoesModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ShoesModel.LAYER));
        }
        return shoes;
    }

    private HatModel<AbstractClientPlayer> hatModel() {
        if (hat == null) {
            hat = new HatModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(HatModel.LAYER));
        }
        return hat;
    }

    private CowboyHatModel<AbstractClientPlayer> cowboyHatModel() {
        if (cowboyHat == null) {
            cowboyHat = new CowboyHatModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(CowboyHatModel.LAYER));
        }
        return cowboyHat;
    }

    private StrawHatModel<AbstractClientPlayer> strawHatModel() {
        if (strawHat == null) {
            strawHat = new StrawHatModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(StrawHatModel.LAYER));
        }
        return strawHat;
    }

    private CapModel<AbstractClientPlayer> capModel() {
        if (cap == null) {
            cap = new CapModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(CapModel.LAYER));
        }
        return cap;
    }

    private HelmetModel<AbstractClientPlayer> helmetModel() {
        if (helmet == null) {
            helmet = new HelmetModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(HelmetModel.LAYER));
        }
        return helmet;
    }

    private ScrapHelmetModel<AbstractClientPlayer> scrapHelmetModel() {
        if (scrapHelmet == null) {
            scrapHelmet = new ScrapHelmetModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ScrapHelmetModel.LAYER));
        }
        return scrapHelmet;
    }

    private ScrapChestModel<AbstractClientPlayer> scrapChestModel() {
        if (scrapChest == null) {
            scrapChest = new ScrapChestModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ScrapChestModel.LAYER));
        }
        return scrapChest;
    }

    private ScrapPantsModel<AbstractClientPlayer> scrapPantsModel() {
        if (scrapPants == null) {
            scrapPants = new ScrapPantsModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ScrapPantsModel.LAYER));
        }
        return scrapPants;
    }

    private GhillieHoodModel<AbstractClientPlayer> ghillieHoodModel() {
        if (ghillieHood == null) {
            ghillieHood = new GhillieHoodModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(GhillieHoodModel.LAYER));
        }
        return ghillieHood;
    }

    private GhillieTopModel<AbstractClientPlayer> ghillieTopWideModel() {
        if (ghillieTopWide == null) {
            ghillieTopWide = new GhillieTopModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(GhillieTopModel.LAYER_WIDE));
        }
        return ghillieTopWide;
    }

    private GhillieTopModel<AbstractClientPlayer> ghillieTopSlimModel() {
        if (ghillieTopSlim == null) {
            ghillieTopSlim = new GhillieTopModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(GhillieTopModel.LAYER_SLIM));
        }
        return ghillieTopSlim;
    }

    private GhilliePantsModel<AbstractClientPlayer> ghilliePantsModel() {
        if (ghilliePants == null) {
            ghilliePants = new GhilliePantsModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(GhilliePantsModel.LAYER));
        }
        return ghilliePants;
    }

    private GhillieBootsModel<AbstractClientPlayer> ghillieBootsModel() {
        if (ghillieBoots == null) {
            ghillieBoots = new GhillieBootsModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(GhillieBootsModel.LAYER));
        }
        return ghillieBoots;
    }
}
