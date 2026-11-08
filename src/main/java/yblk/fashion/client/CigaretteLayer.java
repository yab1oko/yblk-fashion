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
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import yblk.fashion.client.model.CigaretteModel;
import yblk.fashion.item.CigaretteItem;
import yblk.fashion.item.ClothingMods;

/**
 * Helmet cigarette mod — stick cutout + emissive tip; smoke translucent (bilinear).
 */
public class CigaretteLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final int FRAME_TICKS = 5;

    private CigaretteModel<AbstractClientPlayer> model;

    public CigaretteLayer(
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
        if (ClothingRenderSkip.hideHeadGarments(player)) {
            return;
        }
        if (GhillieCover.hidesExtras(player) || ClothingLayerHider.hidesFaceCosmetics(player)) {
            return;
        }
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!ClothingMods.has(helmet, ClothingMods.CIGARETTE)) {
            return;
        }

        CigaretteModel<AbstractClientPlayer> cig = model();
        getParentModel().copyPropertiesTo(cig);
        cig.setAllVisible(false);
        cig.head.visible = true;

        int frame = (player.tickCount / FRAME_TICKS) % CigaretteItem.ANIM_FRAMES;
        ResourceLocation tex = CigaretteItem.modelTexture(frame);
        ResourceLocation emissive = CigaretteItem.emissiveTexture(frame);

        cig.showStickOnly();
        VertexConsumer body = buffer.getBuffer(RenderType.entityCutoutNoCull(tex));
        cig.renderToBuffer(poseStack, body, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

        VertexConsumer glow = buffer.getBuffer(RenderType.entityTranslucentEmissive(emissive));
        cig.renderToBuffer(poseStack, glow, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

        cig.showSmokeOnly();
        AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(tex);
        texture.setFilter(true, false);
        VertexConsumer smoke = buffer.getBuffer(RenderType.entityTranslucent(tex));
        cig.renderToBuffer(poseStack, smoke, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
        texture.setFilter(false, false);
    }

    private CigaretteModel<AbstractClientPlayer> model() {
        if (model == null) {
            model = new CigaretteModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(CigaretteModel.LAYER));
        }
        return model;
    }
}
