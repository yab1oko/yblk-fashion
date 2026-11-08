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
 * Front half of the player skin's hat layer (z=-4..0).
 * <p>
 * Open hoodie covers only the back of the head; the full hat layer is hidden so
 * hair doesn't poke through fabric — this mesh puts bangs / face overlay back.
 */
public class HoodFrontModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "hood_front"), "main");

    /** Match vanilla hat inflate. */
    private static final CubeDeformation HAT = new CubeDeformation(0.5F);

    public HoodFrontModel(ModelPart root) {
        super(root);
        this.hat.visible = false;
        this.body.visible = false;
        this.leftArm.visible = false;
        this.rightArm.visible = false;
        this.leftLeg.visible = false;
        this.rightLeg.visible = false;
    }

    public static LayerDefinition create() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        // Full hat: texOffs(32,0) 8×8×8, front face at (40,8).
        // Front half depth 4 → texOffs(36,4) keeps the front face on the same UV.
        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(36, 4)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 4.0F, HAT),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 64);
    }
}
