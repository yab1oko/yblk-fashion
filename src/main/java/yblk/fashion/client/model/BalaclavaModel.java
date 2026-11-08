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
 * Balaclava from Desktop {@code balaclava.java}: full head shell over skin.
 * UV 32×32 (textures may be 64×64).
 * <p>
 * Alone: isotropic {@link #SHELL} so crown and neck stay covered.
 * Under beanie / cowboy / straw (and other headwear): {@link #SHELL_UNDER_HAT}
 * sits inside those crowns so fabric does not poke through.
 */
public class BalaclavaModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "balaclava"), "main");
    public static final ModelLayerLocation LAYER_UNDER_HAT =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "balaclava_under_hat"), "main");

    /** Matches hat outer (0.5) — full cover when no headwear. */
    private static final CubeDeformation SHELL = new CubeDeformation(0.5F);
    /**
     * Inside tight hat crowns (beanie 0.42, straw 0.35, cowboy XZ 0.5 / Y 0).
     * Negative Y pulls the crown under cowboy/beanie tops without XZ pancake alone.
     */
    private static final CubeDeformation SHELL_UNDER_HAT = new CubeDeformation(0.28F, -0.15F, 0.28F);

    public BalaclavaModel(ModelPart root) {
        super(root);
        this.hat.visible = false;
        this.body.visible = false;
        this.leftArm.visible = false;
        this.rightArm.visible = false;
        this.leftLeg.visible = false;
        this.rightLeg.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        return create(SHELL);
    }

    public static LayerDefinition createUnderHatLayer() {
        return create(SHELL_UNDER_HAT);
    }

    private static LayerDefinition create(CubeDeformation shell) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, shell),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 32, 32);
    }
}
