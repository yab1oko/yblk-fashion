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
import yblk.fashion.client.model.GlassesModel;
import yblk.fashion.compat.CuriosClothing;
import yblk.fashion.item.GlassesItem;

/** Curios {@code mask} glasses — PNG alpha cut over the 2nd skin hat layer. */
public class GlassesLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private GlassesModel<AbstractClientPlayer> model;

    public GlassesLayer(
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
        if (GhillieCover.hidesExtras(player) || ClothingLayerHider.hidesFaceCosmetics(player)) {
            return;
        }
        ItemStack worn = findGlasses(player);
        if (worn.isEmpty() || !(worn.getItem() instanceof GlassesItem glassesItem)) {
            return;
        }

        GlassesModel<AbstractClientPlayer> glasses = model();
        getParentModel().copyPropertiesTo(glasses);
        glasses.setAllVisible(false);
        glasses.head.visible = true;

        // Translucent so lens pixels (A≈70–90) tint instead of cutout discarding / staying opaque.
        ResourceLocation tex = glassesItem.modelTexture();
        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(tex));
        glasses.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private static ItemStack findGlasses(AbstractClientPlayer player) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(inv -> inv.findFirstCurio(GlassesItem::isGlasses))
                .filter(r -> CuriosClothing.isRendered(player, r))
                .map(r -> r.stack())
                .orElse(ItemStack.EMPTY);
    }

    private GlassesModel<AbstractClientPlayer> model() {
        if (model == null) {
            model = new GlassesModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(GlassesModel.LAYER));
        }
        return model;
    }
}
