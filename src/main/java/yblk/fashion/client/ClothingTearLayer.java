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
import yblk.fashion.client.model.HoodieTearModel;
import yblk.fashion.client.model.JacketTearModel;
import yblk.fashion.client.model.PantsTearModel;
import yblk.fashion.client.model.ShortsTearModel;
import yblk.fashion.client.model.TShirtTearModel;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClothingType;
import yblk.fashion.item.TShirtColor;

/**
 * Tear marks on worn clothing. Opacity = damageTaken × 1% (capped at 100%).
 */
public class ClothingTearLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public static final ResourceLocation FALLBACK_TEAR_TEXTURE =
            new ResourceLocation(Fashion.MODID, "textures/models/clothing_tear.png");

    public static final float ALPHA_PER_DAMAGE = 0.01F;

    private TShirtTearModel<AbstractClientPlayer> tshirtWide;
    private TShirtTearModel<AbstractClientPlayer> tshirtSlim;
    private HoodieTearModel<AbstractClientPlayer> hoodieClosedWide;
    private HoodieTearModel<AbstractClientPlayer> hoodieClosedSlim;
    private HoodieTearModel<AbstractClientPlayer> hoodieOpenWide;
    private HoodieTearModel<AbstractClientPlayer> hoodieOpenSlim;
    private JacketTearModel<AbstractClientPlayer> jacketClosedWide;
    private JacketTearModel<AbstractClientPlayer> jacketClosedSlim;
    private JacketTearModel<AbstractClientPlayer> jacketOpenWide;
    private JacketTearModel<AbstractClientPlayer> jacketOpenSlim;
    private PantsTearModel<AbstractClientPlayer> pants;
    private ShortsTearModel<AbstractClientPlayer> shorts;

    public ClothingTearLayer(
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
        if (GhillieCover.hidesExtras(player)) {
            return;
        }

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.getItem() instanceof ClothingItem clothing
                && clothing.getClothingType() == ClothingType.TSHIRT
                && chest.isDamageableItem()) {
            renderChestTears(poseStack, buffer, light, player, chest, clothing);
        }
        ItemStack outerwear = CuriosClothing.getRenderedOuterwear(player);
        if (!outerwear.isEmpty() && outerwear.getItem() instanceof ClothingItem outer
                && outerwear.isDamageableItem()) {
            renderChestTears(poseStack, buffer, light, player, outerwear, outer);
        } else if (chest.getItem() instanceof ClothingItem legacy
                && legacy.getClothingType().isOuterwear()
                && chest.isDamageableItem()) {
            renderChestTears(poseStack, buffer, light, player, chest, legacy);
        }

        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        if (legs.getItem() instanceof ClothingItem legWear && legs.isDamageableItem()) {
            ClothingType legType = legWear.getClothingType();
            if (legType == ClothingType.PANTS) {
                renderPantsTears(poseStack, buffer, light, player, legs, legWear);
            } else if (legType == ClothingType.SHORTS) {
                renderShortsTears(poseStack, buffer, light, player, legs, legWear);
            }
        }
    }

    private void renderPantsTears(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            AbstractClientPlayer player,
            ItemStack legs,
            ClothingItem clothing) {
        int lost = legs.getDamageValue();
        if (lost < 5) {
            return;
        }
        float alpha = Math.min(1.0F, lost * ALPHA_PER_DAMAGE);
        if (alpha < 0.05F) {
            return;
        }

        PantsTearModel<AbstractClientPlayer> model = pantsModel();
        getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.leftLeg.visible = true;
        model.rightLeg.visible = true;

        drawTears(poseStack, buffer, light, model, pantsTearTexture(clothing), alpha, true);
    }

    private void renderShortsTears(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            AbstractClientPlayer player,
            ItemStack legs,
            ClothingItem clothing) {
        int lost = legs.getDamageValue();
        if (lost < 5) {
            return;
        }
        float alpha = Math.min(1.0F, lost * ALPHA_PER_DAMAGE);
        if (alpha < 0.05F) {
            return;
        }

        ShortsTearModel<AbstractClientPlayer> model = shortsModel();
        getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.leftLeg.visible = true;
        model.rightLeg.visible = true;

        drawTears(poseStack, buffer, light, model, shortsTearTexture(clothing), alpha, true);
    }

    private void renderChestTears(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            AbstractClientPlayer player,
            ItemStack chest,
            ClothingItem clothing) {
        int lost = chest.getDamageValue();
        if (lost < 5) {
            return;
        }

        float alpha = Math.min(1.0F, lost * ALPHA_PER_DAMAGE);
        if (alpha < 0.05F) {
            return;
        }

        boolean useSlim = "slim".equals(player.getModelName());
        HumanoidModel<AbstractClientPlayer> model;
        ResourceLocation tearTex;
        ClothingType type = clothing.getClothingType();
        if (type == ClothingType.COAT) {
            // No tear atlas for coats yet.
            return;
        }

        if (type.hasHood()) {
            boolean open = ClothingItem.isHoodOpen(chest);
            model = type == ClothingType.JACKET
                    ? jacketModel(useSlim, open)
                    : hoodieModel(useSlim, open);
            tearTex = type == ClothingType.JACKET ? jacketTearTexture(clothing) : hoodieTearTexture(clothing);
            getParentModel().copyPropertiesTo(model);
            model.setAllVisible(false);
            model.body.visible = true;
            model.leftArm.visible = true;
            model.rightArm.visible = true;
            drawTears(poseStack, buffer, light, model, tearTex, alpha, true);
            model.body.visible = false;
            model.leftArm.visible = false;
            model.rightArm.visible = false;
            if (!ClothingRenderSkip.hideHeadGarments(player)) {
                model.head.visible = true;
                drawTears(poseStack, buffer, light, model, tearTex, alpha, false);
            }
        } else {
            model = useSlim ? tshirtSlimModel() : tshirtWideModel();
            tearTex = tshirtTearTexture(clothing);
            getParentModel().copyPropertiesTo(model);
            model.setAllVisible(false);
            model.body.visible = true;
            model.leftArm.visible = true;
            model.rightArm.visible = true;
            drawTears(poseStack, buffer, light, model, tearTex, alpha, true);
        }
    }

    private static void drawTears(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            HumanoidModel<AbstractClientPlayer> model,
            ResourceLocation tearTex,
            float alpha,
            boolean cull) {
        RenderType renderType = cull
                ? RenderType.entityTranslucentCull(tearTex)
                : RenderType.entityTranslucent(tearTex);
        VertexConsumer vc = buffer.getBuffer(renderType);
        model.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, alpha);
    }

    private static ResourceLocation tshirtTearTexture(ClothingItem clothing) {
        TShirtColor color = clothing.getTShirtColor();
        if (color != null) {
            return new ResourceLocation(Fashion.MODID, color.tearTexturePath());
        }
        return FALLBACK_TEAR_TEXTURE;
    }

    private static ResourceLocation hoodieTearTexture(ClothingItem clothing) {
        TShirtColor color = clothing.getColor();
        if (color != null) {
            return new ResourceLocation(Fashion.MODID, color.hoodieTearTexturePath());
        }
        return FALLBACK_TEAR_TEXTURE;
    }

    private static ResourceLocation jacketTearTexture(ClothingItem clothing) {
        TShirtColor color = clothing.getColor();
        if (color != null) {
            return new ResourceLocation(Fashion.MODID, color.jacketTearTexturePath());
        }
        return FALLBACK_TEAR_TEXTURE;
    }

    private static ResourceLocation pantsTearTexture(ClothingItem clothing) {
        TShirtColor color = clothing.getColor();
        if (color != null) {
            return new ResourceLocation(Fashion.MODID, color.pantsTearTexturePath());
        }
        return FALLBACK_TEAR_TEXTURE;
    }

    private static ResourceLocation shortsTearTexture(ClothingItem clothing) {
        TShirtColor color = clothing.getColor();
        if (color != null) {
            return new ResourceLocation(Fashion.MODID, color.shortsTearTexturePath());
        }
        return FALLBACK_TEAR_TEXTURE;
    }

    private PantsTearModel<AbstractClientPlayer> pantsModel() {
        if (pants == null) {
            pants = new PantsTearModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(PantsTearModel.LAYER));
        }
        return pants;
    }

    private ShortsTearModel<AbstractClientPlayer> shortsModel() {
        if (shorts == null) {
            shorts = new ShortsTearModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ShortsTearModel.LAYER));
        }
        return shorts;
    }

    private TShirtTearModel<AbstractClientPlayer> tshirtWideModel() {
        if (tshirtWide == null) {
            tshirtWide = new TShirtTearModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(TShirtTearModel.LAYER_WIDE));
        }
        return tshirtWide;
    }

    private TShirtTearModel<AbstractClientPlayer> tshirtSlimModel() {
        if (tshirtSlim == null) {
            tshirtSlim = new TShirtTearModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(TShirtTearModel.LAYER_SLIM));
        }
        return tshirtSlim;
    }

    private HoodieTearModel<AbstractClientPlayer> hoodieModel(boolean slim, boolean open) {
        if (open) {
            if (slim) {
                if (hoodieOpenSlim == null) {
                    hoodieOpenSlim = new HoodieTearModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(HoodieTearModel.LAYER_OPEN_SLIM));
                }
                return hoodieOpenSlim;
            }
            if (hoodieOpenWide == null) {
                hoodieOpenWide = new HoodieTearModel<>(
                        Minecraft.getInstance().getEntityModels().bakeLayer(HoodieTearModel.LAYER_OPEN_WIDE));
            }
            return hoodieOpenWide;
        }
        if (slim) {
            if (hoodieClosedSlim == null) {
                hoodieClosedSlim = new HoodieTearModel<>(
                        Minecraft.getInstance().getEntityModels().bakeLayer(HoodieTearModel.LAYER_CLOSED_SLIM));
            }
            return hoodieClosedSlim;
        }
        if (hoodieClosedWide == null) {
            hoodieClosedWide = new HoodieTearModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(HoodieTearModel.LAYER_CLOSED_WIDE));
        }
        return hoodieClosedWide;
    }

    private JacketTearModel<AbstractClientPlayer> jacketModel(boolean slim, boolean open) {
        if (open) {
            if (slim) {
                if (jacketOpenSlim == null) {
                    jacketOpenSlim = new JacketTearModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(JacketTearModel.LAYER_OPEN_SLIM));
                }
                return jacketOpenSlim;
            }
            if (jacketOpenWide == null) {
                jacketOpenWide = new JacketTearModel<>(
                        Minecraft.getInstance().getEntityModels().bakeLayer(JacketTearModel.LAYER_OPEN_WIDE));
            }
            return jacketOpenWide;
        }
        if (slim) {
            if (jacketClosedSlim == null) {
                jacketClosedSlim = new JacketTearModel<>(
                        Minecraft.getInstance().getEntityModels().bakeLayer(JacketTearModel.LAYER_CLOSED_SLIM));
            }
            return jacketClosedSlim;
        }
        if (jacketClosedWide == null) {
            jacketClosedWide = new JacketTearModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(JacketTearModel.LAYER_CLOSED_WIDE));
        }
        return jacketClosedWide;
    }
}