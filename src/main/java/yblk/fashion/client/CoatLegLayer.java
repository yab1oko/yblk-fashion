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
import yblk.fashion.client.model.CoatLegModel;
import yblk.fashion.compat.CuriosClothing;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClothingType;

/**
 * After a coat hides pant outer layers under the flaps, redraw legs below the
 * flap hem. Skipped when fashion pants/shorts already own that cut.
 */
public class CoatLegLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private CoatLegModel<AbstractClientPlayer> full;
    private CoatLegModel<AbstractClientPlayer> toShoe;
    private CoatLegModel<AbstractClientPlayer> toBoots;

    public CoatLegLayer(
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
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        if (legs.getItem() instanceof ClothingItem legWear && legWear.getClothingType().isLeggings()) {
            return;
        }

        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        CoatLegModel<AbstractClientPlayer> calf = fullModel();
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

    private static boolean wearingCoat(AbstractClientPlayer player) {
        ItemStack outer = CuriosClothing.getRenderedOuterwear(player);
        if (outer.getItem() instanceof ClothingItem c && c.getClothingType() == ClothingType.COAT) {
            return true;
        }
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        return chest.getItem() instanceof ClothingItem c && c.getClothingType() == ClothingType.COAT;
    }

    private CoatLegModel<AbstractClientPlayer> fullModel() {
        if (full == null) {
            full = new CoatLegModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(CoatLegModel.LAYER));
        }
        return full;
    }

    private CoatLegModel<AbstractClientPlayer> toShoeModel() {
        if (toShoe == null) {
            toShoe = new CoatLegModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(CoatLegModel.LAYER_TO_SHOE));
        }
        return toShoe;
    }

    private CoatLegModel<AbstractClientPlayer> toBootsModel() {
        if (toBoots == null) {
            toBoots = new CoatLegModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(CoatLegModel.LAYER_TO_BOOTS));
        }
        return toBoots;
    }
}
