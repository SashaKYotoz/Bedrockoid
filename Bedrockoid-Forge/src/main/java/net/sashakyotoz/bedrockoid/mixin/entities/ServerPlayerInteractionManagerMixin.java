package net.sashakyotoz.bedrockoid.mixin.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Shadow
    protected ServerLevel level;

    @Shadow
    @Final
    protected ServerPlayer player;

    @Inject(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayerGameMode;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z", remap = false), cancellable = true)
    private void bedrockoid$breakSnowFirst(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState state = this.level.getBlockState(pos);
        if (!BedrockoidConfig.snowlogging || !BlockUtils.isSnowlogged(state))
            return;
        BlockState snowState = BlockUtils.getSnowEquivalent(state);
        this.level.setBlock(pos, state.setValue(BlockUtils.LAYERS, 0), 3);
        this.level.levelEvent(2001, pos, Block.getId(snowState));
        if (!this.player.isCreative() && snowState.canHarvestBlock(this.level, pos, this.player)) {
            ItemStack tool = this.player.getMainHandItem();
            Block.dropResources(snowState, this.level, pos, null, this.player, tool.copy());
        }
        cir.setReturnValue(true);
    }
}