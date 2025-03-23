package net.sashakyotoz.bedrockoid.common.world.features;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import net.sashakyotoz.bedrockoid.common.world.features.modifiers.FallenTreeBiomeModifier;
import net.sashakyotoz.bedrockoid.common.world.features.modifiers.SnowUnderTreeBiomeModifier;

public class BiomeModifiers {
    public static final DeferredRegister<MapCodec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.BIOME_MODIFIER_SERIALIZERS, Bedrockoid.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<SnowUnderTreeBiomeModifier>> SNOW_UNDER_TREES_BIOME_MODIFIER_CODEC = BIOME_MODIFIER_SERIALIZERS.register("snow_under_trees",
            () -> RecordCodecBuilder.mapCodec(builder -> builder.group(PlacedFeature.CODEC.fieldOf("feature").forGetter(SnowUnderTreeBiomeModifier::snowUnderTreesFeature)).apply(builder, SnowUnderTreeBiomeModifier::new)));
    public static final DeferredHolder<MapCodec<? extends BiomeModifier>, MapCodec<FallenTreeBiomeModifier>> FALLEN_TREES_BIOME_MODIFIER_CODEC = BIOME_MODIFIER_SERIALIZERS.register("fallen_tree",
            () -> RecordCodecBuilder.mapCodec(builder -> builder.group(Biome.LIST_CODEC.fieldOf("biomes").forGetter(FallenTreeBiomeModifier::biomes), PlacedFeature.CODEC.fieldOf("feature").forGetter(FallenTreeBiomeModifier::fallenTree)).apply(builder, FallenTreeBiomeModifier::new)));
}