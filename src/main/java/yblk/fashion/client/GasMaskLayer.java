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
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import yblk.fashion.client.model.GasMaskModel;
import yblk.fashion.compat.CuriosClothing;
import yblk.fashion.item.GasMaskItem;

/** Curios {@code mask} gas mask — model switches by installed filter count. */
public class GasMaskLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private GasMaskModel<AbstractClientPlayer> zero;
    private GasMaskModel<AbstractClientPlayer> one;
    private GasMaskModel<AbstractClientPlayer> two;

    public GasMaskLayer(
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
        ItemStack worn = findGasMask(player);
        if (worn.isEmpty() || !(worn.getItem() instanceof GasMaskItem maskItem)) {
            return;
        }

        GasMaskModel<AbstractClientPlayer> model = modelFor(GasMaskItem.filterCount(worn));
        getParentModel().copyPropertiesTo(model);
        model.setAllVisible(false);
        model.head.visible = true;

        // Translucent so lens glass alpha from the PNG is visible (cutout treats it as solid).
        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(maskItem.modelTexture()));
        model.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private static ItemStack findGasMask(AbstractClientPlayer player) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(inv -> inv.findFirstCurio(GasMaskItem::isGasMask))
                .filter(r -> CuriosClothing.isRendered(player, r))
                .map(r -> r.stack())
                .orElse(ItemStack.EMPTY);
    }

    private GasMaskModel<AbstractClientPlayer> modelFor(int filters) {
        if (filters >= 2) {
            return two();
        }
        if (filters == 1) {
            return one();
        }
        return zero();
    }

    private GasMaskModel<AbstractClientPlayer> zero() {
        if (zero == null) {
            zero = new GasMaskModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(GasMaskModel.LAYER_ZERO));
        }
        return zero;
    }

    private GasMaskModel<AbstractClientPlayer> one() {
        if (one == null) {
            one = new GasMaskModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(GasMaskModel.LAYER_ONE));
        }
        return one;
    }

    private GasMaskModel<AbstractClientPlayer> two() {
        if (two == null) {
            two = new GasMaskModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(GasMaskModel.LAYER_TWO));
        }
        return two;
    }
}
