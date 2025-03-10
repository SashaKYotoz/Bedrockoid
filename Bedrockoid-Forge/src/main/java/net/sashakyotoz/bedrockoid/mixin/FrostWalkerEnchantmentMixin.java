package net.sashakyotoz.bedrockoid.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FrostWalkerEnchantment.class)
public class FrostWalkerEnchantmentMixin {
    @WrapOperation(method = "onEntityMoved", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    private static int increaseRadius(int a, int b, Operation<Integer> original) {
        return BedrockoidConfig.frostwalkerBoost ? Math.min(16, b > 1 ? b + 3 : b + 2) : original.call(a,b);
    }

    @WrapOperation(method = "onEntityMoved", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I"))
    private static int delayIceMelting(RandomSource random, int min, int max, Operation<Integer> original) {
        return BedrockoidConfig.frostwalkerBoost ? Mth.nextInt(random, min, max - 20) : original.call(random,min,max);
    }
}