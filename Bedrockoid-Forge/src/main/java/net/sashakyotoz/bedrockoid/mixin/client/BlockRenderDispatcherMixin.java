package net.sashakyotoz.bedrockoid.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderDispatcher.class)
public abstract class BlockRenderDispatcherMixin {

    @Shadow
    public abstract void renderBreakingTexture(BlockState state, BlockPos pos, BlockAndTintGetter world, PoseStack matrices, VertexConsumer consumer, ModelData modelData);

    @Shadow
    public abstract void renderBatched(BlockState state, BlockPos pos, BlockAndTintGetter getter, PoseStack poseStack, VertexConsumer vertexConsumer, boolean cull, RandomSource random, ModelData modelData, RenderType renderType);

    @Inject(method = "renderBreakingTexture(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;)V", at = @At("HEAD"), cancellable = true)
    public void renderSnowDamage(BlockState state, BlockPos pos, BlockAndTintGetter world, PoseStack matrices, VertexConsumer vertexConsumer, CallbackInfo ci) {
        if (BlockUtils.isSnowlogged(state) && BedrockoidConfig.snowlogging) {
            this.renderBreakingTexture(BlockUtils.getSnowEquivalent(state), pos, world, matrices, vertexConsumer, ModelData.EMPTY);
            ci.cancel();
        }
    }

    @Inject(method = "renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;)V", at = @At("HEAD"))
    private void renderSnow(BlockState state, BlockPos pos, BlockAndTintGetter world, PoseStack matrices, VertexConsumer vertexConsumer, boolean cull, RandomSource random, CallbackInfo ci) {
        if (BlockUtils.isSnowlogged(state) && BedrockoidConfig.snowlogging)
            this.renderBatched(BlockUtils.getSnowEquivalent(state), pos, world, matrices, vertexConsumer, cull, random, ModelData.EMPTY, RenderType.cutout());
    }
}