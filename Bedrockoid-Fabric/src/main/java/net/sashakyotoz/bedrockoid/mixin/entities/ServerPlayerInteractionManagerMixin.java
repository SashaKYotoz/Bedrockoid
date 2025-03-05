package net.sashakyotoz.bedrockoid.mixin.entities;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @WrapOperation(
            method = "tryBreakBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"
            )
    )
    public boolean destroyBlockB(
            ServerWorld instance, BlockPos blockPos, boolean b, Operation<Boolean> original, @Local BlockState destroyedState
    ) {
        if (BlockUtils.isSnowlogged(destroyedState)) {
            instance.setBlockState(blockPos, destroyedState.with(BlockUtils.LAYERS, 0), Block.NOTIFY_ALL);
            return true;
        }
        return original.call(instance, blockPos, b);
    }
}