package net.sashakyotoz.bedrockoid.common.snow.snow_managers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.sashakyotoz.bedrockoid.common.snow.BedrockSnowManager;

public class VanillaManager implements SnowManager {
    @Override
    public boolean placeSnow(StructureWorldAccess level, BlockPos pos) {
        int accumulationHeight = level instanceof World l ? l.getGameRules().getInt(GameRules.SNOW_ACCUMULATION_HEIGHT) : 1;

        if (accumulationHeight > 0 && BedrockSnowManager.canSnow(level, pos)) {
            BlockState state = level.getBlockState(pos);

            if (state.isOf(Blocks.SNOW)) {
                int currentLayers = state.get(SnowBlock.LAYERS);

                if (currentLayers < Math.min(accumulationHeight, 8)) {
                    BlockState snowLayers = state.with(SnowBlock.LAYERS, currentLayers + 1);

                    Block.pushEntitiesUpBeforeBlockChange(state, snowLayers, level, pos);
                    level.setBlockState(pos, snowLayers, 2);
                    return true;
                }
            }
            else {
                if (state.contains(Properties.DOUBLE_BLOCK_HALF))
                    level.setBlockState(pos.up(), Blocks.AIR.getDefaultState(), 2);

                level.setBlockState(pos, Blocks.SNOW.getDefaultState(), 2);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isSnow(StructureWorldAccess level, BlockPos pos) {
        return level.getBlockState(pos).isOf(Blocks.SNOW);
    }

    @Override
    public BlockState getStateAfterMelting(BlockState stateNow, StructureWorldAccess level, BlockPos pos) {
        return Blocks.AIR.getDefaultState();
    }
}