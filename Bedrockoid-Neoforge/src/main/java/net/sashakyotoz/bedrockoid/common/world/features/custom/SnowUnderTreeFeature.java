package net.sashakyotoz.bedrockoid.common.world.features.custom;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.sashakyotoz.bedrockoid.common.snow.BedrockSnowManager;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;

public class SnowUnderTreeFeature extends Feature<NoneFeatureConfiguration> {

    public static final Feature<NoneFeatureConfiguration> INSTANCE = new SnowUnderTreeFeature(NoneFeatureConfiguration.CODEC);

    public SnowUnderTreeFeature(Codec<NoneFeatureConfiguration> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        BlockPos pos = ctx.origin();
        WorldGenLevel level = ctx.level();
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

                            if (stateBelow.hasProperty(SnowyDirtBlock.SNOWY))
                                level.setBlock(mPos, stateBelow.setValue(SnowyDirtBlock.SNOWY, true), 2);
                        }
                    }
                }
            }
        }

        return true;
    }
}