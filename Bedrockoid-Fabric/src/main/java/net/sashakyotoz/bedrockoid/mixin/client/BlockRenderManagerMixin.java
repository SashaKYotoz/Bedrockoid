package net.sashakyotoz.bedrockoid.mixin.client;

import net.minecraft.block.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderManager.class)
public class BlockRenderManagerMixin {

    @Inject(method = "renderDamage", at = @At("HEAD"), cancellable = true)
    public void renderSnowDamage(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrices, VertexConsumer vertexConsumer, CallbackInfo ci) {
        if (BlockUtils.isSnowlogged(state) && BedrockoidConfig.snowlogging) {
            this.renderDamage(BlockUtils.getSnowEquivalent(state), pos, world, matrices, vertexConsumer);
            ci.cancel();
        }
    }

    @Inject(method = "renderBlock", at = @At("HEAD"))
    private void renderSnow(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, Random random, CallbackInfo ci) {
        if (BlockUtils.isSnowlogged(state) && BedrockoidConfig.snowlogging)
            this.renderBlock(BlockUtils.getSnowEquivalent(state), pos, world, matrices, vertexConsumer, cull, random);
    }

    @Shadow
    public void renderBlock(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrices, VertexConsumer vertexConsumer, boolean cull, Random random) {
        throw new AssertionError("Mixin injection failed - Bedrockoid BlockRenderManagerMixin.");
    }

    @Shadow
    public void renderDamage(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrices, VertexConsumer consumer) {
        throw new AssertionError("Mixin injection failed - Bedrockoid BlockRenderManagerMixin.");
    }
}