package net.sashakyotoz.bedrockoid.mixin.blocks;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Level.class)
public abstract class LevelMixin {
    @ModifyVariable(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("HEAD"), argsOnly = true)
    private BlockState bedrockoid$keepCauldronWaterlogged(BlockState state, @Local(argsOnly = true) BlockPos pos) {
        if (!(state.getBlock() instanceof AbstractCauldronBlock) || !BlockUtils.hasExtraWaterlogging(state.getBlock()) || state.getValue(BlockStateProperties.WATERLOGGED))
            return state;
        BlockState previous = ((Level) (Object) this).getBlockState(pos);
        if (previous.getBlock() != state.getBlock() && previous.getBlock() instanceof AbstractCauldronBlock && BlockUtils.isExtraWaterlogged(previous))
            return state.setValue(BlockStateProperties.WATERLOGGED, true);
        return state;
    }
}
