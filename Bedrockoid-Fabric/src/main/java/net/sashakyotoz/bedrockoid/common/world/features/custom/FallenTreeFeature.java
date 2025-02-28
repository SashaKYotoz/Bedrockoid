package net.sashakyotoz.bedrockoid.common.world.features.custom;

import com.mojang.serialization.Codec;
import net.minecraft.block.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.sashakyotoz.bedrockoid.common.snow.BedrockSnowManager;
import net.sashakyotoz.bedrockoid.common.world.features.custom.configs.FallenTreeFeatureConfig;

public class FallenTreeFeature extends Feature<FallenTreeFeatureConfig> {

    public static final Feature<FallenTreeFeatureConfig> INSTANCE = new FallenTreeFeature(FallenTreeFeatureConfig.CODEC);

    public FallenTreeFeature(Codec<FallenTreeFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<FallenTreeFeatureConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos pos = context.getOrigin();
        Random random = context.getRandom();
        BlockState trunk = context.getConfig().logType().get(random, context.getOrigin());
        if (!context.getConfig().isDoubleTrunk()) {
            int size = random.nextInt(3) + 3;
            int distance = random.nextInt(6) > 1 ? 1 : 2;
            BlockPos trunkTopPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos);
            if (!world.getBlockState(trunkTopPos).isReplaceable() || !(world.getBlockState(trunkTopPos.down()).isIn(BlockTags.DIRT)))
                return false;

            world.setBlockState(trunkTopPos, trunk.with(PillarBlock.AXIS, Direction.UP.getAxis()), 3);
            if (random.nextBoolean())
                world.setBlockState(trunkTopPos.up(), random.nextInt(2) == 0 ? Blocks.RED_MUSHROOM.getDefaultState() : Blocks.BROWN_MUSHROOM.getDefaultState(), 3);
            for (Direction direction : Direction.Type.HORIZONTAL) {
                BlockPos vinePos = trunkTopPos.offset(direction.getOpposite());
                if (world.getBlockState(vinePos).isReplaceable() && (world.getRandom().nextInt(6) > 3))
                    world.setBlockState(vinePos, Blocks.VINE.getDefaultState().with(VineBlock.getFacingProperty(direction), true), 3);
            }

            Direction direction = Direction.Type.HORIZONTAL.random(random);
            generateFallenTrunk(world, direction, size, distance + 1, trunkTopPos, 4, trunk);
        } else {
            int size = random.nextInt(3) + 3;
            int distance = random.nextInt(6) > 1 ? 1 : 2;
            BlockPos trunkTopPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos);

            if (!world.getBlockState(trunkTopPos).isReplaceable() ||
                    !world.getBlockState(trunkTopPos.down()).isIn(BlockTags.DIRT)) {
                return false;
            }

            world.setBlockState(trunkTopPos, trunk.with(PillarBlock.AXIS, Direction.UP.getAxis()), 3);
            for (Direction direction : Direction.Type.HORIZONTAL) {
                BlockPos vinePos = trunkTopPos.offset(direction.getOpposite());
                if (world.getBlockState(vinePos).isReplaceable() && (random.nextInt(6) > 3)) {
                    world.setBlockState(vinePos,
                            Blocks.VINE.getDefaultState().with(VineBlock.getFacingProperty(direction), true),
                            3
                    );
                }
            }
            Direction direction = Direction.Type.HORIZONTAL.random(random);
            generateFallenTrunk2x2(world, direction, size, distance + 1, trunkTopPos, 21, trunk);
        }

        return true;
    }

    private boolean generateFallenTrunk(StructureWorldAccess world, Direction direction, int size, int distance, BlockPos pos, int tries, BlockState trunk) {
        if (tries <= 0)
            return false;

        BlockPos start = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.offset(direction, distance));
        int maxY = start.getY();
        for (int i = 0; i < size; i++) {
            BlockPos temp = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, start.offset(direction, i));
            if (temp.getY() > maxY)
                maxY = temp.getY();
        }

        for (int i = 0; i < size; i++) {
            BlockPos.Mutable temp = start.offset(direction, i).mutableCopy();
            temp.setY(maxY);
            if (((maxY - start.getY()) > 3 || (pos.getY() - maxY) > 3
                    || !world.getBlockState(temp).isReplaceable() || world.getBlockState(temp.down()).isLiquid()
                    || world.getBlockState(temp.down()).isIn(BlockTags.LOGS) || world.getBlockState(temp.down()).isOf(Blocks.RED_MUSHROOM_BLOCK)
                    || world.getBlockState(temp.down()).isOf(Blocks.BROWN_MUSHROOM_BLOCK)) && i < 2)
                return generateFallenTrunk(world, direction.rotateYClockwise(), size, distance, pos, tries - 1, trunk);
        }
        for (int i = 0; i < size; i++) {
            BlockPos.Mutable temp = start.offset(direction, i).mutableCopy();
            temp.setY(maxY);
            if (world.getBlockState(temp).isReplaceable()) {
                world.setBlockState(temp, trunk.with(PillarBlock.AXIS, direction.getAxis()), 3);
                if (world.getBlockState(temp.up()).isReplaceable())
                    world.setBlockState(temp.up(), Blocks.AIR.getDefaultState(), 3);
            } else
                break;
        }
        return true;
    }

    private boolean generateFallenTrunk2x2(
            StructureWorldAccess world,
            Direction direction,
            int size,
            int distance,
            BlockPos pos,
            int tries,
            BlockState trunk
    ) {
        if (tries <= 0)
            return false;

        BlockPos start = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.offset(direction, distance));
        int maxY = start.getY();

        for (int i = 0; i < size; i++) {
            for (int dx = 0; dx < 2; dx++) {
                for (int dz = 0; dz < 2; dz++) {
                    BlockPos offsetPos = start.offset(direction, i).add(dx, 0, dz);
                    BlockPos topPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, offsetPos);
                    if (topPos.getY() > maxY)
                        maxY = topPos.getY();
                }
            }
        }
        for (int i = 0; i < size; i++) {
            for (int dx = 0; dx < 2; dx++) {
                for (int dz = 0; dz < 2; dz++) {
                    BlockPos offsetPos = start.offset(direction, i).add(dx, 0, dz);
                    BlockPos topPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, offsetPos);
                    boolean badTerrain = (
                            (topPos.getY() - start.getY()) > 3
                                    || (pos.getY() - topPos.getY()) > 3
                                    || !world.getBlockState(topPos).isReplaceable()
                                    || world.getBlockState(topPos.down()).isLiquid()
                                    || world.getBlockState(topPos.down()).isIn(BlockTags.LOGS)
                                    || world.getBlockState(topPos.down()).isOf(Blocks.RED_MUSHROOM_BLOCK)
                                    || world.getBlockState(topPos.down()).isOf(Blocks.BROWN_MUSHROOM_BLOCK)
                    );

                    if (badTerrain) {
                        // If this occurs early, try rotating direction; otherwise fail
                        if (i < 3) {
                            return generateFallenTrunk2x2(
                                    world,
                                    direction.rotateYClockwise(),
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
                    BlockPos offsetPos = start.offset(direction, i).add(dx, 0, dz);
                    BlockPos topPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, offsetPos);

                    if (world.getBlockState(topPos).isReplaceable()) {
                        world.setBlockState(
                                topPos,
                                trunk.with(PillarBlock.AXIS, direction.getAxis()),
                                3
                        );
                        if (world.getBlockState(topPos.up()).isReplaceable())
                            world.setBlockState(topPos.up(), Blocks.AIR.getDefaultState(), 3);

                    }
                }
            }
        }
        return true;
    }
}