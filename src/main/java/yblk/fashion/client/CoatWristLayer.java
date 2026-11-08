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
import yblk.fashion.client.model.CoatWristModel;
import yblk.fashion.compat.CuriosClothing;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClothingType;

/**
 * After a coat hides the full outer sleeves, redraw only the wrists from the
 * player skin so the cut follows the sleeve hem.
 */
public class CoatWristLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private CoatWristModel<AbstractClientPlayer> wide;
    private CoatWristModel<AbstractClientPlayer> slim;

    public CoatWristLayer(
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
        if (GhillieCover.hidesExtras(player)) {
            return;
        }
        if (!wearingCoat(player)) {
            return;
        }

        boolean useSlim = "slim".equals(player.getModelName());
        CoatWristModel<AbstractClientPlayer> model = useSlim ? slimModel() : wideModel();

        getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.leftArm.visible = true;
        model.rightArm.visible = true;

        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(player.getSkinTextureLocation()));
        model.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private static boolean wearingCoat(AbstractClientPlayer player) {
        ItemStack outer = CuriosClothing.getRenderedOuterwear(player);
        if (outer.getItem() instanceof ClothingItem c && c.getClothingType() == ClothingType.COAT) {
            return true;
        }
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        return chest.getItem() instanceof ClothingItem c && c.getClothingType() == ClothingType.COAT;
    }

    private CoatWristModel<AbstractClientPlayer> wideModel() {
        if (wide == null) {
            wide = new CoatWristModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(CoatWristModel.LAYER_WIDE));
        }
        return wide;
    }

    private CoatWristModel<AbstractClientPlayer> slimModel() {
        if (slim == null) {
            slim = new CoatWristModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(CoatWristModel.LAYER_SLIM));
        }
        return slim;
    }
}
