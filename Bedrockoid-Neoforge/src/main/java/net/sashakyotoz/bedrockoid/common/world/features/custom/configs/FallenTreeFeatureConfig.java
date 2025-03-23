package net.sashakyotoz.bedrockoid.common.world.features.custom.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record FallenTreeFeatureConfig(BlockStateProvider logType, boolean isDoubleTrunk) implements FeatureConfiguration {
    public static Codec<FallenTreeFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BlockStateProvider.CODEC.fieldOf("logType").forGetter(FallenTreeFeatureConfig::logType),
                    Codec.BOOL.fieldOf("isDoubleTrunk").forGetter(FallenTreeFeatureConfig::isDoubleTrunk)
            ).apply(instance, FallenTreeFeatureConfig::new));
}