package net.sashakyotoz.bedrockoid.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.VertexSorter;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BlockRenderLayers;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.chunk.BlockBufferAllocatorStorage;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.client.render.chunk.SectionBuilder;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.Random;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(value = {SectionBuilder.class})
public abstract class SectionBuilderMixin {

    @Shadow
    protected abstract BufferBuilder beginBufferBuilding(Map<BlockRenderLayer, BufferBuilder> builders, BlockBufferAllocatorStorage allocatorStorage, BlockRenderLayer layer);

    @Shadow
    @Final
    private BlockRenderManager blockRenderManager;

    @Inject(
            method = "build",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;getRenderType()Lnet/minecraft/block/BlockRenderType;"
            )
    )
    public void compileWithSnowlogging(
            ChunkSectionPos sectionPos, ChunkRendererRegion renderRegion, VertexSorter vertexSorter, BlockBufferAllocatorStorage allocatorStorage, CallbackInfoReturnable<SectionBuilder.RenderData> cir, @Local MatrixStack poseStack, @Local Map<BlockRenderLayer, BufferBuilder> map, @Local Random randomSource, @Local(ordinal = 0) List<BlockModelPart> list, @Local(ordinal = 2) BlockPos blockPos3, @Local BlockState blockState
    ) {
        if (!BlockUtils.isSnowlogged(blockState)) return;
        BlockState snowState = BlockUtils.getSnowEquivalent(blockState);
        BlockRenderLayer chunkSectionLayer = BlockRenderLayers.getBlockLayer(snowState);
        BufferBuilder bufferBuilder = this.beginBufferBuilding(map, allocatorStorage, chunkSectionLayer);
        randomSource.setSeed(snowState.getRenderingSeed(blockPos3));
        this.blockRenderManager.getModel(snowState).addParts(randomSource, list);
        poseStack.push();
        poseStack.translate(
                (float) ChunkSectionPos.getLocalCoord(blockPos3.getX()),
                (float) ChunkSectionPos.getLocalCoord(blockPos3.getY()),
                (float) ChunkSectionPos.getLocalCoord(blockPos3.getZ())
        );
        this.blockRenderManager.renderBlock(snowState, blockPos3, renderRegion, poseStack, bufferBuilder, true, list);
        poseStack.pop();
        list.clear();
    }
}