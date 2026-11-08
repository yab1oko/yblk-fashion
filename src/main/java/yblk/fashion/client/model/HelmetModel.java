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
 * Tactical helmet from Blockbench {@code helmet.java} — shell + rails, UV 64×64.
 * <p>
 * Inflate &gt; 0.5 cuts the vanilla hat outer by occlusion ({@code hideHat=false}).
 * Rails / top plate use a slightly larger deform so they sit outside the shell
 * and do not share coplanar faces (z-flicker). Open hoodie/jacket uses
 * {@link HoodieModel#HOOD_INFLATE_OVER_HAT} / {@link JacketModel#HOOD_INFLATE_OVER_HAT}
 * over this envelope.
 */
public class HelmetModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "helmet"), "main");

    /** Outside vanilla hat outer (0.5) — occlusion cut, no hideHat. */
    public static final float INFLATE = 0.55F;
    /** Rails + top plate sit just outside the shell to avoid z-fighting. */
    public static final float DETAIL_INFLATE = INFLATE + 0.06F;

    private boolean suppressArmorPass;

    public HelmetModel(ModelPart root) {
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

        CubeDeformation shell = new CubeDeformation(INFLATE);
        CubeDeformation detail = new CubeDeformation(DETAIL_INFLATE);
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, shell)
                        .texOffs(0, 25).addBox(3.5F, -8.0F, -4.0F, 1.0F, 4.0F, 8.0F, detail)
                        .texOffs(18, 25).addBox(-4.5F, -8.0F, -4.0F, 1.0F, 4.0F, 8.0F, detail)
                        .texOffs(0, 16).addBox(-3.5F, -8.5F, -4.0F, 7.0F, 1.0F, 8.0F, detail),
                PartPose.ZERO);

        // Front rail (Blockbench Head_r1)
        head.addOrReplaceChild("rail_front",
                CubeListBuilder.create().texOffs(0, 54)
                        .addBox(-0.5F, -2.0F, -4.0F, 1.0F, 2.0F, 8.0F, detail),
                PartPose.offsetAndRotation(0.0F, -6.0F, -4.0F, 0.0F, -1.5708F, 0.0F));
        // Back rail (Blockbench Head_r2)
        head.addOrReplaceChild("rail_back",
                CubeListBuilder.create().texOffs(0, 25)
                        .addBox(-0.5F, -2.0F, -4.0F, 1.0F, 4.0F, 8.0F, detail),
                PartPose.offsetAndRotation(0.0F, -6.0F, 4.0F, 0.0F, -1.5708F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }
}
