package net.sashakyotoz.bedrockoid.mixin.client.sodium;

import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin {

    @Shadow
    public abstract void renderModel(BlockRenderContext ctx, ChunkBuildBuffers buffers);

    @Inject(method = "renderModel", at = @At("HEAD"), remap = false, require = 0)
    private void renderSnowModel(BlockRenderContext ctx, ChunkBuildBuffers buffers, CallbackInfo ci) {
        if (BlockUtils.isSnowlogged(ctx.state())) {
            BlockState snowedState = BlockUtils.getSnowEquivalent(ctx.state());
            BakedModel snowModel = MinecraftClient.getInstance().getBlockRenderManager().getModel(snowedState);
            BlockRenderContext context = new BlockRenderContext(ctx.world());
            Vector3fc origin = ctx.origin();
            context.update(
                    ctx.pos(),
                    new BlockPos((int) origin.x(), (int) origin.y(), (int) origin.z()),
                    snowedState,
                    snowModel,
                    ctx.seed()
            );
            this.renderModel(context, buffers);
        }
    }
}
