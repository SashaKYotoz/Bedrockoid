package net.sashakyotoz.bedrockoid.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SugarCaneBlock.class)
public abstract class SugarcaneBlockMixin implements BonemealableBlock {
    @Shadow
    protected abstract boolean canSurvive(BlockState state, LevelReader level, BlockPos pos);

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return levelReader.getBlockState(blockPos.above()).isAir()
                && blockPos.getY() + 1 < levelReader.getHeight()
                && BedrockoidConfig.canSugarcaneBeBonemeal;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        return this.canSurvive(blockState, level, blockPos)
                && level.getBlockState(blockPos.above()).isAir()
                && BedrockoidConfig.canSugarcaneBeBonemeal;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource randomSource, BlockPos pos, BlockState blockState) {
        if (level.getBlockState(pos.above()).isAir())
            level.setBlock(pos.above(), blockState, 3);
    }
}