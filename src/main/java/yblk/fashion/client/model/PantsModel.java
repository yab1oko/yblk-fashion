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
 * Pants from Blockbench export {@code pants.java}. UV space 64×64 (textures 128×128 HD).
 * Covers thighs → mid-shin (8.5 tall on vanilla 12-tall legs).
 * Left leg is nudged slightly forward so the crotches don't z-fight.
 */
public class PantsModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "pants"), "main");

    public static final float INFLATE = 0.08F;
    public static final float LEG_HEIGHT = 8.5F;
    /** Tiny +Z on the left leg so overlapping pant faces don't flicker. */
    public static final float LEFT_LEG_Z = 0.04F;

    private boolean suppressArmorPass;

    public PantsModel(ModelPart root) {
        super(root);
        this.head.visible = false;
        this.hat.visible = false;
        this.body.visible = false;
        this.leftArm.visible = false;
        this.rightArm.visible = false;
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

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation d = new CubeDeformation(INFLATE);

        // Blockbench: Right Leg texOffs(32, 0), Left Leg texOffs(16, 32)
        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(32, 0)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, LEG_HEIGHT, 4.0F, d),
                PartPose.offset(-1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(16, 32)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, LEG_HEIGHT, 4.0F, d),
                PartPose.offset(1.9F, 12.0F, LEFT_LEG_Z));

        return LayerDefinition.create(mesh, 64, 64);
    }
}
