package net.sashakyotoz.bedrockoid.common.world.features;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import net.sashakyotoz.bedrockoid.common.world.features.custom.FallenTreeFeature;
import net.sashakyotoz.bedrockoid.common.world.features.custom.SnowUnderTreeFeature;
import net.sashakyotoz.bedrockoid.common.world.features.custom.SwampHugeMushroomFeature;

public class BedrockoidFeatures {
    public static final DeferredRegister<MapCodec<? extends Feature>> REGISTRY = DeferredRegister.create(BuiltInRegistries.FEATURE_TYPE, Bedrockoid.MOD_ID);
    public static final DeferredHolder<MapCodec<? extends Feature>, MapCodec<SnowUnderTreeFeature>> SNOW_UNDER_TREE = REGISTRY.register("snow_under_tree", () -> SnowUnderTreeFeature.CODEC);
    public static final DeferredHolder<MapCodec<? extends Feature>, MapCodec<FallenTreeFeature>> FALLEN_TREE = REGISTRY.register("fallen_tree", () -> FallenTreeFeature.CODEC);
    public static final DeferredHolder<MapCodec<? extends Feature>, MapCodec<SwampHugeMushroomFeature>> SWAMP_HUGE_MUSHROOM = REGISTRY.register("swamp_huge_mushroom", () -> SwampHugeMushroomFeature.CODEC);
}
