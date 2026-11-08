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
 * Leather shoes from Blockbench {@code shoes.java}. UV 32×32 (textures 64×64 HD).
 * Covers ankle → sole (y 8…12) plus a small toe cube.
 */
public class ShoesModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "shoes"), "main");

    /** Slightly above pants (0.08) so the cuff doesn't z-fight the pant hem. */
    public static final float INFLATE = 0.14F;
    /** Leg-local Y where the shoe starts — shin outer redraw ends here. */
    public static final float SHOE_TOP_Y = 8.0F;
    public static final float SHOE_HEIGHT = 4.0F;
    public static final float LEFT_LEG_Z = PantsModel.LEFT_LEG_Z;

    private boolean suppressArmorPass;

    public ShoesModel(ModelPart root) {
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

        // Blockbench: Right Leg texOffs(0,0) + toe (0,16); Left Leg (0,8) + toe (16,0)
        PartDefinition rightLeg = root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-2.0F, SHOE_TOP_Y, -2.0F, 4.0F, SHOE_HEIGHT, 4.0F, d),
                PartPose.offset(-1.9F, 12.0F, 0.0F));
        rightLeg.addOrReplaceChild("toe",
                CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-2.0F, 10.0F, -2.5F, 4.0F, 2.0F, 1.0F, d),
                PartPose.ZERO);

        PartDefinition leftLeg = root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(0, 8)
                        .addBox(-2.0F, SHOE_TOP_Y, -2.0F, 4.0F, SHOE_HEIGHT, 4.0F, d),
                PartPose.offset(1.9F, 12.0F, LEFT_LEG_Z));
        leftLeg.addOrReplaceChild("toe",
                CubeListBuilder.create().texOffs(16, 0)
                        .addBox(-2.0F, 10.0F, -2.5F, 4.0F, 2.0F, 1.0F, d),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 32, 32);
    }
}
