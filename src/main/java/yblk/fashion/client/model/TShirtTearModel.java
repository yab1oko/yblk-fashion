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

/** Shirt mesh with extra inflate so tear marks sit on top of the fabric. */
public class TShirtTearModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_WIDE =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "tshirt_tear_wide"), "main");
    public static final ModelLayerLocation LAYER_SLIM =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "tshirt_tear_slim"), "main");

    public static final float INFLATE = TShirtModel.INFLATE + 0.04F;

    public TShirtTearModel(ModelPart root) {
        super(root);
        this.head.visible = false;
        this.hat.visible = false;
        this.leftLeg.visible = false;
        this.rightLeg.visible = false;
    }

    public static LayerDefinition createWide() {
        return create(false);
    }

    public static LayerDefinition createSlim() {
        return create(true);
    }

    private static LayerDefinition create(boolean slim) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation d = new CubeDeformation(INFLATE);
        CubeDeformation arm = new CubeDeformation(TShirtModel.ARM_INFLATE + 0.04F);

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, d),
                PartPose.ZERO);

        if (slim) {
            root.addOrReplaceChild("right_arm",
                    CubeListBuilder.create().texOffs(0, 24)
                            .addBox(-2.0F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, arm),
                    PartPose.offset(-5.0F, 2.5F, 0.0F));
            root.addOrReplaceChild("left_arm",
                    CubeListBuilder.create().texOffs(18, 24)
                            .addBox(-1.0F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, arm),
                    PartPose.offset(5.0F, 2.5F, 0.0F));
        } else {
            root.addOrReplaceChild("right_arm",
                    CubeListBuilder.create().texOffs(0, 16)
                            .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, arm),
                    PartPose.offset(-5.0F, 2.0F, 0.0F));
            root.addOrReplaceChild("left_arm",
                    CubeListBuilder.create().texOffs(16, 16)
                            .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, arm),
                    PartPose.offset(5.0F, 2.0F, 0.0F));
        }

        return LayerDefinition.create(mesh, 32, 32);
    }
}
