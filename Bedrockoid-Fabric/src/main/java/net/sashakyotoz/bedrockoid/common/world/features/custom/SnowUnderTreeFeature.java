package net.sashakyotoz.bedrockoid.common.world.features.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.sashakyotoz.bedrockoid.common.snow.BedrockSnowManager;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;

public record SnowUnderTreeFeature() implements Feature {
    public static final MapCodec<SnowUnderTreeFeature> CODEC = MapCodec.unit(SnowUnderTreeFeature::new);

    @Override
    public MapCodec<SnowUnderTreeFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos pos) {
        BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();

        if (!ModsUtils.isSnowUnderTreesIn()) {
            for (int xi = 0; xi < 16; xi++) {
                for (int zi = 0; zi < 16; zi++) {
                    int x = pos.getX() + xi;
                    int z = pos.getZ() + zi;

                    mPos.set(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) - 1, z);

                    if (level.getBlockState(mPos).is(BlockTags.LEAVES)) {
                        mPos.set(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z);

                        if (BedrockSnowManager.placeSnow(level, mPos)) {
                            BlockState stateBelow;

                            mPos.move(Direction.DOWN);
                            stateBelow = level.getBlockState(mPos);

                            if (stateBelow.hasProperty(BlockStateProperties.SNOWY))
                                level.setBlock(mPos, stateBelow.setValue(BlockStateProperties.SNOWY, true), 2);
                        }
                    }
                }
            }
        }

        return true;
    }
}
