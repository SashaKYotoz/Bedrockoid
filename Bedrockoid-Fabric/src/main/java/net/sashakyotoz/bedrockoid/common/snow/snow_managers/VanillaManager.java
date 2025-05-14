package net.sashakyotoz.bedrockoid.common.snow.snow_managers;

import net.minecraft.block.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.sashakyotoz.bedrockoid.common.snow.BedrockSnowManager;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;

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
            } else {
                if (state.getBlock() instanceof SugarCaneBlock)
                    return true;
                if (BlockUtils.canSnowlog(state))
                    level.setBlockState(pos, state.with(BlockUtils.LAYERS, 1), Block.NOTIFY_LISTENERS);
                else
                    level.setBlockState(pos, level.getBlockState(pos).contains(BlockUtils.LAYERS) ?
                            state.with(BlockUtils.LAYERS, 1) : Blocks.SNOW.getDefaultState(), Block.NOTIFY_LISTENERS);
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