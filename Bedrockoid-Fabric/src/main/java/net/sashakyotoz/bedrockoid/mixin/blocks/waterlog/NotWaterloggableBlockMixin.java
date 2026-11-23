package net.sashakyotoz.bedrockoid.mixin.blocks.waterlog;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = {LayeredCauldronBlock.class, AnvilBlock.class, GrindstoneBlock.class, StonecutterBlock.class, BedBlock.class,
        LecternBlock.class, HopperBlock.class, BrewingStandBlock.class, BasePressurePlateBlock.class, FenceGateBlock.class, BellBlock.class})
public abstract class NotWaterloggableBlockMixin implements SimpleWaterloggedBlock {
    @Override
    public boolean canPlaceLiquid(@Nullable LivingEntity user, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return BedrockoidConfig.blocksWaterloggability
                && BlockUtils.hasExtraWaterlogging(state.getBlock())
                && !state.getValue(BlockStateProperties.WATERLOGGED)
                && fluid == Fluids.WATER;
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!this.canPlaceLiquid(null, level, pos, state, fluidState.getType()))
            return false;
        if (level instanceof Level world && world.isClientSide())
            return true;
        level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, true), 3);
        int delay = level instanceof LevelReader reader ? fluidState.getType().getTickDelay(reader) : 5;
        level.scheduleTick(pos, fluidState.getType(), delay);
        return true;
    }

    @Override
    public ItemStack pickupBlock(@Nullable LivingEntity user, LevelAccessor level, BlockPos pos, BlockState state) {
        if (!BlockUtils.isExtraWaterlogged(state))
            return ItemStack.EMPTY;
        level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, false), 3);
        if (level instanceof LevelReader reader && !state.canSurvive(reader, pos))
            level.destroyBlock(pos, true);
        return new ItemStack(Items.WATER_BUCKET);
    }
}
