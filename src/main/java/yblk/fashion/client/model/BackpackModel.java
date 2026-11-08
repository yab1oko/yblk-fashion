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
 * Backpack from Desktop {@code backpack.java}: torso shell (PNG alpha = straps)
 * + pack body / lid / side pockets on the back. Atlas 64.
 * <p>
 * Pack cubes sit just outside the inflated shell surface so shared faces do not
 * z-fight (same inflate on coplanar boxes flickers).
 */
public class BackpackModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "backpack"), "main");

    /** Above coat/jacket so straps read on top. */
    public static final float INFLATE = 0.35F;
    /** Pack / lid / pockets — modest inflate, offset past the shell. */
    private static final float PACK_INFLATE = 0.08F;
    /** Start pack volume just past the shell's outer back / sides. */
    private static final float PACK_Z = 2.0F + INFLATE + 0.02F;
    private static final float POCKET_X = 4.0F + INFLATE + 0.02F;

    public BackpackModel(ModelPart root) {
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
        CubeDeformation pack = new CubeDeformation(PACK_INFLATE);
        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        // Shell — opaque texels = straps (2nd-layer cut by occlusion + alpha).
                        .texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, shell)
                        // Lid
                        .texOffs(22, 16).addBox(-4.0F, 1.0F, PACK_Z, 8.0F, 2.0F, 2.0F, pack)
                        // Main pack
                        .texOffs(0, 16).addBox(-4.0F, 3.0F, PACK_Z, 8.0F, 8.0F, 3.0F, pack)
                        // Side pockets
                        .texOffs(22, 27).addBox(POCKET_X, 6.0F, PACK_Z, 2.0F, 5.0F, 2.0F, pack)
                        .texOffs(22, 20).addBox(-(POCKET_X + 2.0F), 6.0F, PACK_Z, 2.0F, 5.0F, 2.0F, pack),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 64);
    }
}
