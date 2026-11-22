package net.sashakyotoz.bedrockoid.mixin.blocks;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(World.class)
public abstract class WorldMixin {
    @ModifyVariable(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At("HEAD"), argsOnly = true)
    private BlockState bedrockoid$keepCauldronWaterlogged(BlockState state, @Local(argsOnly = true) BlockPos pos) {
        if (!(state.getBlock() instanceof AbstractCauldronBlock) || !BlockUtils.hasExtraWaterlogging(state.getBlock()) || state.get(Properties.WATERLOGGED))
            return state;
        BlockState previous = ((World) (Object) this).getBlockState(pos);
        if (previous.getBlock() != state.getBlock() && previous.getBlock() instanceof AbstractCauldronBlock && BlockUtils.isExtraWaterlogged(previous))
            return state.with(Properties.WATERLOGGED, true);
        return state;
    }
}
