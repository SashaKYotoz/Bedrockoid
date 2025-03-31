package net.sashakyotoz.bedrockoid.mixin.client;

import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//frozenblock team copyright
//stating changes: use of yarn mappings
//source code: originally from https://github.com/FrozenBlock/WilderWild/
@Mixin(TerrainRenderContext.class)
public abstract class TerrainRenderContextMixin {
    @Shadow public abstract void tessellateBlock(BlockState blockState, BlockPos blockPos, BakedModel model, MatrixStack matrixStack);

    @Inject(method = "tessellateBlock", at = @At("HEAD"), require = 0)
    public void bedrockoidTessellation(BlockState blockState, BlockPos blockPos, BakedModel model, MatrixStack matrixStack, CallbackInfo ci) {
        if (BlockUtils.isSnowlogged(blockState) && BedrockoidConfig.snowlogging) {
            BlockState snowState = BlockUtils.getSnowEquivalent(blockState);
            this.tessellateBlock(snowState, blockPos, MinecraftClient.getInstance().getBlockRenderManager().getModel(snowState), matrixStack);
        }
    }
}