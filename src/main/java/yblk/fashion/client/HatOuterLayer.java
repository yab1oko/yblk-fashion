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
import yblk.fashion.client.model.HatOuterModel;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClothingType;

/**
 * After a fashion hat hides the full skin hat layer, redraw only the front band
 * below the brim hem so bangs/face stay visible (HoodFront-style).
 */
public class HatOuterLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private HatOuterModel<AbstractClientPlayer> beanie;
    private HatOuterModel<AbstractClientPlayer> cowboy;
    private HatOuterModel<AbstractClientPlayer> straw;

    public HatOuterLayer(
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

        // Balaclava covers the whole head — no bangs strip under eye holes.
        if (ClothingLayerHider.wearingBalaclava(player)) {
            return;
        }

        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!(head.getItem() instanceof ClothingItem wear) || !wear.getClothingType().hideHat) {
            return;
        }
        // Full-head ghillie shell — no bangs strip under the mesh.
        if (wear.getClothingType() == ClothingType.GHILLIE_HOOD) {
            return;
        }

        HatOuterModel<AbstractClientPlayer> outer = switch (wear.getClothingType()) {
            case COWBOY_HAT -> cowboyModel();
            case STRAW_HAT -> strawModel();
            default -> beanieModel();
        };
        getParentModel().copyPropertiesTo(outer);
        outer.setAllVisible(false);
        outer.head.visible = true;

        // Cull: each wall's inward twin face would otherwise z-fight with the outer one.
        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucentCull(player.getSkinTextureLocation()));
        outer.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private HatOuterModel<AbstractClientPlayer> beanieModel() {
        if (beanie == null) {
            beanie = new HatOuterModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(HatOuterModel.LAYER));
        }
        return beanie;
    }

    private HatOuterModel<AbstractClientPlayer> cowboyModel() {
        if (cowboy == null) {
            cowboy = new HatOuterModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(HatOuterModel.LAYER_COWBOY));
        }
        return cowboy;
    }

    private HatOuterModel<AbstractClientPlayer> strawModel() {
        if (straw == null) {
            straw = new HatOuterModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(HatOuterModel.LAYER_STRAW));
        }
        return straw;
    }
}
