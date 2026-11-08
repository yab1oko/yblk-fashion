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
 * Scrap chestplate — body + front plate + arms.
 * UV space 64×64 (Blockbench); PNG may be 128×128 HD.
 * Inflate well above coat/vest. Side spikes {@code NONE} at Blockbench offsets; top shoulder
 * plates lifted slightly so they clear {@link #ARM_INFLATE} / pad instead of sinking in.
 */
public class ScrapChestModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "scrap_chest"), "main");

    public static final float INFLATE = 0.55F;
    public static final float ARM_INFLATE = 0.50F;
    public static final float PLATE_INFLATE = 0.65F;

    private boolean suppressArmorPass;

    public ScrapChestModel(ModelPart root) {
        super(root);
        this.head.visible = false;
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

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation body = new CubeDeformation(INFLATE);
        CubeDeformation plate = new CubeDeformation(PLATE_INFLATE);
        CubeDeformation arm = new CubeDeformation(ARM_INFLATE);
        CubeDeformation none = CubeDeformation.NONE;
        CubeDeformation pad = new CubeDeformation(ARM_INFLATE + 0.08F);

        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, body)
                        .texOffs(47, 35).addBox(-3.0F, 2.0F, -2.5F, 6.0F, 6.0F, 1.0F, plate),
                PartPose.ZERO);

        PartDefinition rightArm = root.addOrReplaceChild("right_arm",
                CubeListBuilder.create()
                        .texOffs(24, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, arm)
                        // Top shoulder plate: was y=-4.5 (flush with pad); lift so it sits on top.
                        .texOffs(12, 2).addBox(-3.0F, -5.0F, 0.0F, 4.0F, 2.0F, 0.0F, none),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        rightArm.addOrReplaceChild("spike_front",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-1.0F, -2.0F, 0.0F, 2.0F, 5.0F, 0.0F, none),
                PartPose.offsetAndRotation(-4.5F, 3.8F, -1.0F, 0.0F, 3.1416F, 0.0F));
        rightArm.addOrReplaceChild("spike_back",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-1.0F, -2.0F, 0.0F, 2.0F, 5.0F, 0.0F, none),
                PartPose.offsetAndRotation(-4.5F, 3.3F, 1.0F, 0.0F, 3.1416F, 0.0F));
        rightArm.addOrReplaceChild("shoulder_pad",
                CubeListBuilder.create().texOffs(34, 60)
                        .addBox(-2.0F, -0.5F, -1.5F, 4.0F, 1.0F, 3.0F, pad),
                PartPose.offsetAndRotation(-1.0F, -2.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition leftArm = root.addOrReplaceChild("left_arm",
                CubeListBuilder.create()
                        .texOffs(0, 32).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, arm)
                        .texOffs(34, 60).addBox(-1.0F, -2.5F, -1.5F, 4.0F, 1.0F, 3.0F, pad)
                        .texOffs(0, 0).addBox(3.5F, 1.8F, -1.0F, 2.0F, 5.0F, 0.0F, none)
                        .texOffs(0, 0).addBox(3.5F, 1.3F, 1.0F, 2.0F, 5.0F, 0.0F, none),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        leftArm.addOrReplaceChild("shoulder_plate",
                CubeListBuilder.create().texOffs(12, 2)
                        .addBox(-2.0F, -1.0F, 0.0F, 4.0F, 2.0F, 0.0F, none),
                // Was -3.5; +0.5 up so the plate clears the padded shoulder.
                PartPose.offsetAndRotation(1.0F, -4.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }
}
