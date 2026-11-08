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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import yblk.fashion.client.model.BandanaModel;
import yblk.fashion.item.BandanaItem;

/** Curios {@code bandana} — shape from PNG alpha cutout. */
public class BandanaLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private BandanaModel<AbstractClientPlayer> model;

    public BandanaLayer(
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
        ItemStack worn = findBandana(player);
        if (worn.isEmpty() || !(worn.getItem() instanceof BandanaItem bandanaItem)) {
            return;
        }

        BandanaModel<AbstractClientPlayer> bandana = model();
        getParentModel().copyPropertiesTo(bandana);
        bandana.setAllVisible(false);
        bandana.head.visible = true;

        ResourceLocation tex = bandanaItem.modelTexture();
        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(tex));
        bandana.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private static ItemStack findBandana(AbstractClientPlayer player) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(inv -> inv.findFirstCurio(BandanaItem::isBandana))
                .filter(r -> yblk.fashion.compat.CuriosClothing.isRendered(player, r))
                .map(r -> r.stack())
                .orElse(ItemStack.EMPTY);
    }

    private BandanaModel<AbstractClientPlayer> model() {
        if (model == null) {
            model = new BandanaModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(BandanaModel.LAYER));
        }
        return model;
    }
}
