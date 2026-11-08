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
 * Night-vision goggles from Desktop {@code NVG.java}: head shell (PNG alpha = strap)
 * + dual lenses + bridge at Blockbench positions. Atlas 32 (texture may be 64×64).
 * <p>
 * Inflate only the strap shell — lenses stay at z=-6 with no deform so they keep
 * the original protrusion in front of the face.
 */
public class NvgModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "nvg"), "main");

    /** Outside hat outer so the strap reads over skin / under helmet draw order. */
    public static final float INFLATE = 0.55F;

    public NvgModel(ModelPart root) {
        super(root);
        this.hat.visible = false;
        this.body.visible = false;
        this.leftArm.visible = false;
        this.rightArm.visible = false;
        this.leftLeg.visible = false;
        this.rightLeg.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation shell = new CubeDeformation(INFLATE);
        CubeDeformation lens = CubeDeformation.NONE;
        root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, shell)
                        .texOffs(0, 16).addBox(-3.0F, -4.0F, -6.0F, 2.0F, 2.0F, 2.0F, lens)
                        .texOffs(0, 28).addBox(1.0F, -4.0F, -6.0F, 2.0F, 2.0F, 2.0F, lens)
                        .texOffs(24, 28).addBox(-1.0F, -6.0F, -6.0F, 2.0F, 2.0F, 2.0F, lens),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 32, 32);
    }
}
