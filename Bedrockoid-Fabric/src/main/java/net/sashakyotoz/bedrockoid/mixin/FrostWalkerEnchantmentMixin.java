package net.sashakyotoz.bedrockoid.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FrostWalkerEnchantment.class)
public class FrostWalkerEnchantmentMixin {
    @WrapOperation(method = "freezeWater", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    private static int increaseRadius(int a, int b, Operation<Integer> original) {
        return BedrockoidConfig.frostwalkerBoost ? Math.min(16, b > 1 ? b + 3 : b + 2) : original.call(a,b);
    }

    @WrapOperation(method = "freezeWater", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;nextInt(Lnet/minecraft/util/math/random/Random;II)I"))
    private static int delayIceMelting(Random random, int min, int max, Operation<Integer> original) {
        return BedrockoidConfig.frostwalkerBoost ? MathHelper.nextInt(random, min, max - 20) : original.call(random,min,max);
    }
}