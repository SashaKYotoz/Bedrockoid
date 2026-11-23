package net.sashakyotoz.bedrockoid.mixin.worldgen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Biome.class)
public class BiomeMixin {
    @WrapOperation(
            method = "shouldSnow",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z"
            )
    )
    private boolean bedrockoid$shouldSnow(BlockState instance, Object block, Operation<Boolean> original) {
        return original.call(instance, block) || (BedrockoidConfig.snowlogging && BlockUtils.canSnowlog(instance));
    }
}
