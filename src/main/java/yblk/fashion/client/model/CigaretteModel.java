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
 * Desktop {@code sigarette.java}: stick + smoke plane.
 * Smoke pivot sits on the lit tip (not above the cigarette).
 */
public class CigaretteModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "cigarette"), "main");

    public final ModelPart stick;
    public final ModelPart smoke;

    public CigaretteModel(ModelPart root) {
        super(root);
        this.hat.visible = false;
        this.body.visible = false;
        this.leftArm.visible = false;
        this.rightArm.visible = false;
        this.leftLeg.visible = false;
        this.rightLeg.visible = false;
        this.stick = this.head.getChild("stick");
        this.smoke = this.head.getChild("smoke");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        root.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);

        // Desktop cube_r2 — stick
        head.addOrReplaceChild("stick",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-0.5F, -0.5F, -1.5F, 1.0F, 1.0F, 3.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(1.8F, -1.5F, -5.2F, 0.0F, -0.3491F, 0.0F));

        // Smoke plane — tip-anchored. 64×64 PNG, LayerDef 16×16 (4 px / UV unit).
        // Zero-width cube: WEST/EAST each get depth×height UV (sides of the sheet).
        // Art: left wisp px0–8 + right wisp px8–16 at y16–32 (UV v4–8).
        // depth=2 → WEST (0,4)–(2,8), EAST (2,4)–(4,8); texOffsY+depth = 4 → texOffs(0,2).
        head.addOrReplaceChild("smoke",
                CubeListBuilder.create()
                        .texOffs(0, 2)
                        .addBox(0.5F, -4.0F, -1.0F, 0.0F, 4.0F, 2.0F, CubeDeformation.NONE),
                PartPose.offsetAndRotation(1.4F, -1.0F, -6.7F, 0.0F, -0.3491F, 0.0F));

        return LayerDefinition.create(mesh, 16, 16);
    }

    public void showStickOnly() {
        this.stick.visible = true;
        this.smoke.visible = false;
    }

    public void showSmokeOnly() {
        this.stick.visible = false;
        this.smoke.visible = true;
    }
}
