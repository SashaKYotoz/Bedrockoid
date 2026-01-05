package net.sashakyotoz.bedrockoid.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;

import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {SectionCompiler.class})
public abstract class SectionCompilerMixin {
    @Shadow
    protected abstract BufferBuilder getOrBeginLayer(Map<RenderType, BufferBuilder> map, SectionBufferBuilderPack sectionBufferBuilderPack, ChunkSectionLayer chunkSectionLayer);

    @Shadow
    @Final
    private BlockRenderDispatcher blockRenderer;

    @Inject(
            method = "compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderSectionRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getRenderShape()Lnet/minecraft/world/level/block/RenderShape;"
            )
    )
    public void compileWithSnowlogging(
            SectionPos sectionPos, RenderSectionRegion region, VertexSorting vertexSorting, SectionBufferBuilderPack sectionBufferBuilderPack, List<AddSectionGeometryEvent.AdditionalSectionRenderer> additionalRenderers, CallbackInfoReturnable<SectionCompiler.Results> cir, @Local PoseStack poseStack, @Local Map<RenderType, BufferBuilder> map, @Local RandomSource randomSource, @Local(ordinal = 1) List<BlockModelPart> list, @Local(ordinal = 2) BlockPos blockPos3, @Local BlockState blockState
    ) {
        if (!BlockUtils.isSnowlogged(blockState)) return;
        BlockState snowState = BlockUtils.getSnowEquivalent(blockState);
        ChunkSectionLayer chunkSectionLayer = ItemBlockRenderTypes.getChunkRenderType(snowState);
        BufferBuilder bufferBuilder = this.getOrBeginLayer(map, sectionBufferBuilderPack, chunkSectionLayer);
        randomSource.setSeed(snowState.getSeed(blockPos3));
        this.blockRenderer.getBlockModel(snowState).collectParts(randomSource, list);
        poseStack.pushPose();
        poseStack.translate(
                (float) SectionPos.sectionRelative(blockPos3.getX()),
                (float) SectionPos.sectionRelative(blockPos3.getY()),
                (float) SectionPos.sectionRelative(blockPos3.getZ())
        );
        this.blockRenderer.renderBatched(snowState, blockPos3, region, poseStack, bufferBuilder, true, list);
        poseStack.popPose();
        list.clear();
    }
}