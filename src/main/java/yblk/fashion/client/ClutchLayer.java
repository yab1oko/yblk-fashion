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
import yblk.fashion.client.model.ClutchModel;
import yblk.fashion.compat.CuriosClothing;
import yblk.fashion.item.ClutchItem;

/** Curios {@code body} clutch. With a vest worn, yaw 180° so the pouch clears the front plates. */
public class ClutchLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private ClutchModel<AbstractClientPlayer> model;

    public ClutchLayer(
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
        ItemStack worn = findClutch(player);
        if (worn.isEmpty() || !(worn.getItem() instanceof ClutchItem clutchItem)) {
            return;
        }

        ClutchModel<AbstractClientPlayer> clutch = model();
        getParentModel().copyPropertiesTo(clutch);
        clutch.setAllVisible(false);
        clutch.body.visible = true;
        // Vest front plates fight the pouch — flip to the back only then.
        if (!CuriosClothing.getRenderedVest(player).isEmpty()) {
            clutch.body.yRot += (float) Math.PI;
        }

        ResourceLocation tex = clutchItem.modelTexture();
        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(tex));
        clutch.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private static ItemStack findClutch(AbstractClientPlayer player) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(inv -> inv.findFirstCurio(ClutchItem::isClutch))
                .filter(r -> CuriosClothing.isRendered(player, r))
                .map(r -> r.stack())
                .orElse(ItemStack.EMPTY);
    }

    private ClutchModel<AbstractClientPlayer> model() {
        if (model == null) {
            model = new ClutchModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ClutchModel.LAYER));
        }
        return model;
    }
}
