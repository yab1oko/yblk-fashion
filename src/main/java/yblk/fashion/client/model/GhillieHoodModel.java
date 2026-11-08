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

/** Ghillie hood — Desktop {@code ghillie.java} Head. Atlas 128. */
public class GhillieHoodModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "ghillie_hood"), "main");

    public static final float INFLATE = 0.20F;
    private static final float FLAP = 0.10F;

    private boolean suppressArmorPass;

    public GhillieHoodModel(ModelPart root) {
        super(root);
        this.hat.visible = false;
        this.body.visible = false;
        this.leftArm.visible = false;
        this.rightArm.visible = false;
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

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation d = new CubeDeformation(INFLATE);
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 10).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, d)
                        .texOffs(0, 0).addBox(-5.0F, -8.4F, -5.0F, 10.0F, FLAP, 10.0F, CubeDeformation.NONE),
                PartPose.ZERO);

        head.addOrReplaceChild("flap_l",
                CubeListBuilder.create().texOffs(0, 26)
                        .addBox(-7.0F, 0.0F, -5.0F, 7.0F, FLAP, 10.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(-4.0F, -8.0F, 0.0F, 0.0F, 0.0F, -1.2654F));
        head.addOrReplaceChild("flap_r",
                CubeListBuilder.create().texOffs(0, 26)
                        .addBox(-7.0F, 0.0F, -5.0F, 7.0F, FLAP, 10.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(4.0F, -8.0F, 0.0F, 0.0F, 0.0F, -1.8762F));
        head.addOrReplaceChild("flap_f",
                CubeListBuilder.create().texOffs(32, 10)
                        .addBox(-7.0F, 0.0F, -5.0F, 7.0F, FLAP, 10.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, -8.0F, -4.0F, 1.5708F, -0.3054F, -1.5708F));
        head.addOrReplaceChild("flap_b",
                CubeListBuilder.create().texOffs(0, 26)
                        .addBox(-7.0F, 0.0F, -5.0F, 7.0F, FLAP, 10.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.0F, -8.0F, 4.0F, 1.5708F, 0.3054F, -1.5708F));

        return LayerDefinition.create(mesh, 128, 128);
    }
}
