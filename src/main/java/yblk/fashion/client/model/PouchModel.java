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
 * Thigh pouches from Desktop {@code pouch_single.java} / {@code pouch_double.java}.
 * <p>
 * Leg shell inflate only (PNG alpha = straps). Pouch boxes stay at Blockbench
 * offsets with {@link CubeDeformation#NONE} — never re-offset with inflate.
 * Atlas 32 (textures may be 64×64).
 */
public class PouchModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_SINGLE =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "pouch_single"), "main");
    public static final ModelLayerLocation LAYER_DOUBLE =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "pouch_double"), "main");

    /** Outside pants/shorts (~0.08) so strap shell reads on top. */
    public static final float SHELL_INFLATE = 0.25F;

    public PouchModel(ModelPart root) {
        super(root);
        this.head.visible = false;
        this.hat.visible = false;
        this.body.visible = false;
        this.leftArm.visible = false;
        this.rightArm.visible = false;
    }

    public static LayerDefinition createSingle() {
        return create(false);
    }

    public static LayerDefinition createDouble() {
        return create(true);
    }

    private static LayerDefinition create(boolean bothLegs) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation shell = new CubeDeformation(SHELL_INFLATE);
        CubeDeformation pouch = CubeDeformation.NONE;

        // Right thigh — Desktop pouch_single / pouch_double Right Leg
        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, shell)
                        .texOffs(16, 0).addBox(-3.0F, 3.0F, -1.5F, 1.0F, 5.0F, 3.0F, pouch),
                PartPose.offset(-1.9F, 12.0F, 0.0F));

        if (bothLegs) {
            root.addOrReplaceChild("left_leg",
                    CubeListBuilder.create()
                            .texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, shell)
                            .texOffs(16, 8).addBox(2.0F, 3.0F, -1.5F, 1.0F, 5.0F, 3.0F, pouch),
                    PartPose.offset(1.9F, 12.0F, 0.0F));
        } else {
            root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
        }

        return LayerDefinition.create(mesh, 32, 32);
    }
}
