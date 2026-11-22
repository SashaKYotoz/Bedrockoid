package net.sashakyotoz.bedrockoid.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.random.Random;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FrostWalkerEnchantment.class)
public class FrostWalkerEnchantmentMixin {
    @WrapOperation(method = "freezeWater", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;isWithinDistance(Lnet/minecraft/util/math/Position;D)Z"), require = 0)
    private static boolean bedrockoid$squareArea(BlockPos instance, Position position, double distance, Operation<Boolean> original) {
        return BedrockoidConfig.frostwalkerBoost || original.call(instance, position, distance);
    }

    @WrapOperation(method = "freezeWater", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;nextInt(Lnet/minecraft/util/math/random/Random;II)I"))
    private static int bedrockoid$delayIceMelting(Random random, int min, int max, Operation<Integer> original) {
        return BedrockoidConfig.frostwalkerBoost ? original.call(random, min + 20, max + 20) : original.call(random, min, max);
    }
}
