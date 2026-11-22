package net.sashakyotoz.bedrockoid.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FrostWalkerEnchantment.class)
public class FrostWalkerEnchantmentMixin {
    @WrapOperation(method = "onEntityMoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;closerToCenterThan(Lnet/minecraft/core/Position;D)Z"), require = 0)
    private static boolean bedrockoid$squareArea(BlockPos instance, Position position, double distance, Operation<Boolean> original) {
        return BedrockoidConfig.frostwalkerBoost || original.call(instance, position, distance);
    }

    @WrapOperation(method = "onEntityMoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I"))
    private static int bedrockoid$delayIceMelting(RandomSource random, int min, int max, Operation<Integer> original) {
        return BedrockoidConfig.frostwalkerBoost ? original.call(random, min + 20, max + 20) : original.call(random, min, max);
    }
}