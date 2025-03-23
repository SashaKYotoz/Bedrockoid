package net.sashakyotoz.bedrockoid.common.world.features.custom;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.sashakyotoz.bedrockoid.common.world.features.custom.configs.FallenTreeFeatureConfig;

public class FallenTreeFeature extends Feature<FallenTreeFeatureConfig> {

    public static final Feature<FallenTreeFeatureConfig> INSTANCE = new FallenTreeFeature(FallenTreeFeatureConfig.CODEC);

    public FallenTreeFeature(Codec<FallenTreeFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<FallenTreeFeatureConfig> context) {
        WorldGenLevel world = context.level();
        BlockPos pos = context.origin();
        RandomSource random = context.random();
        BlockState trunk = context.config().logType().getState(random, context.origin());
        if (!context.config().isDoubleTrunk()) {
            int size = random.nextInt(3) + 3;
            int distance = random.nextInt(6) > 1 ? 1 : 2;
            BlockPos trunkTopPos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);
            if (!world.getBlockState(trunkTopPos).canBeReplaced() || !(world.getBlockState(trunkTopPos.below()).is(BlockTags.DIRT)))
                return false;

            world.setBlock(trunkTopPos, trunk.setValue(RotatedPillarBlock.AXIS, Direction.UP.getAxis()), 3);
            if (random.nextBoolean())
                world.setBlock(trunkTopPos.above(), random.nextInt(2) == 0 ? Blocks.RED_MUSHROOM.defaultBlockState() : Blocks.BROWN_MUSHROOM.defaultBlockState(), 3);
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos vinePos = trunkTopPos.relative(direction.getOpposite());
                if (world.getBlockState(vinePos).canBeReplaced() && (world.getRandom().nextInt(6) > 3))
                    world.setBlock(vinePos, Blocks.VINE.defaultBlockState().setValue(VineBlock.getPropertyForFace(direction), true), 3);
            }

            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            generateFallenTrunk(world, direction, size, distance + 1, trunkTopPos, 4, trunk);
        } else {
            int size = random.nextInt(3) + 3;
            int distance = random.nextInt(6) > 1 ? 1 : 2;
            BlockPos trunkTopPos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);

            if (!world.getBlockState(trunkTopPos).canBeReplaced() ||
                    !world.getBlockState(trunkTopPos.below()).is(BlockTags.DIRT)) {
                return false;
            }

            world.setBlock(trunkTopPos, trunk.setValue(RotatedPillarBlock.AXIS, Direction.UP.getAxis()), 3);
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            generateFallenTrunk2x2(world, direction, size, distance + 1, trunkTopPos, 21, trunk);
        }

        return true;
    }

    private boolean generateFallenTrunk(WorldGenLevel world, Direction direction, int size, int distance, BlockPos pos, int tries, BlockState trunk) {
        if (tries <= 0)
            return false;

        BlockPos start = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.relative(direction, distance));
        int maxY = start.getY();
        for (int i = 0; i < size; i++) {
            BlockPos temp = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, start.relative(direction, i));
            if (temp.getY() > maxY)
                maxY = temp.getY();
        }

        for (int i = 0; i < size; i++) {
            BlockPos.MutableBlockPos temp = start.relative(direction, i).mutable();
            temp.setY(maxY);
            if (((maxY - start.getY()) > 3 || (pos.getY() - maxY) > 3
                    || !world.getBlockState(temp).canBeReplaced() || world.getBlockState(temp.below()).liquid()
                    || world.getBlockState(temp.below()).is(BlockTags.LOGS) || world.getBlockState(temp.below()).is(Blocks.RED_MUSHROOM_BLOCK)
                    || world.getBlockState(temp.below()).is(Blocks.BROWN_MUSHROOM_BLOCK)) && i < 2)
                return generateFallenTrunk(world, direction.getClockWise(), size, distance, pos, tries - 1, trunk);
        }
        for (int i = 0; i < size; i++) {
            BlockPos.MutableBlockPos temp = start.relative(direction, i).mutable();
            temp.setY(maxY);
            if (world.getBlockState(temp).canBeReplaced()) {
                world.setBlock(temp, trunk.setValue(RotatedPillarBlock.AXIS, direction.getAxis()), 3);
                if (world.getBlockState(temp.above()).canBeReplaced())
                    world.setBlock(temp.above(), Blocks.AIR.defaultBlockState(), 3);
            } else
                break;
        }
        return true;
    }

    private boolean generateFallenTrunk2x2(
            WorldGenLevel world,
            Direction direction,
            int size,
            int distance,
            BlockPos pos,
            int tries,
            BlockState trunk
    ) {
        if (tries <= 0)
            return false;

        BlockPos start = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.relative(direction, distance));
        int maxY = start.getY();

        for (int i = 0; i < size; i++) {
            for (int dx = 0; dx < 2; dx++) {
                for (int dz = 0; dz < 2; dz++) {
                    BlockPos offsetPos = start.relative(direction, i).offset(dx, 0, dz);
                    BlockPos topPos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, offsetPos);
                    if (topPos.getY() > maxY)
                        maxY = topPos.getY();
                }
            }
        }
        for (int i = 0; i < size; i++) {
            for (int dx = 0; dx < 2; dx++) {
                for (int dz = 0; dz < 2; dz++) {
                    BlockPos offsetPos = start.relative(direction, i).offset(dx, 0, dz);
                    BlockPos topPos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, offsetPos);
                    boolean badTerrain = (
                            (topPos.getY() - start.getY()) > 3
                                    || (pos.getY() - topPos.getY()) > 3
                                    || !world.getBlockState(topPos).canBeReplaced()
                                    || world.getBlockState(topPos.below()).liquid()
                                    || world.getBlockState(topPos.below()).is(BlockTags.LOGS)
                                    || world.getBlockState(topPos.below()).is(Blocks.RED_MUSHROOM_BLOCK)
                                    || world.getBlockState(topPos.below()).is(Blocks.BROWN_MUSHROOM_BLOCK)
                    );

                    if (badTerrain) {
                        // If this occurs early, try rotating direction; otherwise fail
                        if (i < 3) {
                            return generateFallenTrunk2x2(
                                    world,
                                    direction.getClockWise(),
                                    size,
                                    distance,
                                    pos,
                                    tries - 1,
                                    trunk
                            );
                        } else
                            return false;
                    }
                }
            }
        }
        for (int i = 0; i < size; i++) {
            for (int dx = 0; dx < 2; dx++) {
                for (int dz = 0; dz < 2; dz++) {
                    BlockPos offsetPos = start.relative(direction, i).offset(dx, 0, dz);
                    BlockPos topPos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, offsetPos);

                    if (world.getBlockState(topPos).canBeReplaced()) {
                        world.setBlock(
                                topPos,
                                trunk.setValue(RotatedPillarBlock.AXIS, direction.getAxis()),
                                3
                        );
                        if (world.getBlockState(topPos.above()).canBeReplaced())
                            world.setBlock(topPos.above(), Blocks.AIR.defaultBlockState(), 3);
                        setVines(world, topPos);
                    }
                }
            }
        }
        return true;
    }

    public void setVines(WorldGenLevel world, BlockPos trunkPos) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            setVineOnTrunk(world, trunkPos, direction);
        }
    }

    public void setVineOnTrunk(WorldGenLevel world, BlockPos trunkPos, Direction direction) {
        BlockPos vinePos = trunkPos.relative(direction.getOpposite());
        if (world.getBlockState(vinePos).canBeReplaced() && (world.getRandom().nextInt(6) > 3))
            world.setBlock(vinePos, Blocks.VINE.defaultBlockState().setValue(VineBlock.getPropertyForFace(direction), true), 3);
    }
}