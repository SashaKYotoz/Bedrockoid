package net.sashakyotoz.bedrockoid.mixin.client.sodium;

import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.util.math.BlockPos;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Environment(EnvType.CLIENT)
@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin {
    @Unique
    private static final BlockRenderManager BLOCK_MODEL_SHAPER = MinecraftClient.getInstance().getBlockRenderManager();

    @Shadow
    public abstract void renderModel(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin);

    @Inject(method = "renderModel", at = @At("HEAD"), remap = false, require = 0)
    public void renderModel(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo info) {
        if (!BlockUtils.isSnowlogged(state)) return;
        final BlockState snowState = BlockUtils.getSnowEquivalent(state);
        final BlockStateModel snowModel = BLOCK_MODEL_SHAPER.getModel(snowState);
        this.renderModel(snowModel, snowState, pos, origin);
    }

}