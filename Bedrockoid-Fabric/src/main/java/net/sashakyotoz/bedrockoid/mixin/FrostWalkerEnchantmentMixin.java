package net.sashakyotoz.bedrockoid.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.enchantment.FrostWalkerEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FrostWalkerEnchantment.class)
public class FrostWalkerEnchantmentMixin {
    @WrapOperation(method = "freezeWater", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    private static int increaseRadius(int a, int b, Operation<Integer> original) {
        return Math.min(16, b + 3);
    }
}