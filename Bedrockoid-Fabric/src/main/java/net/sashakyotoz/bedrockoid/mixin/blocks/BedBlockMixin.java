package net.sashakyotoz.bedrockoid.mixin.blocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BedBlock.class)
public abstract class BedBlockMixin {
    @WrapOperation(method = "onPlaced", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    private boolean bedrockoid$waterlogHeadFromFluid(World world, BlockPos pos, BlockState state, int flags, Operation<Boolean> original) {
        if (BlockUtils.hasExtraWaterlogging(state.getBlock()))
            state = state.with(Properties.WATERLOGGED, world.getFluidState(pos).getFluid() == Fluids.WATER);
        return original.call(world, pos, state, flags);
    }
}