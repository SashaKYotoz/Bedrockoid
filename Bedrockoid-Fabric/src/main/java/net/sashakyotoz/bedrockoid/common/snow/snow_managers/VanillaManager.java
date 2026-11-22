package net.sashakyotoz.bedrockoid.common.snow.snow_managers;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.snow.BedrockSnowManager;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;

public class VanillaManager implements SnowManager {
    @Override
    public boolean placeSnow(StructureWorldAccess level, BlockPos pos) {
        int accumulationHeight = level instanceof World world ? world.getGameRules().getInt(GameRules.SNOW_ACCUMULATION_HEIGHT) : 1;
        if (accumulationHeight <= 0 || !BedrockSnowManager.canSnow(level, pos))
            return false;
        BlockState state = level.getBlockState(pos);
        if (state.isOf(Blocks.SNOW)) {
            int currentLayers = state.get(SnowBlock.LAYERS);
            if (currentLayers >= Math.min(accumulationHeight, 8))
                return false;
            BlockState snowLayers = state.with(SnowBlock.LAYERS, currentLayers + 1);
            Block.pushEntitiesUpBeforeBlockChange(state, snowLayers, level, pos);
            level.setBlockState(pos, snowLayers, Block.NOTIFY_LISTENERS);
            return true;
        }
        if (state.getBlock() instanceof SugarCaneBlock)
            return false;
        boolean snowlogging = BedrockoidConfig.snowlogging && BlockUtils.canSnowlog(state);
        if (state.contains(Properties.DOUBLE_BLOCK_HALF)) {
            if (snowlogging && state.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
                level.setBlockState(pos, state.with(BlockUtils.LAYERS, 1), Block.NOTIFY_LISTENERS);
                return true;
            }
            return false;
        }
        if (snowlogging) {
            level.setBlockState(pos, state.with(BlockUtils.LAYERS, 1), Block.NOTIFY_LISTENERS);
            return true;
        }
        level.setBlockState(pos, Blocks.SNOW.getDefaultState(), Block.NOTIFY_LISTENERS);
        return true;
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
