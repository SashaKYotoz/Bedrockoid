package net.sashakyotoz.bedrockoid.common.snow.snow_managers;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.snow.BedrockSnowManager;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;

public class VanillaManager implements SnowManager {
    @Override
    public boolean placeSnow(WorldGenLevel level, BlockPos pos) {
        int accumulationHeight = level instanceof Level l ? l.getGameRules().getInt(GameRules.RULE_SNOW_ACCUMULATION_HEIGHT) : 1;
        if (accumulationHeight <= 0 || !BedrockSnowManager.canSnow(level, pos))
            return false;
        BlockState state = level.getBlockState(pos);
        if (state.is(Blocks.SNOW)) {
            int currentLayers = state.getValue(SnowLayerBlock.LAYERS);
            if (currentLayers >= Math.min(accumulationHeight, 8))
                return false;
            BlockState snowLayers = state.setValue(SnowLayerBlock.LAYERS, currentLayers + 1);
            Block.pushEntitiesUp(state, snowLayers, level, pos);
            level.setBlock(pos, snowLayers, 2);
            return true;
        }
        if (state.getBlock() instanceof SugarCaneBlock)
            return false;
        boolean snowlogging = BedrockoidConfig.snowlogging && BlockUtils.canSnowlog(state);
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            if (snowlogging && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
                level.setBlock(pos, state.setValue(BlockUtils.LAYERS, 1), 2);
                return true;
            }
            return false;
        }
        if (snowlogging) {
            level.setBlock(pos, state.setValue(BlockUtils.LAYERS, 1), 2);
            return true;
        }
        level.setBlock(pos, Blocks.SNOW.defaultBlockState(), 2);
        return true;
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
