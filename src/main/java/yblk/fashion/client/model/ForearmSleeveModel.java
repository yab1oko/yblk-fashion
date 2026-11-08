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
 * Outer-skin sleeve below the t-shirt hem.
 * <p>
 * Vanilla 2nd-layer sleeves are fully hidden while a shirt is worn; this mesh
 * redraws only from {@link TShirtModel#SLEEVE_HEM_Y} to the wrist so the cut
 * sits flush with the short sleeve and watches / bracelets stay visible.
 * <p>
 * Skin atlas: full outer sleeves at (40,32) / (48,48), height 12. Skip the
 * {@link TShirtModel#SLEEVE_HEIGHT} pixels covered by fabric, then sample the rest.
 */
public class ForearmSleeveModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_WIDE =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "forearm_sleeve_wide"), "main");
    public static final ModelLayerLocation LAYER_SLIM =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "forearm_sleeve_slim"), "main");

    /** Slightly under vanilla outer (0.25) so the redrawn forearm isn't bulky. */
    private static final CubeDeformation OUTER = new CubeDeformation(0.15F);

    /** Vanilla arm box: y=-2..10 (height 12). */
    private static final float ARM_TOP_Y = -2.0F;
    private static final float ARM_HEIGHT = 12.0F;

    /** Flush with t-shirt short-sleeve hem. */
    private static final float FOREARM_Y = TShirtModel.SLEEVE_HEM_Y;
    private static final float FOREARM_H = ARM_TOP_Y + ARM_HEIGHT - FOREARM_Y;

    /**
     * Pixels skipped on each sleeve face (under the shirt). Integer because
     * skin UV is 1 unit = 1 texel on the 64×64 atlas.
     */
    private static final int UV_SKIP = Math.round(TShirtModel.SLEEVE_HEIGHT);

    public ForearmSleeveModel(ModelPart root) {
        super(root);
        this.head.visible = false;
        this.hat.visible = false;
        this.body.visible = false;
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
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        // Full outer: right (40,32), left (48,48). +UV_SKIP moves sample to hem.
        int rightV = 32 + UV_SKIP;
        int leftV = 48 + UV_SKIP;

        if (slim) {
            root.addOrReplaceChild("right_arm",
                    CubeListBuilder.create().texOffs(40, rightV)
                            .addBox(-2.0F, FOREARM_Y, -2.0F, 3.0F, FOREARM_H, 4.0F, OUTER),
                    PartPose.offset(-5.0F, 2.5F, 0.0F));
            root.addOrReplaceChild("left_arm",
                    CubeListBuilder.create().texOffs(48, leftV)
                            .addBox(-1.0F, FOREARM_Y, -2.0F, 3.0F, FOREARM_H, 4.0F, OUTER),
                    PartPose.offset(5.0F, 2.5F, 0.0F));
        } else {
            root.addOrReplaceChild("right_arm",
                    CubeListBuilder.create().texOffs(40, rightV)
                            .addBox(-3.0F, FOREARM_Y, -2.0F, 4.0F, FOREARM_H, 4.0F, OUTER),
                    PartPose.offset(-5.0F, 2.0F, 0.0F));
            root.addOrReplaceChild("left_arm",
                    CubeListBuilder.create().texOffs(48, leftV)
                            .addBox(-1.0F, FOREARM_Y, -2.0F, 4.0F, FOREARM_H, 4.0F, OUTER),
                    PartPose.offset(5.0F, 2.0F, 0.0F));
        }

        return LayerDefinition.create(mesh, 64, 64);
    }
}
