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
 * Outer-skin shin above footwear.
 * Full left/right pants layers are hidden while sneakers/shoes are worn; this
 * redraws only the uncovered upper leg outer skin (inverse of {@link CalfPantsModel}).
 */
public class ShinOuterModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "shin_outer"), "main");
    public static final ModelLayerLocation LAYER_SHOES =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "shin_outer_shoes"), "main");

    private static final CubeDeformation OUTER = new CubeDeformation(0.15F);

    public ShinOuterModel(ModelPart root) {
        super(root);
        this.head.visible = false;
        this.hat.visible = false;
        this.body.visible = false;
        this.leftArm.visible = false;
        this.rightArm.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        return create(SneakersModel.SHOE_TOP_Y);
    }

    public static LayerDefinition createForShoes() {
        return create(ShoesModel.SHOE_TOP_Y);
    }

    private static LayerDefinition create(float shinH) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);

        // Skin atlas outer pants: right (0,32), left (0,48) — take the top shinH pixels.
        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(0, 32)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, shinH, 4.0F, OUTER),
                PartPose.offset(-1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(0, 48)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, shinH, 4.0F, OUTER),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }
}
