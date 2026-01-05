package net.sashakyotoz.bedrockoid.mixin.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleBlockFeature.class)
public class SimpleBlockFeatureMixin {
    @Inject(method = "place", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/DoublePlantBlock;placeAt(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;I)V"), cancellable = true)
    private void fixDoublePlantPlacement(FeaturePlaceContext<SimpleBlockConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        BlockState blockstate = context.config().toPlace().getState(context.random(), pos);
        if (level.getBiome(pos).value().hasPrecipitation()
                && level.getBiome(pos).value().getBaseTemperature() < 0.1f
                && blockstate.is(Blocks.LARGE_FERN) && BedrockoidConfig.snowlogging)
            cir.setReturnValue(false);
    }
}