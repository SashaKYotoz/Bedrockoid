package net.sashakyotoz.bedrockoid.mixin.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleBlockFeature.class)
public class SimpleBlockFeatureMixin {
    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void bedrockoid$skipLargeFernsInSnow(FeaturePlaceContext<SimpleBlockConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
        if (!BedrockoidConfig.snowlogging || !(context.config().toPlace() instanceof SimpleStateProvider provider))
            return;
        WorldGenLevel level = context.level();
        BlockPos pos = context.origin();
        if (!provider.getState(level, context.random(), pos).is(Blocks.LARGE_FERN))
            return;
        Biome biome = level.getBiome(pos).value();
        if (biome.hasPrecipitation() && biome.getBaseTemperature() < 0.1f)
            cir.setReturnValue(false);
    }
}
