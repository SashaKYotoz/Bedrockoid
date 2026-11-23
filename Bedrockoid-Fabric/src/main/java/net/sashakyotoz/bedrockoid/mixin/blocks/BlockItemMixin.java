package net.sashakyotoz.bedrockoid.mixin.blocks;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @ModifyReturnValue(method = "getPlacementState", at = @At("RETURN"))
    private BlockState bedrockoid$extraPlacementState(BlockState state, BlockPlaceContext context) {
        if (state == null)
            return null;
        BlockPos pos = context.getClickedPos();
        if (BedrockoidConfig.blocksWaterloggability && BlockUtils.hasExtraWaterlogging(state.getBlock()))
            state = state.setValue(BlockStateProperties.WATERLOGGED, context.getLevel().getFluidState(pos).getType() == Fluids.WATER);
        BlockState replaced = context.getLevel().getBlockState(pos);
        if (BedrockoidConfig.snowlogging && replaced.is(Blocks.SNOW) && BlockUtils.canSnowlog(state) && replaced.getValue(BlockStateProperties.LAYERS) < 8)
            state = state.setValue(BlockUtils.LAYERS, replaced.getValue(BlockStateProperties.LAYERS));
        return state;
    }

    @WrapOperation(
            method = "place",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType()Lnet/minecraft/world/level/block/SoundType;"
            )
    )
    private SoundType bedrockoid$snowPlaceSound(BlockState instance, Operation<SoundType> original) {
        if (BedrockoidConfig.snowlogging && BlockUtils.isSnowlogged(instance))
            return original.call(BlockUtils.getSnowEquivalent(instance));
        return original.call(instance);
    }
}
