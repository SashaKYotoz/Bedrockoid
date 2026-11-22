package net.sashakyotoz.bedrockoid.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SugarCaneBlock.class)
public abstract class SugarcaneBlockMixin implements BonemealableBlock {
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        if (!BedrockoidConfig.canSugarcaneBeBonemeal)
            return false;
        BlockPos above = bedrockoid$findTop(level, pos, state.getBlock()).above();
        return !level.isOutsideBuildHeight(above) && level.getBlockState(above).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        BlockPos above = bedrockoid$findTop(level, pos, block).above();
        if (!level.isOutsideBuildHeight(above) && level.getBlockState(above).isAir())
            level.setBlock(above, block.defaultBlockState(), 3);
    }

    @Unique
    private static BlockPos bedrockoid$findTop(BlockGetter level, BlockPos pos, Block block) {
        BlockPos.MutableBlockPos cursor = pos.mutable();
        while (level.getBlockState(cursor.above()).is(block))
            cursor.move(Direction.UP);
        return cursor.immutable();
    }
}