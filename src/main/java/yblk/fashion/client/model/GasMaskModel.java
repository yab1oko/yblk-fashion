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
 * Gas mask from Desktop Blockbench export — head shell + eye lenses + canister mount.
 * Filter cans: none / one (left) / two. UV atlas 32 (texture may be 64×64, same as NVG).
 * <p>
 * Shell inflate {@link #SHELL} sits outside the player hat layer (leave 2nd layer on).
 * Lens cubes: Blockbench {@code 2×2×1} at {@code z=-5.0}, light XY inflate ({@link #LENS}).
 * XY centers are scaled by shell inflate so they stay under the front-face eye cutouts.
 */
public class GasMaskModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_ZERO =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "gas_mask_zero"), "main");
    public static final ModelLayerLocation LAYER_ONE =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "gas_mask_one"), "main");
    public static final ModelLayerLocation LAYER_TWO =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "gas_mask_two"), "main");

    /** Outside vanilla hat (0.5) so opaque mask covers 2nd skin; holes show it through alpha. */
    public static final float SHELL = 0.55F;
    /** Slight XY grow so lens rims meet the inflated shell — well below {@link #SHELL}. */
    public static final float LENS = 0.15F;

    public GasMaskModel(ModelPart root) {
        super(root);
        this.hat.visible = false;
        this.body.visible = false;
        this.leftArm.visible = false;
        this.rightArm.visible = false;
        this.leftLeg.visible = false;
        this.rightLeg.visible = false;
    }

    public static LayerDefinition createZero() {
        return create(0);
    }

    public static LayerDefinition createOne() {
        return create(1);
    }

    public static LayerDefinition createTwo() {
        return create(2);
    }

    private static LayerDefinition create(int filters) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation shell = new CubeDeformation(SHELL);
        CubeDeformation lens = new CubeDeformation(LENS, LENS, 0.0F);
        CubeDeformation detail = CubeDeformation.NONE;
        // Front-face UVs stretch with SHELL away from (0, -4); keep lens centers on the holes.
        float faceScale = (8.0F + 2.0F * SHELL) / 8.0F;
        float lensCX = 2.0F * faceScale;
        float lensCY = -4.0F;
        float lensZ = -5.0F;
        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, shell)
                        .texOffs(0, 0).addBox(-lensCX - 1.0F, lensCY - 1.0F, lensZ, 2.0F, 2.0F, 1.0F, lens)
                        .texOffs(0, 0).addBox(lensCX - 1.0F, lensCY - 1.0F, lensZ, 2.0F, 2.0F, 1.0F, lens)
                        .texOffs(0, 27).addBox(-2.0F, -3.0F, -5.5F, 4.0F, 3.0F, 2.0F, detail),
                PartPose.ZERO);

        if (filters >= 1) {
            head.addOrReplaceChild("filter_left",
                    CubeListBuilder.create()
                            .texOffs(26, 28).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, detail),
                    PartPose.offsetAndRotation(-2.5F, -0.7F, -5.4F, 0.3927F, 0.0F, 0.0F));
        }
        if (filters >= 2) {
            head.addOrReplaceChild("filter_right",
                    CubeListBuilder.create()
                            .texOffs(26, 28).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, detail),
                    PartPose.offsetAndRotation(2.5F, -0.7F, -5.4F, 0.3927F, 0.0F, 0.0F));
        }

        return LayerDefinition.create(mesh, 32, 32);
    }
}
