package net.sashakyotoz.bedrockoid.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = {ChunkRenderDispatcher.RenderChunk.RebuildTask.class})
public class ChunkRenderDispatcherMixin {
    @WrapOperation(method = {"compile"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V"))
    private void handleBlockRender(BlockRenderDispatcher instance, BlockState state, BlockPos pos, BlockAndTintGetter getter, PoseStack poseStack, VertexConsumer vertexConsumer, boolean flag, RandomSource source, ModelData data, RenderType type, Operation<Void> original) {
        if (state.getRenderShape() == RenderShape.MODEL) {
            if (BedrockoidConfig.snowlogging && BlockUtils.isSnowlogged(state))
                original.call(instance, BlockUtils.getSnowEquivalent(state), pos, getter, poseStack, vertexConsumer, flag, source, data, type);
        }
        original.call(instance, state, pos, getter, poseStack, vertexConsumer, flag, source, data, type);
    }
}