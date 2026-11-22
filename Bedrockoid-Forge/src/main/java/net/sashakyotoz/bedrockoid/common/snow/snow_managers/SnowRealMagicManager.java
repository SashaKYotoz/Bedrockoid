package net.sashakyotoz.bedrockoid.common.snow.snow_managers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SnowRealMagicManager implements SnowManager {
    @Override
    public boolean placeSnow(WorldGenLevel level, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isSnow(WorldGenLevel level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();

        return block == Blocks.SNOW;
    }

    @Override
    public BlockState getStateAfterMelting(BlockState stateNow, WorldGenLevel level, BlockPos pos) {
        return Blocks.AIR.defaultBlockState();
    }
}
