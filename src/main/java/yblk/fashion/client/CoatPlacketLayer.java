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
import yblk.fashion.client.model.CoatPlacketModel;
import yblk.fashion.compat.CuriosClothing;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClothingType;

/**
 * After a coat hides the jacket outer layer, redraw only the open-front strip
 * from the player skin so the PNG alpha slit shows outer skin (not a blank gap).
 * Skipped when a chest shirt/hoodie already owns the torso cover.
 */
public class CoatPlacketLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private CoatPlacketModel<AbstractClientPlayer> model;

    public CoatPlacketLayer(
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
        // Torso already covered by a chest piece — slit shows that mesh, not skin outer.
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.getItem() instanceof ClothingItem c && c.getClothingType().isChestShirt()) {
            return;
        }

        CoatPlacketModel<AbstractClientPlayer> placket = model();
        getParentModel().copyPropertiesTo(placket);
        placket.setAllVisible(false);
        placket.body.visible = true;

        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(player.getSkinTextureLocation()));
        placket.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private static boolean wearingCoat(AbstractClientPlayer player) {
        ItemStack outer = CuriosClothing.getRenderedOuterwear(player);
        if (outer.getItem() instanceof ClothingItem c && c.getClothingType() == ClothingType.COAT) {
            return true;
        }
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        return chest.getItem() instanceof ClothingItem c && c.getClothingType() == ClothingType.COAT;
    }

    private CoatPlacketModel<AbstractClientPlayer> model() {
        if (model == null) {
            model = new CoatPlacketModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(CoatPlacketModel.LAYER));
        }
        return model;
    }
}
