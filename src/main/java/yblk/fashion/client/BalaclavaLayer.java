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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import yblk.fashion.client.model.BalaclavaModel;
import yblk.fashion.item.BalaclavaItem;
import yblk.fashion.item.ClothingItem;

/** Curios {@code mask} balaclava — full head shell (shrunk under fashion headwear). */
public class BalaclavaLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private BalaclavaModel<AbstractClientPlayer> model;
    private BalaclavaModel<AbstractClientPlayer> modelUnderHat;

    public BalaclavaLayer(
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
        ItemStack worn = findBalaclava(player);
        if (worn.isEmpty() || !(worn.getItem() instanceof BalaclavaItem balaclavaItem)) {
            return;
        }

        boolean underHat = wearingHeadwear(player);
        BalaclavaModel<AbstractClientPlayer> balaclava = underHat ? modelUnderHat() : model();
        getParentModel().copyPropertiesTo(balaclava);
        balaclava.setAllVisible(false);
        balaclava.head.visible = true;

        ResourceLocation tex = balaclavaItem.modelTexture();
        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(tex));
        balaclava.renderToBuffer(poseStack, vc, light, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
    }

    /** Beanie / cowboy / straw / cap / helmet — crown would otherwise clip through. */
    private static boolean wearingHeadwear(AbstractClientPlayer player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        return head.getItem() instanceof ClothingItem wear && wear.getClothingType().isHelmet();
    }

    private static ItemStack findBalaclava(AbstractClientPlayer player) {
        return CuriosApi.getCuriosInventory(player)
                .resolve()
                .flatMap(inv -> inv.findFirstCurio(BalaclavaItem::isBalaclava))
                .filter(r -> yblk.fashion.compat.CuriosClothing.isRendered(player, r))
                .map(r -> r.stack())
                .orElse(ItemStack.EMPTY);
    }

    private BalaclavaModel<AbstractClientPlayer> model() {
        if (model == null) {
            model = new BalaclavaModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(BalaclavaModel.LAYER));
        }
        return model;
    }

    private BalaclavaModel<AbstractClientPlayer> modelUnderHat() {
        if (modelUnderHat == null) {
            modelUnderHat = new BalaclavaModel<>(
                    Minecraft.getInstance().getEntityModels().bakeLayer(BalaclavaModel.LAYER_UNDER_HAT));
        }
        return modelUnderHat;
    }
}
