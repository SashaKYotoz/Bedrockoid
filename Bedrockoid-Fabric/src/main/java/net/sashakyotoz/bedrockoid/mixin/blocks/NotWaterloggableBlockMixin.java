package net.sashakyotoz.bedrockoid.mixin.blocks;

import net.minecraft.block.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = {LeveledCauldronBlock.class, AnvilBlock.class, BedBlock.class, GrindstoneBlock.class, StonecutterBlock.class,
        LecternBlock.class, HopperBlock.class, BrewingStandBlock.class, BellBlock.class, AbstractPressurePlateBlock.class, FenceGateBlock.class})
public abstract class NotWaterloggableBlockMixin implements Waterloggable {
    @Override
    public boolean canFillWithFluid(BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return BedrockoidConfig.blocksWaterloggability
                && BlockUtils.hasExtraWaterlogging(state.getBlock())
                && !state.get(Properties.WATERLOGGED)
                && fluid == Fluids.WATER;
    }

    @Override
    public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!this.canFillWithFluid(world, pos, state, fluidState.getFluid()))
            return false;
        if (!world.isClient()) {
            world.setBlockState(pos, state.with(Properties.WATERLOGGED, true), Block.NOTIFY_ALL);
            world.scheduleFluidTick(pos, fluidState.getFluid(), fluidState.getFluid().getTickRate(world));
        }
        return true;
    }

    @Override
    public ItemStack tryDrainFluid(WorldAccess world, BlockPos pos, BlockState state) {
        if (!BlockUtils.isExtraWaterlogged(state))
            return ItemStack.EMPTY;
        world.setBlockState(pos, state.with(Properties.WATERLOGGED, false), Block.NOTIFY_ALL);
        if (!state.canPlaceAt(world, pos))
            world.breakBlock(pos, true);
        return new ItemStack(Items.WATER_BUCKET);
    }
}
