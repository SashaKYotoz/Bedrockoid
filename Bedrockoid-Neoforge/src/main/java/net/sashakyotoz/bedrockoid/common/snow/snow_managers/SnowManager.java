package net.sashakyotoz.bedrockoid.common.snow.snow_managers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

// Code goes under Copyright (c) 2022 bl4ckscor3
public interface SnowManager {
    boolean placeSnow(WorldGenLevel worldAccess, BlockPos pos);

    boolean isSnow(WorldGenLevel worldAccess, BlockPos pos);

    BlockState getStateAfterMelting(BlockState stateNow, WorldGenLevel worldAccess, BlockPos pos);
}