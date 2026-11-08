package yblk.fashion.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import yblk.fashion.compat.CuriosClothing;
import yblk.fashion.item.ClothingItem;
import yblk.fashion.item.ClothingType;

import java.lang.reflect.Field;

/**
 * Runs before {@code CapeLayer}: clothing on the chest must not use the vanilla
 * "wearing plate" cape pose. Resets the cloak to the empty-chest baseline, then
 * nudges it clear of inflated fabric.
 * <p>
 * {@code CapeLayer} applies {@code Y=180} before {@code renderCloak}, so moving
 * the cape <em>away</em> from the back of the body requires a <em>negative</em>
 * {@code cloak.z} (vanilla chestplate uses {@code -1.1F}).
 * <p>
 * {@link ClothingLayerHider} cannot do this in {@code RenderPlayerEvent.Pre}
 * because that fires before {@code setupAnim}, which overwrites the cloak again.
 */
public class CapePoseFixLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final Field CLOAK = findCloakField();

    public CapePoseFixLayer(
            RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    private static Field findCloakField() {
        try {
            Field f = ObfuscationReflectionHelper.findField(PlayerModel.class, "cloak");
            f.setAccessible(true);
            return f;
        } catch (Exception ignored) {
            try {
                Field f = PlayerModel.class.getDeclaredField("f_103373_");
                f.setAccessible(true);
                return f;
            } catch (Exception e) {
                return null;
            }
        }
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

        ItemStack outer = CuriosClothing.getOuterwear(player);
        ItemStack vest = CuriosClothing.getVest(player);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ClothingType type = null;
        if (outer.getItem() instanceof ClothingItem oc) {
            type = oc.getClothingType();
        } else if (vest.getItem() instanceof ClothingItem vc) {
            type = vc.getClothingType();
        } else if (chest.getItem() instanceof ClothingItem cc && !cc.getClothingType().isLeggings()) {
            type = cc.getClothingType();
        }
        if (type == null) {
            return;
        }

        ModelPart cloak = cloak(getParentModel());
        if (cloak == null) {
            return;
        }

        // Empty-chest baseline is z=0 / crouch z=1.4. Negative delta clears fabric
        // after CapeLayer's Y=180 (same direction as vanilla plate's -1.1).
        float back = type == ClothingType.COAT ? 0.70F
                : (type.isVest() || type.hasHood() || type.isOuterwear() ? 0.55F : 0.35F);
        if (player.isCrouching()) {
            cloak.z = 1.4F - back;
            cloak.y = 1.85F;
        } else {
            cloak.z = -back;
            cloak.y = 0.0F;
        }
    }

    private static ModelPart cloak(PlayerModel<?> model) {
        if (CLOAK == null) {
            return null;
        }
        try {
            return (ModelPart) CLOAK.get(model);
        } catch (IllegalAccessException e) {
            return null;
        }
    }
}
