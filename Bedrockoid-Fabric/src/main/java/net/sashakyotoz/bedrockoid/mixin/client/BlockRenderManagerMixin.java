package net.sashakyotoz.bedrockoid.mixin.client;

import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.sashakyotoz.bedrockoid.common.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderManager.class)
public abstract class BlockRenderManagerMixin {

    @Inject(method = "renderBlock", at = @At(value = "RETURN"))
    private void renderSnow(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, Random random, CallbackInfo ci) {
        if (state.getProperties().contains(BlockUtils.LAYERS) && state.get(BlockUtils.LAYERS) > 0)
            BlockUtils.renderSnowOverlay(pos, MinecraftClient.getInstance().world, state.get(BlockUtils.LAYERS));
    }
}