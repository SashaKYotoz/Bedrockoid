package net.sashakyotoz.bedrockoid.common.world.features;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.world.features.custom.FallenTreeFeature;
import net.sashakyotoz.bedrockoid.common.world.features.custom.SnowUnderTreeFeature;
import net.sashakyotoz.bedrockoid.common.world.features.custom.configs.FallenTreeFeatureConfig;

import java.util.List;

public class BedrockoidFeatures {
    public static void register() {
        Bedrockoid.log("Registering Features for modid : " + Bedrockoid.MOD_ID);
        Registry.register(Registries.FEATURE, Bedrockoid.makeID("snow_under_tree"), SnowUnderTreeFeature.INSTANCE);
        Registry.register(Registries.FEATURE, Bedrockoid.makeID("fallen_tree"), FallenTreeFeature.INSTANCE);

        if (BedrockoidConfig.snowSpawnsUnderTrees){
            BiomeModifications.addFeature(context -> context.getBiome().getTemperature() < 0.15f && context.getBiome().hasPrecipitation(),
                    GenerationStep.Feature.TOP_LAYER_MODIFICATION, SNOW_UNDER_TREES);
        }
        if (BedrockoidConfig.fallenTrees){
            BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.FOREST, BiomeKeys.FLOWER_FOREST),
                    GenerationStep.Feature.VEGETAL_DECORATION, FALLEN_OAK_TREE);
            BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.DARK_FOREST),
                    GenerationStep.Feature.VEGETAL_DECORATION, FALLEN_DARK_OAK_TREE);
            BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.BIRCH_FOREST, BiomeKeys.FLOWER_FOREST),
                    GenerationStep.Feature.VEGETAL_DECORATION, FALLEN_BIRCH_TREE);
            BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_TAIGA),
                    GenerationStep.Feature.VEGETAL_DECORATION, FALLEN_SPRUCE_TREE);
            BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA),
                    GenerationStep.Feature.VEGETAL_DECORATION, FALLEN_BIG_SPRUCE_TREE);
            BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.IS_JUNGLE),
                    GenerationStep.Feature.VEGETAL_DECORATION, FALLEN_JUNGLE_TREE);
            BiomeModifications.addFeature(BiomeSelectors.includeByKey(BiomeKeys.JUNGLE),
                    GenerationStep.Feature.VEGETAL_DECORATION, FALLEN_BIG_JUNGLE_TREE);
        }

        if (BedrockoidConfig.mushroomTreesInSwamp){
            BiomeModifications.addFeature(BiomeSelectors.tag(BiomeTags.SWAMP_HUT_HAS_STRUCTURE),
                    GenerationStep.Feature.TOP_LAYER_MODIFICATION, VegetationPlacedFeatures.MUSHROOM_ISLAND_VEGETATION);
        }
    }

    //configured features
    public static final RegistryKey<ConfiguredFeature<?, ?>> SNOW_UNDER_TREES_CF = createCF("snow_under_trees");

    public static final RegistryKey<ConfiguredFeature<?, ?>> FALLEN_OAK_TREE_CF = createCF("fallen_oak_tree");
    public static final RegistryKey<ConfiguredFeature<?, ?>> FALLEN_DARK_OAK_TREE_CF = createCF("fallen_dark_oak_tree");
    public static final RegistryKey<ConfiguredFeature<?, ?>> FALLEN_BIRCH_TREE_CF = createCF("fallen_birch_tree");
    public static final RegistryKey<ConfiguredFeature<?, ?>> FALLEN_SPRUCE_TREE_CF = createCF("fallen_spruce_tree");
    public static final RegistryKey<ConfiguredFeature<?, ?>> FALLEN_BIG_SPRUCE_TREE_CF = createCF("fallen_big_spruce_tree");
    public static final RegistryKey<ConfiguredFeature<?, ?>> FALLEN_JUNGLE_TREE_CF = createCF("fallen_jungle_tree");
    public static final RegistryKey<ConfiguredFeature<?, ?>> FALLEN_BIG_JUNGLE_TREE_CF = createCF("fallen_big_jungle_tree");

    public static RegistryKey<ConfiguredFeature<?, ?>> createCF(String name) {
        return RegistryKey.of(RegistryKeys.CONFIGURED_FEATURE, Bedrockoid.makeID(name));
    }

    public static void boostrapCF(Registerable<ConfiguredFeature<?, ?>> context) {
        registerCF(context, SNOW_UNDER_TREES_CF, SnowUnderTreeFeature.INSTANCE, DefaultFeatureConfig.INSTANCE);

        registerCF(context, FALLEN_OAK_TREE_CF, FallenTreeFeature.INSTANCE, new FallenTreeFeatureConfig(BlockStateProvider.of(Blocks.OAK_LOG), false));
        registerCF(context, FALLEN_DARK_OAK_TREE_CF, FallenTreeFeature.INSTANCE, new FallenTreeFeatureConfig(BlockStateProvider.of(Blocks.DARK_OAK_LOG), true));
        registerCF(context, FALLEN_BIRCH_TREE_CF, FallenTreeFeature.INSTANCE, new FallenTreeFeatureConfig(BlockStateProvider.of(Blocks.BIRCH_LOG), false));
        registerCF(context, FALLEN_SPRUCE_TREE_CF, FallenTreeFeature.INSTANCE, new FallenTreeFeatureConfig(BlockStateProvider.of(Blocks.SPRUCE_LOG), false));
        registerCF(context, FALLEN_BIG_SPRUCE_TREE_CF, FallenTreeFeature.INSTANCE, new FallenTreeFeatureConfig(BlockStateProvider.of(Blocks.SPRUCE_LOG), true));
        registerCF(context, FALLEN_JUNGLE_TREE_CF, FallenTreeFeature.INSTANCE, new FallenTreeFeatureConfig(BlockStateProvider.of(Blocks.JUNGLE_LOG), false));
        registerCF(context, FALLEN_BIG_JUNGLE_TREE_CF, FallenTreeFeature.INSTANCE, new FallenTreeFeatureConfig(BlockStateProvider.of(Blocks.JUNGLE_LOG), true));
    }

    private static <FC extends FeatureConfig, F extends Feature<FC>> void registerCF(Registerable<ConfiguredFeature<?, ?>> context,
                                                                                     RegistryKey<ConfiguredFeature<?, ?>> key, F feature, FC config) {
        context.register(key, new ConfiguredFeature<>(feature, config));
    }

    //placed features
    public static final RegistryKey<PlacedFeature> SNOW_UNDER_TREES = createPF("snow_under_trees");

    public static final RegistryKey<PlacedFeature> FALLEN_OAK_TREE = createPF("fallen_oak_tree");
    public static final RegistryKey<PlacedFeature> FALLEN_DARK_OAK_TREE = createPF("fallen_dark_oak_tree");
    public static final RegistryKey<PlacedFeature> FALLEN_BIRCH_TREE = createPF("fallen_birch_tree");
    public static final RegistryKey<PlacedFeature> FALLEN_SPRUCE_TREE = createPF("fallen_spruce_tree");
    public static final RegistryKey<PlacedFeature> FALLEN_BIG_SPRUCE_TREE = createPF("fallen_big_spruce_tree");
    public static final RegistryKey<PlacedFeature> FALLEN_JUNGLE_TREE = createPF("fallen_jungle_tree");
    public static final RegistryKey<PlacedFeature> FALLEN_BIG_JUNGLE_TREE = createPF("fallen_big_jungle_tree");

    public static RegistryKey<PlacedFeature> createPF(String name) {
        return RegistryKey.of(RegistryKeys.PLACED_FEATURE, Bedrockoid.makeID(name));
    }

    public static void boostrapPF(Registerable<PlacedFeature> context) {
        var configLookup = context.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);
        registerPF(context, SNOW_UNDER_TREES, configLookup.getOrThrow(SNOW_UNDER_TREES_CF));

        registerPF(context, FALLEN_OAK_TREE, configLookup.getOrThrow(FALLEN_OAK_TREE_CF),
                VegetationPlacedFeatures.treeModifiersWithWouldSurvive(PlacedFeatures.createCountExtraModifier(0, 0.25f, 1), Blocks.OAK_SAPLING));
        registerPF(context, FALLEN_DARK_OAK_TREE, configLookup.getOrThrow(FALLEN_DARK_OAK_TREE_CF),
                VegetationPlacedFeatures.treeModifiersWithWouldSurvive(PlacedFeatures.createCountExtraModifier(0, 0.1f, 1), Blocks.DARK_OAK_SAPLING));
        registerPF(context, FALLEN_BIRCH_TREE, configLookup.getOrThrow(FALLEN_BIRCH_TREE_CF),
                VegetationPlacedFeatures.treeModifiersWithWouldSurvive(PlacedFeatures.createCountExtraModifier(0, 0.25f, 1), Blocks.BIRCH_SAPLING));
        registerPF(context, FALLEN_SPRUCE_TREE, configLookup.getOrThrow(FALLEN_SPRUCE_TREE_CF),
                VegetationPlacedFeatures.treeModifiersWithWouldSurvive(PlacedFeatures.createCountExtraModifier(0, 0.25f, 1), Blocks.SPRUCE_SAPLING));
        registerPF(context, FALLEN_BIG_SPRUCE_TREE, configLookup.getOrThrow(FALLEN_BIG_SPRUCE_TREE_CF),
                VegetationPlacedFeatures.treeModifiersWithWouldSurvive(PlacedFeatures.createCountExtraModifier(0, 0.1f, 1), Blocks.SPRUCE_SAPLING));
        registerPF(context, FALLEN_JUNGLE_TREE, configLookup.getOrThrow(FALLEN_JUNGLE_TREE_CF),
                VegetationPlacedFeatures.treeModifiersWithWouldSurvive(PlacedFeatures.createCountExtraModifier(0, 0.25f, 1), Blocks.JUNGLE_SAPLING));
        registerPF(context, FALLEN_BIG_JUNGLE_TREE, configLookup.getOrThrow(FALLEN_BIG_JUNGLE_TREE_CF),
                VegetationPlacedFeatures.treeModifiersWithWouldSurvive(PlacedFeatures.createCountExtraModifier(0, 0.1f, 1), Blocks.JUNGLE_SAPLING));
    }

    private static void registerPF(Registerable<PlacedFeature> context,
                                   RegistryKey<PlacedFeature> key,
                                   RegistryEntry<ConfiguredFeature<?, ?>> config,
                                   List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(config, List.copyOf(modifiers)));
    }

    private static void registerPF(Registerable<PlacedFeature> context,
                                   RegistryKey<PlacedFeature> key,
                                   RegistryEntry<ConfiguredFeature<?, ?>> config,
                                   PlacementModifier... modifiers) {
        registerPF(context, key, config, List.of(modifiers));
    }
}