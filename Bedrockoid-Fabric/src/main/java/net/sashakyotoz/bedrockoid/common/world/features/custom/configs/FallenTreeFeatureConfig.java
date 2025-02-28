package net.sashakyotoz.bedrockoid.common.world.features.custom.configs;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record FallenTreeFeatureConfig(BlockStateProvider logType, boolean isDoubleTrunk) implements FeatureConfig {
    public static Codec<FallenTreeFeatureConfig> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BlockStateProvider.TYPE_CODEC.fieldOf("logType").forGetter(FallenTreeFeatureConfig::logType),
                    Codec.BOOL.fieldOf("isDoubleTrunk").forGetter(FallenTreeFeatureConfig::isDoubleTrunk)
            ).apply(instance, FallenTreeFeatureConfig::new));
}