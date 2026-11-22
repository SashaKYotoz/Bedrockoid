package net.sashakyotoz.bedrockoid.common.snow.snow_managers;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

public interface SnowManager {
    public boolean placeSnow(StructureWorldAccess worldAccess, BlockPos pos);

    public boolean isSnow(StructureWorldAccess worldAccess, BlockPos pos);

    public BlockState getStateAfterMelting(BlockState stateNow, StructureWorldAccess worldAccess, BlockPos pos);
}
