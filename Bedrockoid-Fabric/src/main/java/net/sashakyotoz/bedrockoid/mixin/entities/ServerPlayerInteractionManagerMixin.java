package net.sashakyotoz.bedrockoid.mixin.entities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Shadow
    protected ServerWorld world;

    @Shadow
    @Final
    protected ServerPlayerEntity player;

    @Inject(method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"), cancellable = true)
    private void bedrockoid$breakSnowFirst(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = this.world.getBlockState(pos);
        if (!BedrockoidConfig.snowlogging || !BlockUtils.isSnowlogged(state))
            return;
        BlockState snowState = BlockUtils.getSnowEquivalent(state);
        this.world.setBlockState(pos, state.with(BlockUtils.LAYERS, 0), Block.NOTIFY_ALL);
        if (!this.player.isCreative()) {
            ItemStack tool = this.player.getMainHandStack();
            ItemStack toolCopy = tool.copy();
            boolean canHarvest = this.player.canHarvest(snowState);
            tool.postMine(this.world, snowState, pos, this.player);
            if (canHarvest)
                Block.dropStacks(snowState, this.world, pos, null, this.player, toolCopy);
        }
        cir.setReturnValue(true);
    }
}
