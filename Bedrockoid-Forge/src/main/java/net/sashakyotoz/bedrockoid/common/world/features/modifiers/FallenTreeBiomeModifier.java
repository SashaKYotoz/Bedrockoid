package net.sashakyotoz.bedrockoid.common.world.features.modifiers;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.world.features.BiomeModifiers;

public record FallenTreeBiomeModifier(HolderSet<Biome> biomes, Holder<PlacedFeature> fallenTree) implements BiomeModifier {
    @Override
    public void modify(Holder<Biome> biome, Phase phase, Builder builder) {
        if (phase == Phase.ADD && BedrockoidConfig.fallenTrees && this.biomes.contains(biome))
            builder.getGenerationSettings().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION.ordinal(), fallenTree);
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return BiomeModifiers.FALLEN_TREES_BIOME_MODIFIER_CODEC.get();
    }
}