package net.sashakyotoz.bedrockoid.common.world.features.custom;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;

public class SwampHugeMushroomFeature extends Feature<NoneFeatureConfiguration> {
    public static final Feature<NoneFeatureConfiguration> INSTANCE = new SwampHugeMushroomFeature(NoneFeatureConfiguration.CODEC);
    private static final int CLEARANCE_RADIUS = 3;
    private static final int CLEARANCE_HEIGHT = 8;

    public SwampHugeMushroomFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if (!BedrockoidConfig.mushroomTreesInSwamp)
            return false;
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        if (!hasClearance(level, origin))
            return false;
        ResourceKey<ConfiguredFeature<?, ?>> mushroom = context.random().nextBoolean() ? TreeFeatures.HUGE_RED_MUSHROOM : TreeFeatures.HUGE_BROWN_MUSHROOM;
        return level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE).get(mushroom)
                .map(holder -> holder.value().place(level, context.chunkGenerator(), context.random(), origin))
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
