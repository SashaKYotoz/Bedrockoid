package net.sashakyotoz.bedrockoid.mixin.blocks.waterlog;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = {LayeredCauldronBlock.class, AnvilBlock.class, GrindstoneBlock.class, StonecutterBlock.class, BedBlock.class,
        LecternBlock.class, HopperBlock.class, BrewingStandBlock.class, BasePressurePlateBlock.class, FenceGateBlock.class, BellBlock.class})
public abstract class NotWaterloggableBlockMixin implements SimpleWaterloggedBlock {
    @Override
    public boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return BedrockoidConfig.blocksWaterloggability
                && BlockUtils.hasExtraWaterlogging(state.getBlock())
                && !state.getValue(BlockStateProperties.WATERLOGGED)
                && fluid == Fluids.WATER;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!this.canPlaceLiquid(level, pos, state, fluidState.getType()))
            return false;
        if (!level.isClientSide()) {
            level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, true), 3);
            level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
        }
        return true;
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        if (!BlockUtils.isExtraWaterlogged(state))
            return ItemStack.EMPTY;
        level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, false), 3);
        if (!state.canSurvive(level, pos))
            level.destroyBlock(pos, true);
        return new ItemStack(Items.WATER_BUCKET);
    }
}
