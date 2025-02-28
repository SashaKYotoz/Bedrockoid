package net.sashakyotoz.bedrockoid.mixin.blocks;

import net.minecraft.block.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class BlockStateMixin {
    @Inject(method = "onStateReplaced", at = @At("HEAD"))
    private void updateRenderer(World world, BlockPos pos, BlockState state, boolean moved, CallbackInfo ci) {
        if (state.getProperties().contains(BlockUtils.LAYERS) && world.isClient())
            BlockUtils.renderSnowOverlay(pos, world, state.get(BlockUtils.LAYERS));
    }
}