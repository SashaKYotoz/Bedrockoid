package net.sashakyotoz.bedrockoid.mixin.blocks;

import net.minecraft.block.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FlowerBlock.class)
public class FlowerBlockMixin implements Fertilizable {
    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
        return !state.isOf(Blocks.WITHER_ROSE);
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        if (!ModsUtils.isBedrockifyIn() && BedrockoidConfig.canPlantsBeBonemeal) {
            int amount = random.nextBetween(1, 5);
            for (int i = 0; i < amount; i++) {
                int x = random.nextBetween(-3, 3);
                int z = random.nextBetween(-3, 3);
                BlockPos newPos = pos.add(x, 0, z);
                if (world.getBlockState(newPos).isAir() && world.getBlockState(newPos.down()).isIn(BlockTags.DIRT))
                    world.setBlockState(newPos, state);
            }
        }
    }
}
