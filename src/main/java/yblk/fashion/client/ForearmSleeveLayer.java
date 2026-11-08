package yblk.fashion.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import yblk.fashion.client.model.ForearmSleeveModel;
import yblk.fashion.compat.CuriosClothing;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClothingType;

/**
 * After a t-shirt hides the full outer sleeves, redraw only the forearms from the
 * player skin so watches / bracelets on the 2nd layer stay visible.
 * Skipped when Curios outerwear (or long-sleeve CHEST) covers the arm.
 */
public class ForearmSleeveLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private ForearmSleeveModel<AbstractClientPlayer> wide;
    private ForearmSleeveModel<AbstractClientPlayer> slim;

    public ForearmSleeveLayer(
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

        if (player.isInvisible()) {
            return;
        }
        if (ClothingRenderSkip.localFirstPersonWorld(player)) {
            return;
        }
        if (GhillieCover.top(player)) {
            return;
        }

        if (!needsForearmRedraw(player)) {
            return;
        }

        boolean useSlim = "slim".equals(player.getModelName());
        ForearmSleeveModel<AbstractClientPlayer> model = useSlim ? slimModel() : wideModel();

        getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.leftArm.visible = true;
        model.rightArm.visible = true;

        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(player.getSkinTextureLocation()));
        model.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private static boolean needsForearmRedraw(AbstractClientPlayer player) {
        boolean shortSleeveHide = false;
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack outer = CuriosClothing.getRenderedOuterwear(player);
        if (coversFullArm(outer) || coversFullArm(chest)) {
            return false;
        }
        if (chest.getItem() instanceof ClothingItem clothing) {
            ClothingType type = clothing.getClothingType();
            if (type.hideSleeves && type.sleeveHeight < 10.0F) {
                shortSleeveHide = true;
            }
        }
        return shortSleeveHide;
    }

    private static boolean coversFullArm(ItemStack stack) {
        return stack.getItem() instanceof ClothingItem clothing
                && clothing.getClothingType().hideSleeves
                && clothing.getClothingType().sleeveHeight >= 10.0F;
    }

    private ForearmSleeveModel<AbstractClientPlayer> wideModel() {
        if (wide == null) {
            wide = new ForearmSleeveModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ForearmSleeveModel.LAYER_WIDE));
        }
        return wide;
    }

    private ForearmSleeveModel<AbstractClientPlayer> slimModel() {
        if (slim == null) {
            slim = new ForearmSleeveModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ForearmSleeveModel.LAYER_SLIM));
        }
        return slim;
    }
}
