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
 * T-shirt from Blockbench Java exports ({@code test2.java} / {@code tshirt_alex.java}).
 * UV space 32×32; PNGs are 64×64 HD (2 texels per UV). Steve arms 4px; Alex 3px.
 */
public class TShirtModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_WIDE =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "tshirt_wide"), "main");
    public static final ModelLayerLocation LAYER_SLIM =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "tshirt_slim"), "main");

    public static final float INFLATE = 0.08F;
    /** Thinner sleeves reduce arm∩body cube intersection seams. */
    public static final float ARM_INFLATE = 0.03F;

    /** Arm-local Y where the short sleeve box starts (vanilla arm top). */
    public static final float SLEEVE_TOP_Y = -2.0F;
    /** Short sleeve length along the arm (Steve/Alex mesh). */
    public static final float SLEEVE_HEIGHT = 4.0F;
    /** Arm-local Y of the sleeve hem — 2nd-layer cut / forearm redraw starts here. */
    public static final float SLEEVE_HEM_Y = SLEEVE_TOP_Y + SLEEVE_HEIGHT;

    /**
     * HumanoidArmorLayer calls setPartVisibility after getHumanoidArmorModel, which would
     * re-enable body/arms. When true, this model draws nothing so only ClothingRenderLayer paints.
     */
    private boolean suppressArmorPass;

    public TShirtModel(ModelPart root) {
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

    /** Steve — {@code test2.java} */
    public static LayerDefinition createWide() {
        return create(false);
    }

    /** Alex — {@code tshirt_alex.java} */
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

        CubeDeformation d = new CubeDeformation(INFLATE);
        CubeDeformation arm = new CubeDeformation(ARM_INFLATE);

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, d),
                PartPose.ZERO);

        if (slim) {
            // Alex short sleeves — texOffs(0, 24) / (18, 24), 3×4×4
            root.addOrReplaceChild("right_arm",
                    CubeListBuilder.create().texOffs(0, 24)
                            .addBox(-2.0F, SLEEVE_TOP_Y, -2.0F, 3.0F, SLEEVE_HEIGHT, 4.0F, arm),
                    PartPose.offset(-5.0F, 2.5F, 0.0F));
            root.addOrReplaceChild("left_arm",
                    CubeListBuilder.create().texOffs(18, 24)
                            .addBox(-1.0F, SLEEVE_TOP_Y, -2.0F, 3.0F, SLEEVE_HEIGHT, 4.0F, arm),
                    PartPose.offset(5.0F, 2.5F, 0.0F));
        } else {
            // Steve short sleeves — texOffs(0, 16) / (16, 16), 4×4×4
            root.addOrReplaceChild("right_arm",
                    CubeListBuilder.create().texOffs(0, 16)
                            .addBox(-3.0F, SLEEVE_TOP_Y, -2.0F, 4.0F, SLEEVE_HEIGHT, 4.0F, arm),
                    PartPose.offset(-5.0F, 2.0F, 0.0F));
            root.addOrReplaceChild("left_arm",
                    CubeListBuilder.create().texOffs(16, 16)
                            .addBox(-1.0F, SLEEVE_TOP_Y, -2.0F, 4.0F, SLEEVE_HEIGHT, 4.0F, arm),
                    PartPose.offset(5.0F, 2.0F, 0.0F));
        }

        return LayerDefinition.create(mesh, 32, 32);
    }
}
