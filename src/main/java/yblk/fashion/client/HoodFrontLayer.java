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
import yblk.fashion.client.model.HoodFrontModel;
import yblk.fashion.compat.CuriosClothing;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClothingType;

/**
 * With an open hoodie the hat layer is hidden under the hood; redraw only the
 * front half of that layer so the face/bangs aren't cut off.
 */
public class HoodFrontLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private HoodFrontModel<AbstractClientPlayer> model;

    public HoodFrontLayer(
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
        // Balaclava already covers the face — do not redraw hat outer through it.
        if (ClothingLayerHider.wearingBalaclava(player)) {
            return;
        }

        // Cap: hat layer stays for occlusion. Hat: HatOuterLayer redraws bangs.
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        if (head.getItem() instanceof ClothingItem wear && wear.getClothingType().isHelmet()) {
            return;
        }

        ItemStack hooded = CuriosClothing.hoodedPiece(player);
        if (!(hooded.getItem() instanceof ClothingItem clothing)
                || !clothing.getClothingType().hasHood()
                || !ClothingItem.isHoodOpen(hooded)) {
            return;
        }

        HoodFrontModel<AbstractClientPlayer> front = model();
        getParentModel().copyPropertiesTo(front);
        front.setAllVisible(false);
        front.head.visible = true;

        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(player.getSkinTextureLocation()));
        front.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private HoodFrontModel<AbstractClientPlayer> model() {
        if (model == null) {
            model = new HoodFrontModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(HoodFrontModel.LAYER));
        }
        return model;
    }
}
