package net.sashakyotoz.bedrockoid.common.world.features.custom;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.TreeConfiguredFeatures;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;

public class SwampHugeMushroomFeature extends Feature<DefaultFeatureConfig> {
    public static final Feature<DefaultFeatureConfig> INSTANCE = new SwampHugeMushroomFeature(DefaultFeatureConfig.CODEC);
    private static final int CLEARANCE_RADIUS = 3;
    private static final int CLEARANCE_HEIGHT = 8;

    public SwampHugeMushroomFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        if (!BedrockoidConfig.mushroomTreesInSwamp)
            return false;
        StructureWorldAccess world = context.getWorld();
        BlockPos origin = context.getOrigin();
        if (!hasClearance(world, origin))
            return false;
        RegistryKey<ConfiguredFeature<?, ?>> mushroom = context.getRandom().nextBoolean() ? TreeConfiguredFeatures.HUGE_RED_MUSHROOM : TreeConfiguredFeatures.HUGE_BROWN_MUSHROOM;
        return world.getRegistryManager().get(RegistryKeys.CONFIGURED_FEATURE).getEntry(mushroom)
                .map(entry -> entry.value().generate(world, context.getGenerator(), context.getRandom(), origin))
                .orElse(false);
    }

    private static boolean hasClearance(StructureWorldAccess world, BlockPos origin) {
        BlockPos.Mutable cursor = new BlockPos.Mutable();
        for (int dx = -CLEARANCE_RADIUS; dx <= CLEARANCE_RADIUS; dx++) {
            for (int dz = -CLEARANCE_RADIUS; dz <= CLEARANCE_RADIUS; dz++) {
                for (int dy = 0; dy <= CLEARANCE_HEIGHT; dy++) {
                    cursor.set(origin, dx, dy, dz);
                    BlockState state = world.getBlockState(cursor);
                    if (state.isIn(BlockTags.LOGS) || state.isIn(BlockTags.LEAVES))
                        return false;
                }
            }
        }
        return true;
    }
}