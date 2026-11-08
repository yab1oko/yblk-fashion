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
import yblk.fashion.client.model.ShinOuterModel;
import yblk.fashion.compat.CuriosClothing;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClothingType;

/**
 * After sneakers/shoes hide the full outer pant layers, redraw only the shin
 * above the cuff — unless pants/shorts already cover that zone with fabric.
 */
public class ShinOuterLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private ShinOuterModel<AbstractClientPlayer> sneakers;
    private ShinOuterModel<AbstractClientPlayer> boots;

    public ShinOuterLayer(
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

        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        if (!(feet.getItem() instanceof ClothingItem shoes) || !shoes.getClothingType().isBoots()) {
            return;
        }

        // Coat owns the leg cut (flaps + the band below them).
        ItemStack outer = CuriosClothing.getOuterwear(player);
        if (outer.getItem() instanceof ClothingItem coat && coat.getClothingType() == ClothingType.COAT) {
            return;
        }
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.getItem() instanceof ClothingItem coat && coat.getClothingType() == ClothingType.COAT) {
            return;
        }

        // Pants/shorts/ghillie pants cover the mid-shin band via their own meshes.
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        if (legs.getItem() instanceof ClothingItem legWear) {
            ClothingType type = legWear.getClothingType();
            if (type == ClothingType.PANTS || type == ClothingType.SHORTS || type == ClothingType.GHILLIE_PANTS) {
                return;
            }
        }

        // Ghillie boots own the lower and use ShinOuter only when pants are absent
        // (sneakers-style redraw above cuff).
        ShinOuterModel<AbstractClientPlayer> shin =
                shoes.getClothingType() == ClothingType.SHOES ? bootsModel() : sneakersModel();
        getParentModel().copyPropertiesTo(shin);
        shin.setAllVisible(false);
        shin.leftLeg.visible = true;
        shin.rightLeg.visible = true;

        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(player.getSkinTextureLocation()));
        shin.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private ShinOuterModel<AbstractClientPlayer> sneakersModel() {
        if (sneakers == null) {
            sneakers = new ShinOuterModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ShinOuterModel.LAYER));
        }
        return sneakers;
    }

    private ShinOuterModel<AbstractClientPlayer> bootsModel() {
        if (boots == null) {
            boots = new ShinOuterModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ShinOuterModel.LAYER_SHOES));
        }
        return boots;
    }
}
