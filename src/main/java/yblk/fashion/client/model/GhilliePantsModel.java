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
 * Ghillie pants — upper legs (y 0…8).
 * Desktop: {@code Left Leg} + remapped {@code right foot} thigh + thigh strips from {@code Right Leg}.
 */
public class GhilliePantsModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "ghillie_pants"), "main");

    public static final float INFLATE = 0.18F;
    /** Meets {@link GhillieBootsModel#BOOT_TOP_Y}. */
    public static final float LEG_HEIGHT = 8.0F;
    private static final float STRIP_W = 0.12F;

    private boolean suppressArmorPass;

    public GhilliePantsModel(ModelPart root) {
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

        // Right thigh: Desktop "right foot" remapped onto humanoid right_leg.
        PartDefinition right = root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(32, 36)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, LEG_HEIGHT, 4.0F, d),
                PartPose.offset(-1.9F, 12.0F, 0.0F));
        // Thigh strips from Desktop Right Leg (y 0 / 4)
        addStrip(right, "rl34", 23, 52, -2.1F, 4.0F, 2.0F, 0.1719F, 0.7703F, 0.2444F);
        addStrip(right, "rl35", 23, 52, -2.1F, 0.0F, 2.0F, 0.1719F, 0.7703F, 0.2444F);
        addStrip(right, "rl36", 23, 52, -2.1F, 4.0F, -2.0F, -0.1719F, -0.7703F, 0.2444F);
        addStrip(right, "rl37", 23, 52, -2.1F, 0.0F, -2.0F, -0.1719F, -0.7703F, 0.2444F);
        addStrip(right, "rl38", 0, 52, -0.1F, 0.0F, -2.0F, -1.5708F, -1.3963F, 1.5708F);
        addStrip(right, "rl39", 0, 52, -2.1F, 0.0F, 0.0F, 0.0F, 0.0F, 0.1745F);
        addStrip(right, "rl40", 0, 52, -2.1F, 4.0F, 0.0F, 0.0F, 0.0F, 0.1745F);
        addStrip(right, "rl41", 0, 52, -0.1F, 0.0F, 2.0F, 1.5708F, -1.3963F, -1.5708F);
        addStrip(right, "rl42", 0, 52, -0.1F, 4.0F, 2.0F, 1.5708F, -1.3963F, -1.5708F);
        addStrip(right, "rl43", 0, 52, -0.1F, 4.0F, -2.0F, -1.5708F, -1.3963F, 1.5708F);

        PartDefinition left = root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(48, 36)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, LEG_HEIGHT, 4.0F, d),
                PartPose.offset(1.9F, 12.0F, 0.0F));
        addStrip(left, "ll49", 23, 52, 2.1F, 4.0F, 2.0F, 2.9697F, 0.7703F, 2.8972F);
        addStrip(left, "ll50", 23, 52, 2.1F, 4.0F, -2.0F, -2.9697F, -0.7703F, 2.8972F);
        addStrip(left, "ll51", 23, 52, 2.1F, 0.0F, -2.0F, -2.9697F, -0.7703F, 2.8972F);
        addStrip(left, "ll52", 23, 52, 2.1F, 0.0F, 2.0F, 2.9697F, 0.7703F, 2.8972F);
        addStrip(left, "ll53", 0, 52, 0.1F, 0.0F, -2.0F, -1.5708F, -1.3963F, 1.5708F);
        addStrip(left, "ll54", 0, 52, 2.1F, 0.0F, 0.0F, 0.0F, 0.0F, -0.1745F);
        addStrip(left, "ll55", 0, 52, 2.1F, 4.0F, 0.0F, 0.0F, 0.0F, -0.1745F);
        addStrip(left, "ll56", 0, 52, 0.1F, 0.0F, 2.0F, 1.5708F, -1.3963F, -1.5708F);
        addStrip(left, "ll57", 0, 52, 0.1F, 4.0F, 2.0F, 1.5708F, -1.3963F, -1.5708F);
        addStrip(left, "ll58", 0, 52, 0.1F, 4.0F, -2.0F, -1.5708F, -1.3963F, 1.5708F);

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
