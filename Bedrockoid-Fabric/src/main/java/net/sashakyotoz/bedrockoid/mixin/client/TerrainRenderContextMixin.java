package net.sashakyotoz.bedrockoid.mixin.client;

import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.util.math.BlockPos;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TerrainRenderContext.class)
public abstract class TerrainRenderContextMixin {
    @Shadow
    public abstract void bufferModel(BlockStateModel model, BlockState blockState, BlockPos blockPos);

    @Inject(method = "bufferModel", at = @At("HEAD"), require = 0)
    public void bedrockoidTessellation(BlockStateModel model, BlockState blockState, BlockPos blockPos, CallbackInfo ci) {
        if (BlockUtils.isSnowlogged(blockState) && BedrockoidConfig.snowlogging) {
            BlockState snowState = BlockUtils.getSnowEquivalent(blockState);
            this.bufferModel(MinecraftClient.getInstance().getBlockRenderManager().getModel(snowState), snowState, blockPos);
        }
    }
}