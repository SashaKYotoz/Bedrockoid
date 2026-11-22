package net.sashakyotoz.bedrockoid.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;

public class SnowColors {
    public static final int SNOW_TINT = 0xCCCCCC;
    private static final int WHITE = 0xFFFFFF;

    public static int averageFoliage(BlockAndTintGetter level, BlockPos pos) {
        return level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.getDefaultColor();
    }

    public static int averageGrass(BlockAndTintGetter level, BlockPos pos, int fallback) {
        return level != null && pos != null ? BiomeColors.getAverageGrassColor(level, pos) : fallback;
    }

    public static BlockPos lowerHalf(BlockState state, BlockPos pos) {
        if (pos != null && state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER)
            return pos.below();
        return pos;
    }

    public static int plant(BlockState state, BlockAndTintGetter level, BlockPos pos, int base) {
        if (!BedrockoidConfig.snowlogging)
            return base;
        if (BlockUtils.isSnowlogged(state))
            return SNOW_TINT;
        BlockPos lower = lowerHalf(state, pos);
        if (level != null && lower != null && lower != pos && BlockUtils.isSnowlogged(level.getBlockState(lower)))
            return SNOW_TINT;
        return base;
    }

    public static int vine(BlockState state, BlockAndTintGetter level, BlockPos pos) {
        int base = averageFoliage(level, pos);
        if (!BedrockoidConfig.snowCoversVines || pos == null)
            return base;
        Biome biome = biomeAt(pos);
        return biome != null && biome.getBaseTemperature() < 0.15f && biome.hasPrecipitation() ? SNOW_TINT : base;
    }

    public static int leaves(BlockState state, BlockAndTintGetter level, BlockPos pos, int base) {
        if (!BedrockoidConfig.snowCoversLeaves || level == null || pos == null)
            return base;
        if (BlockUtils.haveLeavesToChangeColor(state, level, pos))
            return BlockUtils.blendColors(base, WHITE, 0.95f);
        Biome biome = biomeAt(pos);
        if (biome != null && biome.getBaseTemperature() < 0.1f && biome.hasPrecipitation())
            return BlockUtils.blendColors(base, WHITE, hasWarmerNeighbour(pos) ? 0.4f : 0.75f);
        return base;
    }

    private static Biome biomeAt(BlockPos pos) {
        ClientLevel level = Minecraft.getInstance().level;
        return level == null ? null : level.getBiome(pos).value();
    }

    private static boolean hasWarmerNeighbour(BlockPos pos) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null)
            return false;
        return level.getBiome(pos.north()).value().getBaseTemperature() > 0.1f
                || level.getBiome(pos.south()).value().getBaseTemperature() > 0.1f
                || level.getBiome(pos.east()).value().getBaseTemperature() > 0.1f
                || level.getBiome(pos.west()).value().getBaseTemperature() > 0.1f;
    }
}