package net.sashakyotoz.bedrockoid.mixin.client.sodium;

import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//frozenblock team copyright
//stating changes: renaming and refactoring of class' methods and values
//source code: https://github.com/FrozenBlock/WilderWild/blob/1.21.4/src/main/java/net/frozenblock/wilderwild/mixin/client/sodium/BlockRendererMixin.java
@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin {
    @Shadow
    public abstract void renderModel(BakedModel model, BlockState state, BlockPos pos, BlockPos origin);

    @Inject(method = "renderModel", at = @At("HEAD"), remap = false, require = 0)
    private void renderSnowModel(BakedModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo info) {
        if (BlockUtils.isSnowlogged(state)) {
            BlockState snowedState = BlockUtils.getSnowEquivalent(state);
            BakedModel snowModel = MinecraftClient.getInstance().getBlockRenderManager().getModel(snowedState);
            this.renderModel(snowModel, snowedState, pos, origin);
        }
    }
}
