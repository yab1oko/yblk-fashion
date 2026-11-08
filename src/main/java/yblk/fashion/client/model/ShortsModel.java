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
 * Shorts from Blockbench. Textures are 64×64 with 2×-painted UV islands at
 * (16,12) / (16,32); LayerDefinition stays 32×32 so each UV unit maps to 2 texels.
 */
public class ShortsModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "shorts"), "main");

    public static final float INFLATE = 0.08F;
    /** Hem Y — calf outer redraw starts here. */
    public static final float LEG_HEIGHT = 6.0F;
    public static final float LEFT_LEG_Z = 0.04F;

    /** UV origin in 32-space (= pixel/2 on the 64 atlas). */
    public static final int UV_RIGHT_U = 8;
    public static final int UV_RIGHT_V = 6;
    public static final int UV_LEFT_U = 8;
    public static final int UV_LEFT_V = 16;

    private boolean suppressArmorPass;

    public ShortsModel(ModelPart root) {
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
        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(UV_RIGHT_U, UV_RIGHT_V)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, LEG_HEIGHT, 4.0F, d),
                PartPose.offset(-1.9F, 12.0F, 0.0F));
        // Left island is already painted separately — do not mirror (that flips the hem pattern).
        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(UV_LEFT_U, UV_LEFT_V)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, LEG_HEIGHT, 4.0F, d),
                PartPose.offset(1.9F, 12.0F, LEFT_LEG_Z));

        return LayerDefinition.create(mesh, 32, 32);
    }
}
