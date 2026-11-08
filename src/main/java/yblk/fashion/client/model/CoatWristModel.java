package yblk.fashion.client.model;

import java.util.EnumSet;
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
 * Skin wrists below the coat sleeve hem ({@link CoatModel#SLEEVE_END_Y}).
 * <p>
 * Omits {@link Direction#DOWN} (arm +Y toward hand → DOWN is the face against
 * the coat cuff). That plate was the black strip sitting ~2px past the sleeve
 * texture edge.
 */
public class CoatWristModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_WIDE =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "coat_wrist_wide"), "main");
    public static final ModelLayerLocation LAYER_SLIM =
            new ModelLayerLocation(new ResourceLocation(Fashion.MODID, "coat_wrist_slim"), "main");

    private static final float WRIST_Y = CoatModel.SLEEVE_END_Y;
    private static final float WRIST_H = 10.0F - WRIST_Y;
    private static final int UV_SKIP = Math.round(CoatModel.SLEEVE_HEIGHT);

    /** No DOWN (coat join). Keep UP / sides. */
    private static final Set<Direction> WRIST_FACES = EnumSet.of(
            Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);

    public CoatWristModel(ModelPart root) {
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

        int rightV = 32 + UV_SKIP;
        int leftV = 48 + UV_SKIP;

        if (slim) {
            root.addOrReplaceChild("right_arm",
                    CubeListBuilder.create().texOffs(40, rightV)
                            .addBox(-2.0F, WRIST_Y, -2.0F, 3.0F, WRIST_H, 4.0F, WRIST_FACES),
                    PartPose.offset(-5.0F, 2.5F, 0.0F));
            root.addOrReplaceChild("left_arm",
                    CubeListBuilder.create().texOffs(48, leftV)
                            .addBox(-1.0F, WRIST_Y, -2.0F, 3.0F, WRIST_H, 4.0F, WRIST_FACES),
                    PartPose.offset(5.0F, 2.5F, 0.0F));
        } else {
            root.addOrReplaceChild("right_arm",
                    CubeListBuilder.create().texOffs(40, rightV)
                            .addBox(-3.0F, WRIST_Y, -2.0F, 4.0F, WRIST_H, 4.0F, WRIST_FACES),
                    PartPose.offset(-5.0F, 2.0F, 0.0F));
            root.addOrReplaceChild("left_arm",
                    CubeListBuilder.create().texOffs(48, leftV)
                            .addBox(-1.0F, WRIST_Y, -2.0F, 4.0F, WRIST_H, 4.0F, WRIST_FACES),
                    PartPose.offset(5.0F, 2.0F, 0.0F));
        }

        return LayerDefinition.create(mesh, 64, 64);
    }
}
