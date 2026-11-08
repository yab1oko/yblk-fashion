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
import yblk.fashion.client.model.NvgModel;
import yblk.fashion.item.NvgItem;

/** Curios {@code mask} NVG — drawn after helmet so lenses sit on the shell. */
public class NvgLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private NvgModel<AbstractClientPlayer> model;

    public NvgLayer(
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
        ItemStack worn = findNvg(player);
        if (worn.isEmpty() || !(worn.getItem() instanceof NvgItem nvgItem)) {
            return;
        }

        NvgModel<AbstractClientPlayer> nvg = model();
        getParentModel().copyPropertiesTo(nvg);
        nvg.setAllVisible(false);
        nvg.head.visible = true;

        ResourceLocation tex = nvgItem.modelTexture();
        VertexConsumer body = buffer.getBuffer(RenderType.entityCutoutNoCull(tex));
        nvg.renderToBuffer(poseStack, body, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

        // Green lens pixels glow (fullbright) via separate emissive atlas.
        VertexConsumer glow = buffer.getBuffer(
                RenderType.entityTranslucentEmissive(nvgItem.emissiveTexture()));
        nvg.renderToBuffer(poseStack, glow, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private static ItemStack findNvg(AbstractClientPlayer player) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(inv -> inv.findFirstCurio(NvgItem::isNvg))
                .filter(r -> yblk.fashion.compat.CuriosClothing.isRendered(player, r))
                .map(r -> r.stack())
                .orElse(ItemStack.EMPTY);
    }

    private NvgModel<AbstractClientPlayer> model() {
        if (model == null) {
            model = new NvgModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(NvgModel.LAYER));
        }
        return model;
    }
}
