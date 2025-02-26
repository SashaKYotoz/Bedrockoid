package net.sashakyotoz.bedrockoid.common.snow.snow_managers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;

public class SnowRealMagicManager implements SnowManager {
    @Override
    public boolean placeSnow(StructureWorldAccess level, BlockPos pos) {
//        if (BedrockSnowManager.canSnow(level, pos))
//            return Hooks.convert(level, pos, level.getBlockState(pos), 1, 2, SnowCommonConfig.placeSnowOnBlockNaturally);
//        else
            return false;
    }

    @Override
    public boolean isSnow(StructureWorldAccess level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();

        return block == Blocks.SNOW;
    }

    @Override
    public BlockState getStateAfterMelting(BlockState stateNow, StructureWorldAccess level, BlockPos pos) {
//        if (SnowCommonConfig.snowNeverMelt)
//            return stateNow;

//        if (stateNow.getBlock() instanceof SnowVariant snowVariant)
//            return snowVariant.getRaw(stateNow, level, pos);

        return Blocks.AIR.getDefaultState();
    }
}