package net.sashakyotoz.bedrockoid.common.snow.snow_managers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.sashakyotoz.bedrockoid.common.snow.BedrockSnowManager;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;

public class VanillaManager implements SnowManager {
    @Override
    public boolean placeSnow(WorldGenLevel level, BlockPos pos) {
        int accumulationHeight = level instanceof Level l ? l.getGameRules().getInt(GameRules.RULE_SNOW_ACCUMULATION_HEIGHT) : 1;

        if (accumulationHeight > 0 && BedrockSnowManager.canSnow(level, pos)) {
            BlockState state = level.getBlockState(pos);

            if (state.is(Blocks.SNOW)) {
                int currentLayers = state.getValue(SnowLayerBlock.LAYERS);

                if (currentLayers < Math.min(accumulationHeight, 8)) {
                    BlockState snowLayers = state.setValue(SnowLayerBlock.LAYERS, currentLayers + 1);

                    Block.pushEntitiesUp(state, snowLayers, level, pos);
                    level.setBlock(pos, snowLayers, 2);
                    return true;
                }
            } else {
                if (state.getBlock() instanceof SugarCaneBlock)
                    return true;
                if (BlockUtils.canSnowlog(state))
                    level.setBlock(pos, state.setValue(BlockUtils.LAYERS, 1), Block.UPDATE_CLIENTS);
                else
                    level.setBlock(pos, level.getBlockState(pos).hasProperty(BlockUtils.LAYERS) ?
                            state.setValue(BlockUtils.LAYERS, 1) : Blocks.SNOW.defaultBlockState(), Block.UPDATE_CLIENTS);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isSnow(WorldGenLevel level, BlockPos pos) {
        return level.getBlockState(pos).is(Blocks.SNOW);
    }

    @Override
    public BlockState getStateAfterMelting(BlockState stateNow, WorldGenLevel level, BlockPos pos) {
        return Blocks.AIR.defaultBlockState();
    }
}