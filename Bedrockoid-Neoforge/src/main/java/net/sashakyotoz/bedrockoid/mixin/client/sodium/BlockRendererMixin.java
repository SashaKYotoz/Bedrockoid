package net.sashakyotoz.bedrockoid.mixin.client.sodium;

import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin {
    @Shadow
    public abstract void renderModel(BakedModel model, BlockState state, BlockPos pos, BlockPos origin);

    @Inject(method = "renderModel", at = @At("HEAD"), remap = false, require = 0)
    private void renderSnowModel(BakedModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        if (BlockUtils.isSnowlogged(state)) {
            BlockState snowedState = BlockUtils.getSnowEquivalent(state);
            BakedModel snowModel = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(snowedState);
            this.renderModel(snowModel, snowedState, pos, origin);
        }
    }
}