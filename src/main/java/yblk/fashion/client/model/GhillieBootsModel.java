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
 * Ghillie boots — lower legs (y 8…12).
 * Desktop: {@code Right Leg} cuff + remapped {@code left foot}.
 */
public class GhillieBootsModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "ghillie_boots"), "main");

    public static final float INFLATE = 0.20F;
    public static final float BOOT_TOP_Y = 8.0F;
    public static final float BOOT_HEIGHT = 4.0F;
    private static final float STRIP_W = 0.12F;

    private boolean suppressArmorPass;

    public GhillieBootsModel(ModelPart root) {
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

        PartDefinition right = root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(32, 36)
                        .addBox(-2.0F, BOOT_TOP_Y, -2.0F, 4.0F, BOOT_HEIGHT, 4.0F, d),
                PartPose.offset(-1.9F, 12.0F, 0.0F));
        // Desktop "right foot" hem strips (were on pants; belong on the cuff).
        addStrip(right, "rf29", 23, 52, -2.1F, 8.0F, 2.0F, 0.1719F, 0.7703F, 0.2444F);
        addStrip(right, "rf30", 23, 52, -2.1F, 8.0F, -2.0F, -0.1719F, -0.7703F, 0.2444F);
        addStrip(right, "rf31", 0, 52, -0.1F, 8.0F, 2.0F, 1.5708F, -1.3963F, -1.5708F);
        addStrip(right, "rf32", 0, 52, -2.1F, 8.0F, 0.0F, 0.0F, 0.0F, 0.1745F);
        addStrip(right, "rf33", 0, 52, -0.1F, 8.0F, -2.0F, -1.5708F, -1.3963F, 1.5708F);

        // Left boot: Desktop "left foot" remapped onto left_leg (y=8).
        PartDefinition left = root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(48, 36)
                        .addBox(-2.0F, BOOT_TOP_Y, -2.0F, 4.0F, BOOT_HEIGHT, 4.0F, d),
                PartPose.offset(1.9F, 12.0F, 0.0F));
        addStrip(left, "lf44", 23, 52, 2.1F, 8.0F, -2.0F, -2.9697F, -0.7703F, 2.8972F);
        addStrip(left, "lf45", 23, 52, 2.1F, 8.0F, 2.0F, 2.9697F, 0.7703F, 2.8972F);
        addStrip(left, "lf46", 0, 52, 0.1F, 8.0F, -2.0F, -1.5708F, -1.3963F, 1.5708F);
        addStrip(left, "lf47", 0, 52, 2.1F, 8.0F, 0.0F, 0.0F, 0.0F, -0.1745F);
        addStrip(left, "lf48", 0, 52, 0.1F, 8.0F, 2.0F, 1.5708F, -1.3963F, -1.5708F);

        return LayerDefinition.create(mesh, 128, 128);
    }

    private static void addStrip(
            PartDefinition parent, String name, int u, int v,
            float x, float y, float z, float rx, float ry, float rz) {
        parent.addOrReplaceChild(name,
                CubeListBuilder.create().texOffs(u, v)
                        .addBox(0.0F, 0.0F, -2.0F, STRIP_W, 5.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(x, y, z, rx, ry, rz));
    }
}
