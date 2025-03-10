package net.sashakyotoz.bedrockoid.mixin.entities;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({ServerPlayerGameMode.class})
public class ServerPlayerInteractionManagerMixin {
    @WrapOperation(
            method = "destroyBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;canHarvestBlock(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)Z"
            )
    )
    public boolean destroyBlockB(
            BlockState instance, BlockGetter getter, BlockPos pos, Player player, Operation<Boolean> original, @Local BlockState blockState
    ) {
        if (BlockUtils.isSnowlogged(blockState)) {
            player.level().setBlockAndUpdate(pos, blockState.setValue(BlockUtils.LAYERS, 0));
            return true;
        }
        return original.call(instance,getter, pos, player);
    }
}