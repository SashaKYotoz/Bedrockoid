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
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FlowerBlock.class)
public class FlowerBlockMixin implements BonemealableBlock {
    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return BedrockoidConfig.canPlantsBeBonemeal
                && !state.is(Blocks.WITHER_ROSE)
                && !BlockUtils.isSnowlogged(state);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int amount = random.nextIntBetweenInclusive(1, 5);
        for (int i = 0; i < amount; i++) {
            BlockPos target = pos.offset(random.nextIntBetweenInclusive(-3, 3), 0, random.nextIntBetweenInclusive(-3, 3));
            if (level.getBlockState(target).isAir()
                    && level.getBlockState(target.below()).is(BlockTags.DIRT)
                    && state.canSurvive(level, target))
                level.setBlockAndUpdate(target, state);
        }
    }
}
