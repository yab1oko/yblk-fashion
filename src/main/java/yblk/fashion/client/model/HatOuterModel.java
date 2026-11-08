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
 * Front/side skin hat walls below a fashion hat brim.
 * <p>
 * Full hat layer is hidden while headwear is on. A full-head redraw makes a
 * black neck/brim band — this only restores face/bangs (HoodFront-style).
 */
public class HatOuterModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "hat_outer"), "main");
    public static final ModelLayerLocation LAYER_COWBOY =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "hat_outer_cowboy"), "main");
    public static final ModelLayerLocation LAYER_STRAW =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "hat_outer_straw"), "main");

    /** Vanilla hat-layer inflate, applied only along each wall's plane. */
    private static final float GROW = 0.5F;
    private static final float HEAD_TOP_Y = -8.0F;
    private static final float FACE_BOTTOM_Y = -1.5F;

    public HatOuterModel(ModelPart root) {
        super(root);
        this.hat.visible = false;
        this.body.visible = false;
        this.leftArm.visible = false;
        this.rightArm.visible = false;
        this.leftLeg.visible = false;
        this.rightLeg.visible = false;
    }

    public static LayerDefinition create() {
        return create(HatModel.HEM_Y, false);
    }

    public static LayerDefinition createForCowboy() {
        return create(CowboyHatModel.OUTER_FACE_Y, true);
    }

    public static LayerDefinition createForStraw() {
        return create(StrawHatModel.OUTER_FACE_Y, true);
    }

    /**
     * @param wideBrim when true, skip Y-grow so walls don't poke into a flat brim plane.
     */
    private static LayerDefinition create(float faceY, boolean wideBrim) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation frontGrow = wideBrim
                ? new CubeDeformation(GROW, 0.0F, 0.0F)
                : new CubeDeformation(GROW, GROW, 0.0F);
        CubeDeformation sideGrow = wideBrim
                ? new CubeDeformation(0.0F, 0.0F, GROW)
                : new CubeDeformation(0.0F, GROW, GROW);

        float faceH = FACE_BOTTOM_Y - faceY;
        int uvSkip = Math.round(faceY - HEAD_TOP_Y);
        int v = 8 + uvSkip;
        float edge = 4.0F + GROW;
        root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(40, v)
                        .addBox(-4.0F, faceY, -edge, 8.0F, faceH, 0.0F, frontGrow)
                        .texOffs(36, v - 4)
                        .addBox(-edge, faceY, -4.0F, 0.0F, faceH, 4.0F, sideGrow)
                        .texOffs(44, v - 4)
                        .addBox(edge, faceY, -4.0F, 0.0F, faceH, 4.0F, sideGrow),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 64);
    }
}
