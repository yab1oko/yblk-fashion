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

/** Ghillie top — Desktop {@code ghillie.java} Body + Arms. */
public class GhillieTopModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_WIDE =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "ghillie_top_wide"), "main");
    public static final ModelLayerLocation LAYER_SLIM =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "ghillie_top_slim"), "main");

    public static final float INFLATE = 0.20F;
    public static final float ARM_INFLATE = 0.18F;
    private static final float STRIP_W = 0.12F;
    private static final float FRINGE_Z = 0.12F;

    private boolean suppressArmorPass;

    public GhillieTopModel(ModelPart root) {
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

    public static LayerDefinition createWide() {
        return create(false);
    }

    public static LayerDefinition createSlim() {
        return create(true);
    }

    private static LayerDefinition create(boolean slim) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation body = new CubeDeformation(INFLATE);
        CubeDeformation arm = new CubeDeformation(ARM_INFLATE);

        PartDefinition torso = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(34, 20)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, body),
                PartPose.ZERO);
        addFringe(torso, "fringe_b1", 0.0F, 5.0F, 2.0F, 0.1745F, 0.0F, 0.0F);
        addFringe(torso, "fringe_b0", 0.0F, 0.0F, 2.0F, 0.1745F, 0.0F, 0.0F);
        addFringe(torso, "fringe_f1", 0.0F, 5.0F, -2.0F, -0.1745F, 0.0F, 0.0F);
        addFringe(torso, "fringe_f0", 0.0F, 0.0F, -2.0F, -0.1745F, 0.0F, 0.0F);

        if (slim) {
            PartDefinition ra = root.addOrReplaceChild("right_arm",
                    CubeListBuilder.create().texOffs(0, 36)
                            .addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, arm),
                    PartPose.offset(-5.0F, 2.5F, 0.0F));
            addRightArmFringe(ra, -0.5F);
            PartDefinition la = root.addOrReplaceChild("left_arm",
                    CubeListBuilder.create().texOffs(16, 36)
                            .addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, arm),
                    PartPose.offset(5.0F, 2.5F, 0.0F));
            addLeftArmFringe(la);
        } else {
            PartDefinition ra = root.addOrReplaceChild("right_arm",
                    CubeListBuilder.create().texOffs(0, 36)
                            .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, arm),
                    PartPose.offset(-5.0F, 2.0F, 0.0F));
            addRightArmFringe(ra, 0.0F);
            PartDefinition la = root.addOrReplaceChild("left_arm",
                    CubeListBuilder.create().texOffs(16, 36)
                            .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, arm),
                    PartPose.offset(5.0F, 2.0F, 0.0F));
            addLeftArmFringe(la);
        }

        return LayerDefinition.create(mesh, 128, 128);
    }

    private static void addRightArmFringe(PartDefinition arm, float xNudge) {
        float xOut = -3.0F + xNudge;
        float xIn = -1.0F + xNudge;
        // Desktop cube_r9..r18
        addStrip(arm, "r9", 23, 52, xOut, -2.0F, 2.0F, 0.2324F, 0.7725F, 0.3163F);
        addStrip(arm, "r10", 23, 52, xOut, 2.0F, 2.0F, 0.2324F, 0.7725F, 0.3163F);
        addStrip(arm, "r11", 23, 52, xOut, -2.0F, -2.0F, -0.2719F, -0.7329F, 0.4667F);
        addStrip(arm, "r12", 23, 52, xOut, 2.0F, -2.0F, -0.2719F, -0.7329F, 0.4667F);
        addStrip(arm, "r13", 0, 52, xIn, -2.0F, -2.0F, -1.5708F, -1.3963F, 1.5708F);
        addStrip(arm, "r14", 0, 52, xIn, 2.0F, -2.0F, -1.5708F, -1.3963F, 1.5708F);
        addStrip(arm, "r15", 0, 52, xIn, 2.0F, 2.0F, 1.5708F, -1.3963F, -1.5708F);
        addStrip(arm, "r16", 0, 52, xIn, -2.0F, 2.0F, 1.5708F, -1.3963F, -1.5708F);
        addStrip(arm, "r17", 0, 52, xOut, 2.0F, 0.0F, 0.0F, 0.0F, 0.1745F);
        addStrip(arm, "r18", 0, 52, xOut, -2.0F, 0.0F, 0.0F, 0.0F, 0.1745F);
    }

    private static void addLeftArmFringe(PartDefinition arm) {
        // Desktop cube_r19..r28
        addStrip(arm, "l19", 23, 52, 3.0F, -2.0F, 2.0F, 2.8697F, 0.7329F, 2.6749F);
        addStrip(arm, "l20", 23, 52, 3.0F, 2.0F, 2.0F, 2.8697F, 0.7329F, 2.6749F);
        addStrip(arm, "l21", 23, 52, 3.0F, 2.0F, -2.0F, -2.9092F, -0.7725F, 2.8253F);
        addStrip(arm, "l22", 23, 52, 3.0F, -2.0F, -2.0F, -2.9092F, -0.7725F, 2.8253F);
        addStrip(arm, "l23", 0, 52, 1.0F, -2.0F, -2.0F, -1.5708F, -1.3963F, 1.5708F);
        addStrip(arm, "l24", 0, 52, 1.0F, 2.0F, -2.0F, -1.5708F, -1.3963F, 1.5708F);
        addStrip(arm, "l25", 0, 52, 1.0F, 2.0F, 2.0F, 1.5708F, -1.3963F, -1.5708F);
        addStrip(arm, "l26", 0, 52, 1.0F, -2.0F, 2.0F, 1.5708F, -1.3963F, -1.5708F);
        addStrip(arm, "l27", 0, 52, 3.0F, 2.0F, 0.0F, 0.0F, 0.0F, -0.1745F);
        addStrip(arm, "l28", 0, 52, 3.0F, -2.0F, 0.0F, 0.0F, 0.0F, -0.1745F);
    }

    private static void addFringe(
            PartDefinition parent, String name, float x, float y, float z, float rx, float ry, float rz) {
        parent.addOrReplaceChild(name,
                CubeListBuilder.create().texOffs(40, 0)
                        .addBox(-4.0F, 0.0F, 0.0F, 8.0F, 7.0F, FRINGE_Z, CubeDeformation.NONE),
                PartPose.offsetAndRotation(x, y, z, rx, ry, rz));
    }

    private static void addStrip(
            PartDefinition parent, String name, int u, int v,
            float x, float y, float z, float rx, float ry, float rz) {
        // Desktop flats are width-0 at x=0; keep a thin volume for visibility.
        parent.addOrReplaceChild(name,
                CubeListBuilder.create().texOffs(u, v)
                        .addBox(0.0F, 0.0F, -2.0F, STRIP_W, 5.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(x, y, z, rx, ry, rz));
    }
}
