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
 * Outer-skin legs below the coat flap hem.
 * <p>
 * Coat hides full pant outer layers; this redraws from {@link CoatModel#FLAP_HEM_Y}
 * to the ankle (or footwear cuff). Skip when fashion pants/shorts already own the cut.
 * Only the calf band — no zero-thickness gap planes (those read as stray cubes).
 */
public class CoatLegModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "coat_leg"), "main");
    public static final ModelLayerLocation LAYER_TO_SHOE =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "coat_leg_to_shoe"), "main");
    public static final ModelLayerLocation LAYER_TO_BOOTS =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "coat_leg_to_boots"), "main");

    /**
     * Just inside the coat shell so the redraw sits on the leg and the flap
     * still covers it. No Y growth — the band must not climb over the hem.
     */
    private static final CubeDeformation OUTER = new CubeDeformation(0.10F, 0.0F, 0.10F);
    private static final float CALF_Y = CoatModel.FLAP_HEM_Y;
    private static final int UV_SKIP = Math.round(CoatModel.FLAP_HEM_Y);

    public CoatLegModel(ModelPart root) {
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
        return create(Math.max(0.05F, SneakersModel.SHOE_TOP_Y - CALF_Y));
    }

    public static LayerDefinition createToBoots() {
        return create(Math.max(0.05F, ShoesModel.SHOE_TOP_Y - CALF_Y));
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
                CubeListBuilder.create()
                        .texOffs(0, rightV)
                        .addBox(-2.0F, CALF_Y, -2.0F, 4.0F, calfH, 4.0F, OUTER),
                PartPose.offset(-1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create()
                        .texOffs(0, leftV)
                        .addBox(-2.0F, CALF_Y, -2.0F, 4.0F, calfH, 4.0F, OUTER),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }
}
