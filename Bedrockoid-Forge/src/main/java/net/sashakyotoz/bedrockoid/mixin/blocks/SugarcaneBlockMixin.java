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
    @Unique
    private static final int bedrockoid$MAX_HEIGHT = 3;

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        if (!BedrockoidConfig.canSugarcaneBeBonemeal)
            return false;
        BlockPos top = bedrockoid$findTop(level, pos, state.getBlock());
        BlockPos above = top.above();
        return bedrockoid$heightBelow(level, top, state.getBlock()) < bedrockoid$MAX_HEIGHT
                && !level.isOutsideBuildHeight(above)
                && level.getBlockState(above).isAir();
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        BlockPos top = bedrockoid$findTop(level, pos, block);
        int height = bedrockoid$heightBelow(level, top, block);
        BlockPos.MutableBlockPos cursor = top.mutable();
        while (height < bedrockoid$MAX_HEIGHT) {
            cursor.move(Direction.UP);
            if (level.isOutsideBuildHeight(cursor) || !level.getBlockState(cursor).isAir())
                break;
            level.setBlock(cursor, block.defaultBlockState(), 3);
            height++;
        }
    }

    @Unique
    private static BlockPos bedrockoid$findTop(BlockGetter level, BlockPos pos, Block block) {
        BlockPos.MutableBlockPos cursor = pos.mutable();
        while (level.getBlockState(cursor.above()).is(block))
            cursor.move(Direction.UP);
        return cursor.immutable();
    }

    @Unique
    private static int bedrockoid$heightBelow(BlockGetter level, BlockPos top, Block block) {
        int height = 0;
        BlockPos.MutableBlockPos cursor = top.mutable();
        while (level.getBlockState(cursor).is(block)) {
            height++;
            cursor.move(Direction.DOWN);
        }
        return height;
    }
}
