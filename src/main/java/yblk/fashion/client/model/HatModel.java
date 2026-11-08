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
 * Hat from Blockbench {@code hat.java}. Cubes sit on {@code head} (UV 64×64).
 * Mesh is already cut to the hat shape — no full-head cube.
 */
public class HatModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "hat"), "main");

    /**
     * Tight shell (may sit under vanilla outer 0.5). 2nd-layer cut is
     * {@code hideHat} + {@link yblk.fashion.client.HatOuterLayer}, not occlusion.
     */
    public static final float INFLATE = 0.42F;

    /** Head-local Y where the brim ends — 2nd-skin redraw starts here. */
    public static final float HEM_Y = -4.5F;

    private final ModelPart knob;
    private boolean suppressArmorPass;

    public HatModel(ModelPart root) {
        super(root);
        this.knob = root.getChild("head").getChild("knob");
        this.hat.visible = false;
        this.body.visible = false;
        this.leftArm.visible = false;
        this.rightArm.visible = false;
        this.leftLeg.visible = false;
        this.rightLeg.visible = false;
    }

    /** Top pom-pom cube — hide under an open hood. */
    public void setKnobVisible(boolean visible) {
        this.knob.visible = visible;
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
        // Crown + brim on head; knob is a child so open hood can hide it.
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 11).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 3.0F, 8.0F, d)
                        .texOffs(0, 0).addBox(-4.5F, -6.5F, -4.5F, 9.0F, 2.0F, 9.0F, d),
                PartPose.ZERO);
        head.addOrReplaceChild("knob",
                CubeListBuilder.create()
                        .texOffs(0, 22).addBox(-1.5F, -10.0F, -1.5F, 3.0F, 2.0F, 3.0F, d),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 64);
    }
}
