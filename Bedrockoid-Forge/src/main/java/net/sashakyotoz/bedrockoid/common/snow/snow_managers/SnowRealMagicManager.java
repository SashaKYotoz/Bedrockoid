package net.sashakyotoz.bedrockoid.common.snow.snow_managers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SnowRealMagicManager implements SnowManager {
    @Override
    public boolean placeSnow(WorldGenLevel level, BlockPos pos) {
//        if (BedrockSnowManager.canSnow(level, pos))
//            return Hooks.convert(level, pos, level.getBlockState(pos), 1, 2, SnowCommonConfig.placeSnowOnBlockNaturally);
//        else
            return false;
    }

    @Override
    public boolean isSnow(WorldGenLevel level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();

        return block == Blocks.SNOW;
    }

    @Override
    public BlockState getStateAfterMelting(BlockState stateNow, WorldGenLevel level, BlockPos pos) {
//        if (stateNow.getBlock() instanceof SnowVariant snowVariant)
//            return snowVariant.getRaw(stateNow, level, pos);

        return Blocks.AIR.defaultBlockState();
    }
}