package net.sashakyotoz.bedrockoid.mixin.blocks;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.BlockState;
import net.minecraft.block.SnowyBlock;
import net.minecraft.item.ItemPlacementContext;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SnowyBlock.class)
public class SnowBlockMixin {
    @Inject(method = "getPlacementState", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/SnowyBlock;isSnow(Lnet/minecraft/block/BlockState;)Z"), cancellable = true)
    private void setLayersForBlock(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir, @Local BlockState blockState) {
        if (BedrockoidConfig.snowlogging && BlockUtils.canSnowlog(blockState)) {
            int layers = blockState.get(BlockUtils.LAYERS);
            if (layers < 8) {
                BlockState placementState = blockState.with(BlockUtils.LAYERS, layers + 1);
                cir.setReturnValue(placementState);
            }
        }
    }
}