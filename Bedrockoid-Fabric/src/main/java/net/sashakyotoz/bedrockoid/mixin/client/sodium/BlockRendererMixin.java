package net.sashakyotoz.bedrockoid.mixin.client.sodium;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer")
public abstract class BlockRendererMixin {
    @Shadow(remap = false)
    public abstract void renderModel(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin);

    @Inject(method = "renderModel", at = @At("HEAD"), remap = false, require = 0)
    private void bedrockoid$renderSnowLayer(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        if (!BedrockoidConfig.snowlogging || !BlockUtils.isSnowlogged(state))
            return;
        BlockState snowState = BlockUtils.getSnowEquivalent(state);
        this.renderModel(Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(snowState), snowState, pos, origin);
    }
}
