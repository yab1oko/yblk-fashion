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
 * Sports bag from Desktop {@code sports_bag.java} — torso shell (PNG alpha = strap)
 * + hip duffel. Atlas 32 (textures may be 64×64).
 */
public class SportsBagModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "sports_bag"), "main");

    /** Above coat/jacket so the strap reads on top. */
    public static final float INFLATE = 0.35F;
    /** Slightly inside the shell so the duffel does not z-fight the torso cube. */
    private static final float BAG_INFLATE = 0.28F;

    public SportsBagModel(ModelPart root) {
        super(root);
        this.head.visible = false;
        this.hat.visible = false;
        this.leftArm.visible = false;
        this.rightArm.visible = false;
        this.leftLeg.visible = false;
        this.rightLeg.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation shell = new CubeDeformation(INFLATE);
        CubeDeformation bag = new CubeDeformation(BAG_INFLATE);

        // Body shell — opaque texels = strap (same UV as Desktop sports_bag.java).
        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(8, 16)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, shell),
                PartPose.ZERO);

        body.addOrReplaceChild("bag",
                CubeListBuilder.create()
                        .texOffs(0, 17).addBox(-3.0F, 7.0F, 0.0F, 3.0F, 1.0F, 2.0F, bag)
                        .texOffs(0, 14).addBox(-3.0F, -4.0F, 0.0F, 3.0F, 1.0F, 2.0F, bag)
                        .texOffs(0, 0).addBox(-3.0F, -3.0F, -1.0F, 4.0F, 10.0F, 4.0F, bag),
                PartPose.offsetAndRotation(-0.7F, 3.8F, 3.0F, 0.0F, 0.0F, -0.6981F));

        return LayerDefinition.create(mesh, 32, 32);
    }
}
