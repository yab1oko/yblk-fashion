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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import yblk.fashion.client.model.PouchModel;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClothingMods;
import yblk.fashion.item.PouchItem;

/**
 * Thigh pouches: single (body XOR legs) or double (body AND legs).
 * Drawn after pants so shell inflate sits on top.
 */
public class PouchLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private PouchModel<AbstractClientPlayer> single;
    private PouchModel<AbstractClientPlayer> both;

    public PouchLayer(
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
        if (player.isInvisible() || ClothingRenderSkip.localFirstPersonWorld(player)) {
            return;
        }
        if (GhillieCover.hidesExtras(player)) {
            return;
        }
        boolean body = bodyHasPouch(player);
        boolean legs = legsHasPouch(player);
        if (!body && !legs) {
            return;
        }

        boolean useDouble = body && legs;
        PouchModel<AbstractClientPlayer> pouch = useDouble ? doubleModel() : singleModel();
        getParentModel().copyPropertiesTo(pouch);
        pouch.setAllVisible(false);
        pouch.rightLeg.visible = true;
        pouch.leftLeg.visible = useDouble;

        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(PouchItem.modelTexture()));
        pouch.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    public static boolean bodyHasPouch(LivingEntity entity) {
        return hasPouchOnBodyPiece(entity.getItemBySlot(EquipmentSlot.CHEST));
    }

    public static boolean legsHasPouch(LivingEntity entity) {
        ItemStack legs = entity.getItemBySlot(EquipmentSlot.LEGS);
        return ClothingMods.has(legs, ClothingMods.POUCH)
                && legs.getItem() instanceof ClothingItem c
                && c.getClothingType().isLeggings();
    }

    private static boolean hasPouchOnBodyPiece(ItemStack stack) {
        return ClothingMods.has(stack, ClothingMods.POUCH)
                && stack.getItem() instanceof ClothingItem c
                && c.getClothingType().isPouchBodySlot();
    }

    private PouchModel<AbstractClientPlayer> singleModel() {
        if (single == null) {
            single = new PouchModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(PouchModel.LAYER_SINGLE));
        }
        return single;
    }

    private PouchModel<AbstractClientPlayer> doubleModel() {
        if (both == null) {
            both = new PouchModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(PouchModel.LAYER_DOUBLE));
        }
        return both;
    }
}
