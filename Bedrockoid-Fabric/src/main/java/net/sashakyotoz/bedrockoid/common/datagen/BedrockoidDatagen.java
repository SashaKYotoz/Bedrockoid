package net.sashakyotoz.bedrockoid.common.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import net.sashakyotoz.bedrockoid.common.world.features.BedrockoidFeatures;

public class BedrockoidDatagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ModWorldGenerator::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, BedrockoidFeatures::boostrapCF);
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, BedrockoidFeatures::boostrapPF);
    }
}
