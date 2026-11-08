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
 * Outer-skin calves below the shorts hem.
 * <p>
 * Full layer: hem → ankle. Shoe layers: hem → footwear cuff so outer skin
 * does not poke through sneakers/shoes.
 */
public class CalfShortsModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "calf_shorts"), "main");
    public static final ModelLayerLocation LAYER_TO_SHOE =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "calf_shorts_to_shoe"), "main");
    public static final ModelLayerLocation LAYER_TO_BOOTS =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "calf_shorts_to_boots"), "main");

    private static final CubeDeformation OUTER = new CubeDeformation(0.15F);
    private static final float CALF_Y = ShortsModel.LEG_HEIGHT;
    private static final int UV_SKIP = Math.round(ShortsModel.LEG_HEIGHT);

    public CalfShortsModel(ModelPart root) {
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
        return create(SneakersModel.SHOE_TOP_Y - CALF_Y);
    }

    public static LayerDefinition createToBoots() {
        return create(ShoesModel.SHOE_TOP_Y - CALF_Y);
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
        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(0, leftV)
                        .addBox(-2.0F, CALF_Y, -2.0F, 4.0F, calfH, 4.0F, OUTER),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }
}
