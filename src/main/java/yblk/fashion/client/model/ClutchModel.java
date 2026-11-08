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
 * Clutch from Desktop {@code clutch.java}: body shell (PNG alpha = strap cut) + pouch.
 * {@link yblk.fashion.client.ClutchLayer} adds yaw 180° when a vest is worn.
 * Atlas 32 (textures may be 64×64, same as bandana).
 */
public class ClutchModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "clutch"), "main");

    /** Just above coat/jacket (~0.15) so the strap reads on top. */
    public static final float INFLATE = 0.35F;
    private static final float POUCH_INFLATE = 0.28F;

    public ClutchModel(ModelPart root) {
        super(root);
        this.head.visible = false;
        this.hat.visible = false;
        this.leftArm.visible = false;
        this.rightArm.visible = false;
        this.leftLeg.visible = false;
        this.rightLeg.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation shell = new CubeDeformation(INFLATE);
        CubeDeformation pouch = new CubeDeformation(POUCH_INFLATE);

        // Body shell — opaque texels = strap. Layer rotates yaw 180° (pouch on back).
        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, shell),
                PartPose.ZERO);
        body.addOrReplaceChild("pouch",
                CubeListBuilder.create().texOffs(0, 16)
                        .addBox(-2.0F, -2.0F, -1.0F, 5.0F, 3.0F, 2.0F, pouch),
                PartPose.offsetAndRotation(-1.7F, 8.3F, -3.0F, 0.0F, 0.0F, 0.2182F));
        body.addOrReplaceChild("flap",
                CubeListBuilder.create().texOffs(0, 21)
                        .addBox(-0.4894F, 0.0098F, 0.0F, 1.0F, 1.0F, 0.0F, new CubeDeformation(0.08F)),
                PartPose.offsetAndRotation(0.9128F, 7.8438F, -4.0F, -0.3927F, 0.0F, 0.2182F));

        return LayerDefinition.create(mesh, 32, 32);
    }
}
