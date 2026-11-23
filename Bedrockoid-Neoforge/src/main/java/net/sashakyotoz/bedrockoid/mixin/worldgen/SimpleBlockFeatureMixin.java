package net.sashakyotoz.bedrockoid.mixin.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.SimpleBlockFeature;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleBlockFeature.class)
public class SimpleBlockFeatureMixin {
    @Inject(method = "place(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
    private void bedrockoid$skipLargeFernsInSnow(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!BedrockoidConfig.snowlogging || !(((SimpleBlockFeature) (Object) this).toPlace().value() instanceof SimpleStateProvider provider))
            return;
        if (!provider.state().is(Blocks.LARGE_FERN))
            return;
        Biome biome = level.getBiome(pos).value();
        if (biome.hasPrecipitation() && biome.getBaseTemperature() < 0.1f)
            cir.setReturnValue(false);
    }
}