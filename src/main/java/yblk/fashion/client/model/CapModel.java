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
 * Cap from Blockbench {@code cap.java} UV layout (32×32).
 * <p>
 * Crown inflate &gt; 0.5 cuts the vanilla hat outer by occlusion ({@code hideHat=false}).
 * Visor is a 1px plate docked under the crown front so the hem seam does not stripe.
 */
public class CapModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "cap"), "main");

    /** Outside vanilla hat outer (0.5) — occlusion cut, no hideHat. */
    public static final float CROWN_INFLATE = 0.55F;
    /** Modest; must stay under crown inflate. */
    public static final float VISOR_INFLATE = 0.22F;
    public static final float HEM_Y = -4.5F;

    private boolean suppressArmorPass;

    public CapModel(ModelPart root) {
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

        CubeDeformation crown = new CubeDeformation(CROWN_INFLATE);
        CubeDeformation visor = new CubeDeformation(VISOR_INFLATE);
        // Crown: uniform 0.55 outside outer 0.5. Visor: 1 tall, deeper under crown (z=-8, d=4.6).
        root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 3.5F, 8.0F, crown)
                        .texOffs(0, 13).addBox(-4.0F, -5.25F, -8.0F, 8.0F, 1.0F, 4.6F, visor),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 32, 32);
    }
}
