package net.sashakyotoz.bedrockoid.common.world.features;

import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import net.sashakyotoz.bedrockoid.common.world.features.custom.FallenTreeFeature;
import net.sashakyotoz.bedrockoid.common.world.features.custom.SnowUnderTreeFeature;

public class BedrockoidFeatures {
    public static final DeferredRegister<Feature<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.FEATURES, Bedrockoid.MOD_ID);
    public static final RegistryObject<Feature<?>> SNOW_UNDER_TREE = REGISTRY.register("snow_under_tree", () -> SnowUnderTreeFeature.INSTANCE);
    public static final RegistryObject<Feature<?>> FALLEN_TREE = REGISTRY.register("fallen_tree", () -> FallenTreeFeature.INSTANCE);
}