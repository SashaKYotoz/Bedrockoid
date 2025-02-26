package net.sashakyotoz.bedrockoid.common.world.features;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import net.sashakyotoz.bedrockoid.common.world.SnowUnderTreeFeature;

public class BedrockoidFeatures {
    public static void register() {
        Bedrockoid.log("Registering Features for modid : " + Bedrockoid.MOD_ID);
        Registry.register(Registries.FEATURE, Bedrockoid.makeID("snow_under_tree"), SnowUnderTreeFeature.INSTANCE);

        BiomeModifications.addFeature(context -> context.getBiome().getTemperature() < 0.15f && context.getBiome().hasPrecipitation(),
                GenerationStep.Feature.TOP_LAYER_MODIFICATION, SNOW_UNDER_TREES);
    }

    public static final RegistryKey<PlacedFeature> SNOW_UNDER_TREES = create("snow_under_trees");

    public static RegistryKey<PlacedFeature> create(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Bedrockoid.makeID(name));
    }
}