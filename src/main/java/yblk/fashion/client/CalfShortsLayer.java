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
import yblk.fashion.client.model.CalfShortsModel;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClothingType;

/**
 * After shorts hide the full outer pant layers, redraw calves from the shorts hem.
 * With sneakers, stop at the shoe top so outer skin does not clip through the cuff.
 */
public class CalfShortsLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private CalfShortsModel<AbstractClientPlayer> full;
    private CalfShortsModel<AbstractClientPlayer> toShoe;
    private CalfShortsModel<AbstractClientPlayer> toBoots;

    public CalfShortsLayer(
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

        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        if (!(legs.getItem() instanceof ClothingItem clothing)
                || clothing.getClothingType() != ClothingType.SHORTS) {
            return;
        }

        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        CalfShortsModel<AbstractClientPlayer> calf = fullModel();
        if (feet.getItem() instanceof ClothingItem shoes) {
            ClothingType foot = shoes.getClothingType();
            if (foot == ClothingType.SNEAKERS) {
                calf = toShoeModel();
            } else if (foot == ClothingType.SHOES) {
                calf = toBootsModel();
            }
        }
        getParentModel().copyPropertiesTo(calf);
        calf.setAllVisible(false);
        calf.leftLeg.visible = true;
        calf.rightLeg.visible = true;

        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(player.getSkinTextureLocation()));
        calf.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private CalfShortsModel<AbstractClientPlayer> fullModel() {
        if (full == null) {
            full = new CalfShortsModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(CalfShortsModel.LAYER));
        }
        return full;
    }

    private CalfShortsModel<AbstractClientPlayer> toShoeModel() {
        if (toShoe == null) {
            toShoe = new CalfShortsModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(CalfShortsModel.LAYER_TO_SHOE));
        }
        return toShoe;
    }

    private CalfShortsModel<AbstractClientPlayer> toBootsModel() {
        if (toBoots == null) {
            toBoots = new CalfShortsModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(CalfShortsModel.LAYER_TO_BOOTS));
        }
        return toBoots;
    }
}
