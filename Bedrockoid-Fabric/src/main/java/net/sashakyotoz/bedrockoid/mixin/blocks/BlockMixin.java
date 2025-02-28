package net.sashakyotoz.bedrockoid.mixin.blocks;

import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Block.class, priority = 900)
public abstract class BlockMixin {

    @Shadow
    private BlockState defaultState;

    @Inject(method = "setDefaultState", at = @At("TAIL"))
    private void addSnowLayers(BlockState state, CallbackInfo ci) {
        Block block = state.getBlock();
        if (block instanceof PlantBlock && block.getStateManager().getProperties().contains(BlockUtils.LAYERS))
            this.defaultState = state.with(BlockUtils.LAYERS, 3);
    }

    @Inject(method = "appendProperties", at = @At("HEAD"))
    private void onAppendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        Block self = (Block) (Object) this;
        if (self instanceof PlantBlock)
            builder.add(BlockUtils.LAYERS);
    }
}