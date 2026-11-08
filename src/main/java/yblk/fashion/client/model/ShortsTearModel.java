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

/** Shorts mesh with extra inflate so tear marks sit on top of the fabric. */
public class ShortsTearModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "shorts_tear"), "main");

    /** Keep close to the fabric — shorts silhouette shows inflate overhang more than pants. */
    public static final float INFLATE = ShortsModel.INFLATE + 0.025F;

    public ShortsTearModel(ModelPart root) {
        super(root);
        this.head.visible = false;
        this.hat.visible = false;
        this.body.visible = false;
        this.leftArm.visible = false;
        this.rightArm.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation d = new CubeDeformation(INFLATE);

        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(ShortsModel.UV_RIGHT_U, ShortsModel.UV_RIGHT_V)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, ShortsModel.LEG_HEIGHT, 4.0F, d),
                PartPose.offset(-1.9F, 12.0F, 0.0F));
        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(ShortsModel.UV_LEFT_U, ShortsModel.UV_LEFT_V)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, ShortsModel.LEG_HEIGHT, 4.0F, d),
                PartPose.offset(1.9F, 12.0F, ShortsModel.LEFT_LEG_Z));

        return LayerDefinition.create(mesh, 32, 32);
    }
}
