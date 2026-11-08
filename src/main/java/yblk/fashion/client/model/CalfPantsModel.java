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
 * Outer-skin calves below the pants hem.
 * <p>
 * Vanilla 2nd-layer pants are fully hidden while pants are worn; this mesh
 * redraws only from {@link PantsModel#LEG_HEIGHT} to the ankle (or footwear
 * cuff) so the cut sits flush with the pant legs — same idea as
 * {@link CalfShortsModel} / {@link ForearmSleeveModel}.
 * <p>
 * Skin atlas: full outer pants at (0,32) / (0,48), height 12. Skip the
 * pixels covered by fabric ({@code Math.round} of hem), then sample the rest.
 */
public class CalfPantsModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "calf_pants"), "main");
    public static final ModelLayerLocation LAYER_TO_SHOE =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "calf_pants_to_shoe"), "main");
    public static final ModelLayerLocation LAYER_TO_BOOTS =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "calf_pants_to_boots"), "main");

    /** Slightly under vanilla outer (0.25). */
    private static final CubeDeformation OUTER = new CubeDeformation(0.15F);

    private static final float CALF_Y = PantsModel.LEG_HEIGHT;
    /** Must round — truncating 8.5→8 desyncs UV from the fabric hem. */
    private static final int UV_SKIP = Math.round(PantsModel.LEG_HEIGHT);

    public CalfPantsModel(ModelPart root) {
        super(root);
        this.head.visible = false;
        this.hat.visible = false;
        this.body.visible = false;
        this.leftArm.visible = false;
        this.rightArm.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        return create(12.0F - CALF_Y);
    }

    public static LayerDefinition createToShoe() {
        float h = SneakersModel.SHOE_TOP_Y - CALF_Y;
        return create(Math.max(0.05F, h));
    }

    public static LayerDefinition createToBoots() {
        float h = ShoesModel.SHOE_TOP_Y - CALF_Y;
        return create(Math.max(0.05F, h));
    }

    private static LayerDefinition create(float calfH) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);

        int rightV = 32 + UV_SKIP;
        int leftV = 48 + UV_SKIP;

        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(0, rightV)
                        .addBox(-2.0F, CALF_Y, -2.0F, 4.0F, calfH, 4.0F, OUTER),
                PartPose.offset(-1.9F, 12.0F, 0.0F));
        // Match PantsModel left-leg +Z so the cut sits on the same limb axis.
        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(0, leftV)
                        .addBox(-2.0F, CALF_Y, -2.0F, 4.0F, calfH, 4.0F, OUTER),
                PartPose.offset(1.9F, 12.0F, PantsModel.LEFT_LEG_Z));

        return LayerDefinition.create(mesh, 64, 64);
    }
}
