package yblk.fashion.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import yblk.fashion.Fashion;

/**
 * Hoodie from Blockbench exports (closed/open × Steve/Alex).
 * UV space 64×64; PNGs are 128×128 HD. Long sleeves (h=11); hood on {@code head}.
 */
public class HoodieModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_CLOSED_WIDE =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "hoodie_closed_wide"), "main");
    public static final ModelLayerLocation LAYER_CLOSED_SLIM =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "hoodie_closed_slim"), "main");
    public static final ModelLayerLocation LAYER_OPEN_WIDE =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "hoodie_open_wide"), "main");
    public static final ModelLayerLocation LAYER_OPEN_SLIM =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "hoodie_open_slim"), "main");
    public static final ModelLayerLocation LAYER_CLOSED_WIDE_HAT =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "hoodie_closed_wide_hat"), "main");
    public static final ModelLayerLocation LAYER_CLOSED_SLIM_HAT =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "hoodie_closed_slim_hat"), "main");
    public static final ModelLayerLocation LAYER_OPEN_WIDE_HAT =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "hoodie_open_wide_hat"), "main");
    public static final ModelLayerLocation LAYER_OPEN_SLIM_HAT =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "hoodie_open_slim_hat"), "main");

    public static final float INFLATE = 0.10F;
    /** Thinner sleeves reduce arm∩body cube intersection seams. */
    public static final float ARM_INFLATE = 0.04F;
    /** Outside vanilla hat (0.5) so fabric doesn't sink into hair. */
    public static final float HOOD_INFLATE = 0.55F;
    /**
     * Outside fashion headwear under an open hood. Sized for tactical helmet
     * envelope ({@link HelmetModel#DETAIL_INFLATE} on rails / top plate).
     */
    public static final float HOOD_INFLATE_OVER_HAT = 1.20F;

    private boolean suppressArmorPass;

    public HoodieModel(ModelPart root) {
        super(root);
        this.hat.visible = false;
        this.leftLeg.visible = false;
        this.rightLeg.visible = false;
    }

    public void setSuppressArmorPass(boolean suppressArmorPass) {
        this.suppressArmorPass = suppressArmorPass;
    }

    @Override
    public void renderToBuffer(
            com.mojang.blaze3d.vertex.PoseStack poseStack,
            com.mojang.blaze3d.vertex.VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha) {
        if (suppressArmorPass) {
            return;
        }
        super.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public static LayerDefinition createClosedWide() {
        return create(false, false, false);
    }

    public static LayerDefinition createClosedSlim() {
        return create(true, false, false);
    }

    public static LayerDefinition createOpenWide() {
        return create(false, true, false);
    }

    public static LayerDefinition createOpenSlim() {
        return create(true, true, false);
    }

    public static LayerDefinition createClosedWideHat() {
        return create(false, false, true);
    }

    public static LayerDefinition createClosedSlimHat() {
        return create(true, false, true);
    }

    public static LayerDefinition createOpenWideHat() {
        return create(false, true, true);
    }

    public static LayerDefinition createOpenSlimHat() {
        return create(true, true, true);
    }

    private static LayerDefinition create(boolean slim, boolean hoodOpen, boolean overHat) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation d = new CubeDeformation(INFLATE);
        CubeDeformation arm = new CubeDeformation(ARM_INFLATE);
        CubeDeformation hood = new CubeDeformation(overHat ? HOOD_INFLATE_OVER_HAT : HOOD_INFLATE);

        if (hoodOpen) {
            // hoodie_open — hood draped over the back of the head
            root.addOrReplaceChild("head",
                    CubeListBuilder.create().texOffs(4, 4)
                            .addBox(-4.0F, -8.0F, 0.0F, 8.0F, 8.0F, 4.0F, hood),
                    PartPose.ZERO);
        } else {
            // hoodie_closed — collar / rolled hood at the neck
            root.addOrReplaceChild("head",
                    CubeListBuilder.create().texOffs(40, 0)
                            .addBox(-4.0F, -3.0F, 0.0F, 8.0F, 3.0F, 4.0F, hood),
                    PartPose.ZERO);
        }

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, d),
                PartPose.ZERO);

        if (slim) {
            root.addOrReplaceChild("right_arm",
                    CubeListBuilder.create().texOffs(24, 49)
                            .addBox(-2.0F, -2.0F, -2.0F, 3.0F, 11.0F, 4.0F, arm),
                    PartPose.offset(-5.0F, 2.5F, 0.0F));
            root.addOrReplaceChild("left_arm",
                    CubeListBuilder.create().texOffs(0, 49)
                            .addBox(-1.0F, -2.0F, -2.0F, 3.0F, 11.0F, 4.0F, arm),
                    PartPose.offset(5.0F, 2.5F, 0.0F));
        } else {
            root.addOrReplaceChild("right_arm",
                    CubeListBuilder.create().texOffs(24, 16)
                            .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, arm),
                    PartPose.offset(-5.0F, 2.0F, 0.0F));
            root.addOrReplaceChild("left_arm",
                    CubeListBuilder.create().texOffs(0, 32)
                            .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, arm),
                    PartPose.offset(5.0F, 2.0F, 0.0F));
        }

        return LayerDefinition.create(mesh, 64, 64);
    }
}
