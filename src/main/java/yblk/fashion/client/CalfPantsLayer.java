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
import yblk.fashion.client.model.CalfPantsModel;
import yblk.fashion.client.model.GhillieBootsModel;
import yblk.fashion.client.model.GhilliePantsModel;
import yblk.fashion.client.model.PantsModel;
import yblk.fashion.client.model.ShoesModel;
import yblk.fashion.client.model.SneakersModel;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClothingType;

/**
 * After pants hide the full outer pant layers, redraw only the calves from the
 * player skin so the cut follows the pants hem (not a blank gap / full outer box).
 * With footwear, stop at the cuff when the shoe sits below the pant hem.
 */
public class CalfPantsLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private CalfPantsModel<AbstractClientPlayer> full;
    private CalfPantsModel<AbstractClientPlayer> toShoe;
    private CalfPantsModel<AbstractClientPlayer> toBoots;

    public CalfPantsLayer(
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
        if (!(legs.getItem() instanceof ClothingItem clothing)) {
            return;
        }
        ClothingType legType = clothing.getClothingType();
        if (legType != ClothingType.PANTS && legType != ClothingType.GHILLIE_PANTS) {
            return;
        }

        float pantHem = legType == ClothingType.GHILLIE_PANTS
                ? GhilliePantsModel.LEG_HEIGHT
                : PantsModel.LEG_HEIGHT;

        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        CalfPantsModel<AbstractClientPlayer> calf = fullModel();
        if (feet.getItem() instanceof ClothingItem shoes) {
            ClothingType foot = shoes.getClothingType();
            if (foot == ClothingType.GHILLIE_BOOTS) {
                // Ghillie pant hem flush with boot top.
                if (GhillieBootsModel.BOOT_TOP_Y <= pantHem + 0.001F) {
                    return;
                }
                calf = toShoeModel();
            } else if (foot == ClothingType.SNEAKERS) {
                // Pant hem flush with sneaker top — nothing to redraw between them.
                if (SneakersModel.SHOE_TOP_Y <= pantHem + 0.001F) {
                    return;
                }
                calf = toShoeModel();
            } else if (foot == ClothingType.SHOES) {
                if (ShoesModel.SHOE_TOP_Y <= pantHem + 0.001F) {
                    return;
                }
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

    private CalfPantsModel<AbstractClientPlayer> fullModel() {
        if (full == null) {
            full = new CalfPantsModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(CalfPantsModel.LAYER));
        }
        return full;
    }

    private CalfPantsModel<AbstractClientPlayer> toShoeModel() {
        if (toShoe == null) {
            toShoe = new CalfPantsModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(CalfPantsModel.LAYER_TO_SHOE));
        }
        return toShoe;
    }

    private CalfPantsModel<AbstractClientPlayer> toBootsModel() {
        if (toBoots == null) {
            toBoots = new CalfPantsModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(CalfPantsModel.LAYER_TO_BOOTS));
        }
        return toBoots;
    }
}
