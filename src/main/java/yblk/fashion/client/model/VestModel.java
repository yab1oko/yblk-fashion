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
 * Plate carrier from Blockbench {@code vest.java} — body shell + pouches, UV 64×64.
 * <p>
 * Inflate sits outside hoodie / jacket / coat so the vest stays visible over
 * Curios outerwear. PNG alpha cuts straps / neckline / armholes.
 */
public class VestModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "vest"), "main");

    /** Above {@link CoatModel#INFLATE} (thickest outerwear body). */
    public static final float INFLATE = CoatModel.INFLATE + 0.06F;

    private boolean suppressArmorPass;

    public VestModel(ModelPart root) {
        super(root);
        this.head.visible = false;
        this.hat.visible = false;
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
        root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation d = new CubeDeformation(INFLATE);
        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, d)
                        .texOffs(0, 16).addBox(-4.0F, 6.0F, -3.0F, 8.0F, 6.0F, 1.0F, d)
                        .texOffs(18, 16).addBox(-4.0F, 6.0F, 2.0F, 8.0F, 6.0F, 1.0F, d)
                        .texOffs(0, 23).addBox(-4.0F, 2.6F, -3.0F, 2.0F, 3.0F, 1.0F, d)
                        .texOffs(0, 23).addBox(2.0F, 2.6F, -3.0F, 2.0F, 3.0F, 1.0F, d)
                        .texOffs(0, 23).addBox(-1.0F, 2.6F, -3.0F, 2.0F, 3.0F, 1.0F, d),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 64);
    }
}
