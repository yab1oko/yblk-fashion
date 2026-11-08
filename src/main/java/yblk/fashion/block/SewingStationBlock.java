package yblk.fashion.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import yblk.fashion.block.entity.SewingStationBlockEntity;

public class SewingStationBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final EnumProperty<SewingStationPart> PART =
            EnumProperty.create("part", SewingStationPart.class);

    /**
     * Collision / outline for FACING=north (model -X = west = SIDE).
     * Thin fabric sheets (~0.1) are intentionally omitted — walk-through.
     */
    private static final VoxelShape MAIN_NORTH = Shapes.or(
            Block.box(2, 0, 2, 14, 15, 14),
            Block.box(3, 3, 1, 13, 14, 2),
            Block.box(0, 15, 0, 16, 16, 16));
    private static final VoxelShape SIDE_NORTH = Shapes.or(
            Block.box(2, 0, 2, 4, 15, 4),
            Block.box(2, 0, 12, 4, 15, 14),
            Block.box(0, 15, 0, 16, 16, 16),
            Block.box(2, 16, 4, 8, 18, 10),
            Block.box(8, 16, 4, 14, 19, 10),
            Block.box(3, 18, 5, 7, 27, 9),
            Block.box(7, 24, 5, 14, 27, 9),
            Block.box(1, 23, 5, 2, 27, 9),
            Block.box(4, 16, 11, 9, 17, 16));

    private static final VoxelShape MAIN_SOUTH = rotateY(MAIN_NORTH, 180);
    private static final VoxelShape MAIN_WEST = rotateY(MAIN_NORTH, 270);
    private static final VoxelShape MAIN_EAST = rotateY(MAIN_NORTH, 90);
    private static final VoxelShape SIDE_SOUTH = rotateY(SIDE_NORTH, 180);
    private static final VoxelShape SIDE_WEST = rotateY(SIDE_NORTH, 270);
    private static final VoxelShape SIDE_EAST = rotateY(SIDE_NORTH, 90);

    public SewingStationBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(PART, SewingStationPart.MAIN));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    /** SIDE block sits on the model's -X side → counter-clockwise of FACING. */
    public static Direction getSideDirection(Direction facing) {
        return facing.getCounterClockWise();
    }

    public static BlockPos getMainPos(BlockState state, BlockPos pos) {
        if (state.getValue(PART) == SewingStationPart.MAIN) {
            return pos;
        }
        return pos.relative(getSideDirection(state.getValue(FACING)).getOpposite());
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos main = context.getClickedPos();
        BlockPos side = main.relative(getSideDirection(facing));
        Level level = context.getLevel();
        if (!level.getBlockState(side).canBeReplaced(context)
                || !level.getWorldBorder().isWithinBounds(side)) {
            return null;
        }
        return this.defaultBlockState()
                .setValue(FACING, facing)
                .setValue(PART, SewingStationPart.MAIN);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
                            ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            Direction facing = state.getValue(FACING);
            BlockPos side = pos.relative(getSideDirection(facing));
            level.setBlock(side, state.setValue(PART, SewingStationPart.SIDE), 3);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, 3);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.getValue(FACING);
        Direction toPartner = state.getValue(PART) == SewingStationPart.MAIN
                ? getSideDirection(facing)
                : getSideDirection(facing).getOpposite();
        if (direction == toPartner) {
            return neighborState.is(this) && neighborState.getValue(PART) != state.getValue(PART)
                    && neighborState.getValue(FACING) == facing
                    ? state
                    : Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && state.getValue(PART) == SewingStationPart.SIDE) {
            BlockPos main = getMainPos(state, pos);
            BlockState mainState = level.getBlockState(main);
            if (mainState.is(this) && mainState.getValue(PART) == SewingStationPart.MAIN) {
                // Harvest MAIN first so loot / BE contents drop when the side is broken.
                if (!player.isCreative()) {
                    BlockEntity be = level.getBlockEntity(main);
                    dropResources(mainState, level, main, be, player, player.getMainHandItem());
                }
                level.setBlock(main, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, main, Block.getId(mainState));
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    /** Non-cubic machine geometry otherwise gets crushed by smooth-lighting AO. */
    @Override
    @SuppressWarnings("deprecation")
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean main = state.getValue(PART) == SewingStationPart.MAIN;
        return switch (state.getValue(FACING)) {
            case SOUTH -> main ? MAIN_SOUTH : SIDE_SOUTH;
            case WEST -> main ? MAIN_WEST : SIDE_WEST;
            case EAST -> main ? MAIN_EAST : SIDE_EAST;
            default -> main ? MAIN_NORTH : SIDE_NORTH;
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockPos mainPos = getMainPos(state, pos);
            BlockEntity be = level.getBlockEntity(mainPos);
            if (be instanceof SewingStationBlockEntity station && player instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, station, mainPos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == SewingStationPart.MAIN
                ? new SewingStationBlockEntity(pos, state)
                : null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (state.getValue(PART) == SewingStationPart.MAIN) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof SewingStationBlockEntity station) {
                    station.dropContents(level, pos);
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    /** Rotate a north-oriented shape by {@code degrees} around Y (0/90/180/270). */
    private static VoxelShape rotateY(VoxelShape shape, int degrees) {
        VoxelShape[] buffer = {Shapes.empty()};
        shape.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            double x1 = minX;
            double z1 = minZ;
            double x2 = maxX;
            double z2 = maxZ;
            for (int i = 0; i < degrees / 90; i++) {
                double nx1 = 1.0 - z2;
                double nz1 = x1;
                double nx2 = 1.0 - z1;
                double nz2 = x2;
                x1 = Math.min(nx1, nx2);
                x2 = Math.max(nx1, nx2);
                z1 = Math.min(nz1, nz2);
                z2 = Math.max(nz1, nz2);
            }
            buffer[0] = Shapes.or(buffer[0], Shapes.box(x1, minY, z1, x2, maxY, z2));
        });
        return buffer[0];
    }
}
