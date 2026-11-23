package net.sashakyotoz.bedrockoid.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
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
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
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
        BlockState flower = state.hasProperty(BlockUtils.LAYERS) ? state.setValue(BlockUtils.LAYERS, 0) : state;
        int placed = 0;
        int goal = random.nextIntBetweenInclusive(1, 4);
        for (int attempt = 0; attempt < 24 && placed < goal; attempt++) {
            BlockPos target = pos.offset(random.nextIntBetweenInclusive(-3, 3), random.nextIntBetweenInclusive(-1, 1), random.nextIntBetweenInclusive(-3, 3));
            if (target.equals(pos) || !level.getBlockState(target).isAir() || !flower.canSurvive(level, target))
                continue;
            level.setBlockAndUpdate(target, flower);
            placed++;
        }
    }
}