package net.sashakyotoz.bedrockoid.mixin.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SugarCaneBlock.class)
public abstract class SugarcaneBlockMixin implements Fertilizable {
    @Shadow
    protected abstract boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos);

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return world.getBlockState(pos.up()).isAir()
                && pos.getY() + 1 < world.getHeight()
                && BedrockoidConfig.canSugarcaneBeBonemeal;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return this.canPlaceAt(state, world, pos)
                && world.getBlockState(pos.up()).isAir()
                && BedrockoidConfig.canSugarcaneBeBonemeal;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        if (world.getBlockState(pos.up()).isAir())
            world.setBlockState(pos.up(), state, 3);
    }
}