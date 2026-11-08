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

/** Hoodie mesh with extra inflate so tear marks sit on top of the fabric. */
public class HoodieTearModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_CLOSED_WIDE =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "hoodie_tear_closed_wide"), "main");
    public static final ModelLayerLocation LAYER_CLOSED_SLIM =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "hoodie_tear_closed_slim"), "main");
    public static final ModelLayerLocation LAYER_OPEN_WIDE =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "hoodie_tear_open_wide"), "main");
    public static final ModelLayerLocation LAYER_OPEN_SLIM =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "hoodie_tear_open_slim"), "main");

    public static final float INFLATE = HoodieModel.INFLATE + 0.06F;

    public HoodieTearModel(ModelPart root) {
        super(root);
        this.hat.visible = false;
        this.leftLeg.visible = false;
        this.rightLeg.visible = false;
    }

    public static LayerDefinition createClosedWide() {
        return create(false, false);
    }

    public static LayerDefinition createClosedSlim() {
        return create(true, false);
    }

    public static LayerDefinition createOpenWide() {
        return create(false, true);
    }

    public static LayerDefinition createOpenSlim() {
        return create(true, true);
    }

    private static LayerDefinition create(boolean slim, boolean hoodOpen) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation d = new CubeDeformation(INFLATE);
        CubeDeformation arm = new CubeDeformation(HoodieModel.ARM_INFLATE + 0.06F);
        CubeDeformation hood = new CubeDeformation(HoodieModel.HOOD_INFLATE + 0.06F);

        if (hoodOpen) {
            root.addOrReplaceChild("head",
                    CubeListBuilder.create().texOffs(4, 4)
                            .addBox(-4.0F, -8.0F, 0.0F, 8.0F, 8.0F, 4.0F, hood),
                    PartPose.ZERO);
        } else {
            root.addOrReplaceChild("head",
                    CubeListBuilder.create().texOffs(40, 0)
                            .addBox(-4.0F, -3.0F, 0.0F, 8.0F, 3.0F, 4.0F, hood),
                    PartPose.ZERO);
        }

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, d),
                PartPose.ZERO);

        if (slim) {
            root.addOrReplaceChild("right_arm",
                    CubeListBuilder.create().texOffs(24, 49)
                            .addBox(-2.0F, -2.0F, -2.0F, 3.0F, 11.0F, 4.0F, arm),
                    PartPose.offset(-5.0F, 2.5F, 0.0F));
            root.addOrReplaceChild("left_arm",
                    CubeListBuilder.create().texOffs(0, 49)
                            .addBox(-1.0F, -2.0F, -2.0F, 3.0F, 11.0F, 4.0F, arm),
                    PartPose.offset(5.0F, 2.5F, 0.0F));
        } else {
            root.addOrReplaceChild("right_arm",
                    CubeListBuilder.create().texOffs(24, 16)
                            .addBox(-3.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, arm),
                    PartPose.offset(-5.0F, 2.0F, 0.0F));
            root.addOrReplaceChild("left_arm",
                    CubeListBuilder.create().texOffs(0, 32)
                            .addBox(-1.0F, -2.0F, -2.0F, 4.0F, 11.0F, 4.0F, arm),
                    PartPose.offset(5.0F, 2.0F, 0.0F));
        }

        return LayerDefinition.create(mesh, 64, 64);
    }
}
