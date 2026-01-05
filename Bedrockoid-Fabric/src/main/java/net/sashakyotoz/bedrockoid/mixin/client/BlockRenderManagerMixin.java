package net.sashakyotoz.bedrockoid.mixin.client;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//frozenblock team copyright
//stating changes: use of yarn mappings
//source code: https://github.com/FrozenBlock/WilderWild/blob/master/src/main/java/net/frozenblock/wilderwild/mixin/snowlogging/client/BlockRenderDispatcherMixin.java
@Mixin(BlockRenderManager.class)
public abstract class BlockRenderManagerMixin {

    @Shadow public abstract void renderDamage(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrices, VertexConsumer vertexConsumer);

    @Inject(method = "renderDamage", at = @At("HEAD"), cancellable = true)
    public void renderSnowDamage(BlockState state, BlockPos pos, BlockRenderView world, MatrixStack matrices, VertexConsumer vertexConsumer, CallbackInfo ci) {
        if (BlockUtils.isSnowlogged(state) && BedrockoidConfig.snowlogging) {
            this.renderDamage(BlockUtils.getSnowEquivalent(state), pos, world, matrices, vertexConsumer);
            ci.cancel();
        }
    }
}