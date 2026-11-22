package net.sashakyotoz.bedrockoid.client;

import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.Biome;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;

public class SnowColors {
    public static final int SNOW_TINT = 0xCCCCCC;
    private static final int WHITE = 0xFFFFFF;

    public static int averageFoliage(BlockRenderView world, BlockPos pos) {
        return world != null && pos != null ? BiomeColors.getFoliageColor(world, pos) : FoliageColors.getDefaultColor();
    }

    public static int averageGrass(BlockRenderView world, BlockPos pos, int fallback) {
        return world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : fallback;
    }

    public static BlockPos lowerHalf(BlockState state, BlockPos pos) {
        if (pos != null && state.contains(Properties.DOUBLE_BLOCK_HALF) && state.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER)
            return pos.down();
        return pos;
    }

    public static int plant(BlockState state, BlockRenderView world, BlockPos pos, int base) {
        if (!BedrockoidConfig.snowlogging)
            return base;
        if (BlockUtils.isSnowlogged(state))
            return SNOW_TINT;
        BlockPos lower = lowerHalf(state, pos);
        if (world != null && lower != null && lower != pos && BlockUtils.isSnowlogged(world.getBlockState(lower)))
            return SNOW_TINT;
        return base;
    }

    public static int vine(BlockState state, BlockRenderView world, BlockPos pos) {
        int base = averageFoliage(world, pos);
        if (!BedrockoidConfig.snowCoversVines || pos == null)
            return base;
        Biome biome = biomeAt(pos);
        return biome != null && biome.getTemperature() < 0.15f && biome.hasPrecipitation() ? SNOW_TINT : base;
    }

    public static int leaves(BlockState state, BlockRenderView world, BlockPos pos, int base) {
        if (!BedrockoidConfig.snowCoversLeaves || world == null || pos == null)
            return base;
        if (BlockUtils.haveLeavesToChangeColor(state, world, pos))
            return BlockUtils.blendColors(base, WHITE, 0.95f);
        Biome biome = biomeAt(pos);
        if (biome != null && biome.getTemperature() < 0.1f && biome.hasPrecipitation())
            return BlockUtils.blendColors(base, WHITE, hasWarmerNeighbour(pos) ? 0.4f : 0.75f);
        return base;
    }

    private static Biome biomeAt(BlockPos pos) {
        ClientWorld world = MinecraftClient.getInstance().world;
        return world == null ? null : world.getBiome(pos).value();
    }

    private static boolean hasWarmerNeighbour(BlockPos pos) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world == null)
            return false;
        return world.getBiome(pos.north()).value().getTemperature() > 0.1f
                || world.getBiome(pos.south()).value().getTemperature() > 0.1f
                || world.getBiome(pos.east()).value().getTemperature() > 0.1f
                || world.getBiome(pos.west()).value().getTemperature() > 0.1f;
    }
}