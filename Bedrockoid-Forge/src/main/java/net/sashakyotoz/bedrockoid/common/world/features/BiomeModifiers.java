package net.sashakyotoz.bedrockoid.common.world.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import net.sashakyotoz.bedrockoid.common.world.features.modifiers.FallenTreeBiomeModifier;
import net.sashakyotoz.bedrockoid.common.world.features.modifiers.SnowUnderTreeBiomeModifier;

public class BiomeModifiers {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Bedrockoid.MOD_ID);

    public static final RegistryObject<Codec<SnowUnderTreeBiomeModifier>> SNOW_UNDER_TREES_BIOME_MODIFIER_CODEC = BIOME_MODIFIER_SERIALIZERS.register("snow_under_trees",
            () -> RecordCodecBuilder.create(builder -> builder.group(PlacedFeature.CODEC.fieldOf("feature").forGetter(SnowUnderTreeBiomeModifier::snowUnderTreesFeature)).apply(builder, SnowUnderTreeBiomeModifier::new)));
    public static final RegistryObject<Codec<FallenTreeBiomeModifier>> FALLEN_TREES_BIOME_MODIFIER_CODEC = BIOME_MODIFIER_SERIALIZERS.register("fallen_tree",
            () -> RecordCodecBuilder.create(builder -> builder.group(Biome.LIST_CODEC.fieldOf("biomes").forGetter(FallenTreeBiomeModifier::biomes),PlacedFeature.CODEC.fieldOf("feature").forGetter(FallenTreeBiomeModifier::fallenTree)).apply(builder, FallenTreeBiomeModifier::new)));
}