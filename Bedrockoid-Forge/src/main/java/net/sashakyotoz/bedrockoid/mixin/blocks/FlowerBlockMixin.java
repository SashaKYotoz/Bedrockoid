package net.sashakyotoz.bedrockoid.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FlowerBlock.class)
public class FlowerBlockMixin implements BonemealableBlock {

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean b) {
        return !blockState.is(Blocks.WITHER_ROSE);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource random, BlockPos blockPos, BlockState blockState) {
        if (!ModsUtils.isBedrockifyIn() && BedrockoidConfig.canPlantsBeBonemeal) {
            int amount = random.nextIntBetweenInclusive(1, 5);
            for (int i = 0; i < amount; i++) {
                int x = random.nextIntBetweenInclusive(-3, 3);
                int z = random.nextIntBetweenInclusive(-3, 3);
                BlockPos newPos = blockPos.offset(x, 0, z);
                if (serverLevel.getBlockState(newPos).isAir() && serverLevel.getBlockState(newPos.below()).is(BlockTags.DIRT))
                    serverLevel.setBlockAndUpdate(newPos, blockState);
            }
        }
    }
}