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
 * Sneakers from Blockbench {@code sneakers.java}. UV space 64×64 (textures 128×128).
 * Covers ankle → sole (y 8.5…12 on the vanilla leg).
 */
public class SneakersModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "sneakers"), "main");

    public static final float INFLATE = 0.08F;
    /** Leg-local Y where the shoe starts (flush with {@link PantsModel#LEG_HEIGHT}). */
    public static final float SHOE_TOP_Y = 8.5F;
    public static final float SHOE_HEIGHT = 3.5F;
    public static final float LEFT_LEG_Z = PantsModel.LEFT_LEG_Z;

    private boolean suppressArmorPass;

    public SneakersModel(ModelPart root) {
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

        PartDefinition rightLeg = root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(32, 0)
                        .addBox(-2.0F, SHOE_TOP_Y, -2.0F, 4.0F, SHOE_HEIGHT, 4.0F, d),
                PartPose.offset(-1.9F, 12.0F, 0.0F));
        rightLeg.addOrReplaceChild("toe",
                CubeListBuilder.create().texOffs(1, 2)
                        .addBox(-2.0F, -1.0F, 0.0F, 3.0F, 1.0F, 0.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.5F, 9.5F, -2.0F, 0.2618F, 0.0F, 0.0F));

        PartDefinition leftLeg = root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(16, 32)
                        .addBox(-2.0F, SHOE_TOP_Y, -2.0F, 4.0F, SHOE_HEIGHT, 4.0F, d),
                PartPose.offset(1.9F, 12.0F, LEFT_LEG_Z));
        leftLeg.addOrReplaceChild("toe",
                CubeListBuilder.create().texOffs(1, 2)
                        .addBox(-2.0F, -1.0F, 0.0F, 3.0F, 1.0F, 0.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(0.5F, 9.5F, -2.0F, 0.2618F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }
}
