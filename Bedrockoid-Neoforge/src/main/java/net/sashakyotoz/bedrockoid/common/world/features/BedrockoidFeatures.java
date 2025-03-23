package net.sashakyotoz.bedrockoid.common.world.features;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import net.sashakyotoz.bedrockoid.common.world.features.custom.FallenTreeFeature;
import net.sashakyotoz.bedrockoid.common.world.features.custom.SnowUnderTreeFeature;
import net.sashakyotoz.bedrockoid.common.world.features.custom.configs.FallenTreeFeatureConfig;

public class BedrockoidFeatures {
    public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create(BuiltInRegistries.FEATURE, Bedrockoid.MOD_ID);
    public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> SNOW_UNDER_TREE = REGISTRY.register("snow_under_tree", () -> SnowUnderTreeFeature.INSTANCE);
    public static final DeferredHolder<Feature<?>, Feature<FallenTreeFeatureConfig>> FALLEN_TREE = REGISTRY.register("fallen_tree", () -> FallenTreeFeature.INSTANCE);
}