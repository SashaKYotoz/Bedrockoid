package net.sashakyotoz.bedrockoid.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.FrostedIceBlock;
import net.minecraft.util.math.random.Random;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FrostedIceBlock.class)
public abstract class FrostedIceBlockMixin {
    @WrapOperation(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;nextInt(Lnet/minecraft/util/math/random/Random;II)I"), require = 0)
    private int bedrockoid$delayIceMelting(Random random, int min, int max, Operation<Integer> original) {
        return BedrockoidConfig.frostwalkerBoost ? original.call(random, min + 20, max + 20) : original.call(random, min, max);
    }
}
