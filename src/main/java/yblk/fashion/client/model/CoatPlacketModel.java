package yblk.fashion.client.model;

import java.util.Set;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Direction;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import yblk.fashion.Fashion;

/**
 * Outer-skin strip visible through the coat's open-front PNG slit.
 * <p>
 * Coat hides the full jacket layer; this redraws only the center band from the
 * skin atlas (jacket front UV), sitting just inside the inflated coat front.
 */
public class CoatPlacketModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "coat_placket"), "main");

    /** Just inside coat front ({@link CoatModel#INFLATE}) so the strip reads through the slit. */
    private static final float Z = -2.0F - CoatModel.INFLATE + 0.02F;
    /** Matches ~2px slit on the 8-wide body front. */
    private static final float PLACKET_W = 2.0F;
    /** Jacket front outer: (20,36); center 2px starts at u=23. */
    private static final int UV_U = 23;
    private static final int UV_V = 36;

    public CoatPlacketModel(ModelPart root) {
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

        root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(UV_U, UV_V)
                        .addBox(-PLACKET_W * 0.5F, 0.0F, Z, PLACKET_W, 12.0F, 0.0F, Set.of(Direction.NORTH)),
                PartPose.ZERO);

        return LayerDefinition.create(mesh, 64, 64);
    }
}
