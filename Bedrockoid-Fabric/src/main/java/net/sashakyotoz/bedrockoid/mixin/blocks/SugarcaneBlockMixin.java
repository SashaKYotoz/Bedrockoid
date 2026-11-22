package net.sashakyotoz.bedrockoid.mixin.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SugarCaneBlock.class)
public abstract class SugarcaneBlockMixin implements Fertilizable {
    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        if (!BedrockoidConfig.canSugarcaneBeBonemeal)
            return false;
        BlockPos above = bedrockoid$findTop(world, pos, state.getBlock()).up();
        return !world.isOutOfHeightLimit(above) && world.getBlockState(above).isAir();
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        BlockPos above = bedrockoid$findTop(world, pos, block).up();
        if (!world.isOutOfHeightLimit(above) && world.getBlockState(above).isAir())
            world.setBlockState(above, block.getDefaultState(), Block.NOTIFY_ALL);
    }

    @Unique
    private static BlockPos bedrockoid$findTop(BlockView world, BlockPos pos, Block block) {
        BlockPos.Mutable cursor = pos.mutableCopy();
        while (world.getBlockState(cursor.up()).isOf(block))
            cursor.move(Direction.UP);
        return cursor.toImmutable();
    }
}