package net.sashakyotoz.bedrockoid.common.snow.snow_managers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

public interface SnowManager {
    public boolean placeSnow(WorldGenLevel worldAccess, BlockPos pos);

    public boolean isSnow(WorldGenLevel worldAccess, BlockPos pos);

    public BlockState getStateAfterMelting(BlockState stateNow, WorldGenLevel worldAccess, BlockPos pos);
}
