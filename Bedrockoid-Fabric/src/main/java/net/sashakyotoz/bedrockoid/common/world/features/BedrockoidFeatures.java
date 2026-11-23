package net.sashakyotoz.bedrockoid.common.world.features;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.world.features.custom.FallenTreeFeature;
import net.sashakyotoz.bedrockoid.common.world.features.custom.SnowUnderTreeFeature;
import net.sashakyotoz.bedrockoid.common.world.features.custom.SwampHugeMushroomFeature;

public class BedrockoidFeatures {
    public static final ResourceKey<PlacedFeature> SNOW_UNDER_TREES = placed("snow_under_trees");
    public static final ResourceKey<PlacedFeature> FALLEN_BIG_SPRUCE_TREE = placed("fallen_big_spruce_tree");
    public static final ResourceKey<PlacedFeature> FALLEN_BIG_JUNGLE_TREE = placed("fallen_big_jungle_tree");
    public static final ResourceKey<PlacedFeature> FALLEN_BIG_PALE_TREE = placed("fallen_big_pale_tree");
    public static final ResourceKey<PlacedFeature> FALLEN_BIG_DARK_OAK_TREE = placed("fallen_big_dark_oak_tree");
    public static final ResourceKey<PlacedFeature> SWAMP_HUGE_MUSHROOM = placed("swamp_huge_mushroom");

    public static void register() {
        Registry.register(BuiltInRegistries.FEATURE, Bedrockoid.makeID("snow_under_tree"), SnowUnderTreeFeature.INSTANCE);
        Registry.register(BuiltInRegistries.FEATURE, Bedrockoid.makeID("fallen_tree"), FallenTreeFeature.INSTANCE);
        Registry.register(BuiltInRegistries.FEATURE, Bedrockoid.makeID("swamp_huge_mushroom"), SwampHugeMushroomFeature.INSTANCE);

        if (BedrockoidConfig.snowSpawnsUnderTrees)
            BiomeModifications.addFeature(context -> context.getBiome().hasPrecipitation() && context.getBiome().getBaseTemperature() < 0.15F,
                    GenerationStep.Decoration.TOP_LAYER_MODIFICATION, SNOW_UNDER_TREES);
        if (BedrockoidConfig.fallenTrees) {
            BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_TAIGA), GenerationStep.Decoration.VEGETAL_DECORATION, FALLEN_BIG_SPRUCE_TREE);
            BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE), GenerationStep.Decoration.VEGETAL_DECORATION, FALLEN_BIG_JUNGLE_TREE);
            BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.PALE_GARDEN), GenerationStep.Decoration.VEGETAL_DECORATION, FALLEN_BIG_PALE_TREE);
            BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.DARK_FOREST), GenerationStep.Decoration.VEGETAL_DECORATION, FALLEN_BIG_DARK_OAK_TREE);
        }
        if (BedrockoidConfig.mushroomTreesInSwamp)
            BiomeModifications.addFeature(BiomeSelectors.includeByKey(Biomes.SWAMP), GenerationStep.Decoration.VEGETAL_DECORATION, SWAMP_HUGE_MUSHROOM);
    }

    private static ResourceKey<PlacedFeature> placed(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Bedrockoid.makeID(name));
    }
}
