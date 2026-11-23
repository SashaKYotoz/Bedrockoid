package net.sashakyotoz.bedrockoid.common.world.features.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;

public record SwampHugeMushroomFeature() implements Feature {
    public static final MapCodec<SwampHugeMushroomFeature> CODEC = MapCodec.unit(SwampHugeMushroomFeature::new);
    private static final int CLEARANCE_RADIUS = 3;
    private static final int CLEARANCE_HEIGHT = 8;

    @Override
    public MapCodec<SwampHugeMushroomFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {
        if (!BedrockoidConfig.mushroomTreesInSwamp)
            return false;
        if (!hasClearance(level, origin))
            return false;
        ResourceKey<Feature> mushroom = random.nextBoolean() ? TreeFeatures.HUGE_RED_MUSHROOM : TreeFeatures.HUGE_BROWN_MUSHROOM;
        return level.registryAccess().lookupOrThrow(Registries.FEATURE).get(mushroom)
                .map(holder -> holder.value().place(level, chunkGenerator, random, origin))
                .orElse(false);
    }

    private static boolean hasClearance(WorldGenLevel level, BlockPos origin) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -CLEARANCE_RADIUS; dx <= CLEARANCE_RADIUS; dx++) {
            for (int dz = -CLEARANCE_RADIUS; dz <= CLEARANCE_RADIUS; dz++) {
                for (int dy = 0; dy <= CLEARANCE_HEIGHT; dy++) {
                    cursor.setWithOffset(origin, dx, dy, dz);
                    BlockState state = level.getBlockState(cursor);
                    if (state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES))
                        return false;
                }
            }
        }
        return true;
    }
}
