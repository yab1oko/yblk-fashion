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
 * Coat from Desktop {@code coat.java} (solid body cube — open front is a
 * transparent slit in the PNG). Inflate via {@link CubeDeformation}.
 * <p>
 * Sleeves are solid cubes (all faces). Open-tube / face-mask experiments caused
 * empty shoulders and arm-wide z-flicker; cuff lids are a separate follow-up.
 * Flaps use vanilla leg centering (not Blockbench z=+2).
 */
public class CoatModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_WIDE =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "coat_wide"), "main");
    public static final ModelLayerLocation LAYER_SLIM =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "coat_slim"), "main");

    public static final float INFLATE = 0.15F;
    public static final float ARM_INFLATE = 0.10F;
    public static final float SHELL = INFLATE;

    public static final float SLEEVE_HEIGHT = 10.0F;
    public static final float SLEEVE_TOP_Y = -2.0F;
    public static final float SLEEVE_END_Y = SLEEVE_TOP_Y + SLEEVE_HEIGHT;
    public static final float FLAP_HEM_Y = 4.0F;

    private boolean suppressArmorPass;

    public CoatModel(ModelPart root) {
        super(root);
        this.hat.visible = false;
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
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation body = new CubeDeformation(INFLATE);
        CubeDeformation arm = new CubeDeformation(ARM_INFLATE);

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(24, 0)
                        .addBox(-4.0F, -1.0F, 1.0F, 8.0F, 1.0F, 3.0F, body),
                PartPose.ZERO);

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, body),
                PartPose.ZERO);

        if (slim) {
            root.addOrReplaceChild("right_arm",
                    CubeListBuilder.create().texOffs(0, 16)
                            .addBox(-2.0F, SLEEVE_TOP_Y, -2.0F, 3.0F, SLEEVE_HEIGHT, 4.0F, arm),
                    PartPose.offset(-5.0F, 2.5F, 0.0F));
            root.addOrReplaceChild("left_arm",
                    CubeListBuilder.create().texOffs(16, 16)
                            .addBox(-1.0F, SLEEVE_TOP_Y, -2.0F, 3.0F, SLEEVE_HEIGHT, 4.0F, arm),
                    PartPose.offset(5.0F, 2.5F, 0.0F));
        } else {
            root.addOrReplaceChild("right_arm",
                    CubeListBuilder.create().texOffs(0, 16)
                            .addBox(-3.0F, SLEEVE_TOP_Y, -2.0F, 4.0F, SLEEVE_HEIGHT, 4.0F, arm),
                    PartPose.offset(-5.0F, 2.0F, 0.0F));
            root.addOrReplaceChild("left_arm",
                    CubeListBuilder.create().texOffs(16, 16)
                            .addBox(-1.0F, SLEEVE_TOP_Y, -2.0F, 4.0F, SLEEVE_HEIGHT, 4.0F, arm),
                    PartPose.offset(5.0F, 2.0F, 0.0F));
        }

        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(24, 4)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, FLAP_HEM_Y, 4.0F, body),
                PartPose.offset(-1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(0, 30)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, FLAP_HEM_Y, 4.0F, body),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }
}
