package net.sashakyotoz.bedrockoid.common.world.features.modifiers;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ClimateSettingsBuilder;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.world.features.BiomeModifiers;

//copyright bl4ckscor3 MIT License https://github.com/bl4ckscor3/SnowUnderTrees/
public record SnowUnderTreeBiomeModifier(Holder<PlacedFeature> snowUnderTreesFeature) implements BiomeModifier {
    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD && BedrockoidConfig.snowSpawnsUnderTrees) {
            ClimateSettingsBuilder climate = builder.getClimateSettings();

            if (climate.hasPrecipitation() && climate.getTemperature() < 0.15F)
                builder.getGenerationSettings().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION.ordinal(), snowUnderTreesFeature);
        }
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return BiomeModifiers.SNOW_UNDER_TREES_BIOME_MODIFIER_CODEC.get();
    }
}