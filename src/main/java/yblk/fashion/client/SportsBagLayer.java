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
import yblk.fashion.client.model.SportsBagModel;
import yblk.fashion.item.SportsBagItem;

/** Curios {@code body} sports bag on the hip. */
public class SportsBagLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private SportsBagModel<AbstractClientPlayer> model;

    public SportsBagLayer(
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
        ItemStack worn = findBag(player);
        if (worn.isEmpty() || !(worn.getItem() instanceof SportsBagItem bagItem)) {
            return;
        }

        SportsBagModel<AbstractClientPlayer> bag = model();
        getParentModel().copyPropertiesTo(bag);
        bag.setAllVisible(false);
        bag.body.visible = true;

        ResourceLocation tex = bagItem.modelTexture();
        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(tex));
        bag.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    private static ItemStack findBag(AbstractClientPlayer player) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(inv -> inv.findFirstCurio(SportsBagItem::isSportsBag))
                .filter(r -> yblk.fashion.compat.CuriosClothing.isRendered(player, r))
                .map(r -> r.stack())
                .orElse(ItemStack.EMPTY);
    }

    private SportsBagModel<AbstractClientPlayer> model() {
        if (model == null) {
            model = new SportsBagModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(SportsBagModel.LAYER));
        }
        return model;
    }
}
