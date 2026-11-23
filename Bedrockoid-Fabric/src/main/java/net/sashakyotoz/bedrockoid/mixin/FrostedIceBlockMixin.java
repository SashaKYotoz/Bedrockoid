package net.sashakyotoz.bedrockoid.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.FrostedIceBlock;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FrostedIceBlock.class)
public abstract class FrostedIceBlockMixin {
    @WrapOperation(method = "onPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I"), require = 0)
    private int bedrockoid$delayIceMelting(RandomSource random, int minInclusive, int maxInclusive, Operation<Integer> original) {
        return BedrockoidConfig.frostwalkerBoost ? original.call(random, minInclusive + 20, maxInclusive + 20) : original.call(random, minInclusive, maxInclusive);
    }
}