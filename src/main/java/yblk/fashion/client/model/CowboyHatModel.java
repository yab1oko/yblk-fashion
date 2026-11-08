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
 * Cowboy hat from Blockbench {@code cowboy_hat.java}. UV 64×64.
 * Crown cubes 1–3: XZ inflate only. Flat brim / flaps: no deform.
 */
public class CowboyHatModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "cowboy_hat"), "main");

    /** Crown XZ grow — never Y (Y-inflate on ridges dug into cube 1 → flicker). */
    public static final float INFLATE = 0.5F;
    /** Whole hat shifted up 0.1 so cube1 clears the scalp. */
    private static final float LIFT = -0.1F;
    public static final float HEM_Y = -5.0F + LIFT;
    private static final float SIDE_Y = -5.25F + LIFT;
    private static final float SHELL_TOP = -8.0F + LIFT;
    private static final float RIDGE_Y = -9.0F + LIFT;
    /** 2nd-skin redraw under the raised brim. */
    public static final float OUTER_FACE_Y = -4.5F + LIFT;

    private boolean suppressArmorPass;

    public CowboyHatModel(ModelPart root) {
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

        // XZ only: cube1 bottom stays on HEM_Y (flush with brim); ridges don't dig into cube1.
        CubeDeformation crown = new CubeDeformation(INFLATE, 0.0F, INFLATE);
        CubeDeformation none = CubeDeformation.NONE;
        float shellH = HEM_Y - SHELL_TOP; // 3 — Blockbench

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 28).addBox(-4.0F, SHELL_TOP, -4.0F, 8.0F, shellH, 8.0F, crown)
                        .texOffs(30, 14).addBox(-4.0F, RIDGE_Y, -4.0F, 3.0F, 1.0F, 8.0F, crown)
                        .texOffs(32, 23).addBox(1.0F, RIDGE_Y, -4.0F, 3.0F, 1.0F, 8.0F, crown)
                        .texOffs(0, 0).addBox(-7.0F, HEM_Y, -7.0F, 14.0F, 0.0F, 14.0F, none),
                PartPose.ZERO);

        head.addOrReplaceChild("brim_sides",
                CubeListBuilder.create()
                        .texOffs(0, 14).addBox(-0.75F, 7.0F, -7.0F, 1.0F, 0.0F, 14.0F, none)
                        .texOffs(0, 14).addBox(-0.75F, -7.0F, -7.0F, 1.0F, 0.0F, 14.0F, none),
                PartPose.offsetAndRotation(0.0F, SIDE_Y, 0.0F, 0.0F, 0.0F, 1.5708F));

        return LayerDefinition.create(mesh, 64, 64);
    }
}
