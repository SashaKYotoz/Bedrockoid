package net.sashakyotoz.bedrockoid.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class SnowTints {
    public static final int SNOW_TINT = 0xFFCCCCCC;
    private static final int WHITE = 0xFFFFFF;
    private static final int OPAQUE = 0xFF000000;

    public static void apply(BlockColors colors, BiConsumer<List<BlockTintSource>, Block> registrar) {
        wrap(colors, registrar, Kind.LEAVES, Blocks.OAK_LEAVES, Blocks.SPRUCE_LEAVES, Blocks.BIRCH_LEAVES, Blocks.JUNGLE_LEAVES,
                Blocks.ACACIA_LEAVES, Blocks.DARK_OAK_LEAVES, Blocks.MANGROVE_LEAVES);
        wrap(colors, registrar, Kind.PLANT, Blocks.SHORT_GRASS, Blocks.FERN, Blocks.TALL_GRASS, Blocks.LARGE_FERN, Blocks.SUGAR_CANE);
        wrap(colors, registrar, Kind.VINE, Blocks.VINE);
    }

    private static void wrap(BlockColors colors, BiConsumer<List<BlockTintSource>, Block> registrar, Kind kind, Block... blocks) {
        for (Block block : blocks) {
            List<BlockTintSource> sources = colors.getTintSources(block.defaultBlockState());
            if (sources == null || sources.isEmpty() || sources.get(0) == null || sources.get(0) instanceof SnowTintSource)
                continue;
            List<BlockTintSource> wrapped = new ArrayList<>(sources);
            wrapped.set(0, new SnowTintSource(kind, sources.get(0)));
            registrar.accept(wrapped, block);
        }
    }

    private enum Kind {
        LEAVES,
        PLANT,
        VINE
    }

    private record SnowTintSource(Kind kind, BlockTintSource delegate) implements BlockTintSource {
        @Override
        public int color(BlockState state) {
            return this.delegate.color(state);
        }

        @Override
        public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            int base = this.delegate.colorInWorld(state, level, pos);
            return switch (this.kind) {
                case LEAVES -> leaves(state, level, pos, base);
                case PLANT -> plant(state, level, pos, base);
                case VINE -> vine(pos, base);
            };
        }

        @Override
        public int colorAsTerrainParticle(BlockState state, BlockAndTintGetter level, BlockPos pos) {
            return this.delegate.colorAsTerrainParticle(state, level, pos);
        }
    }

    private static int plant(BlockState state, BlockAndTintGetter level, BlockPos pos, int base) {
        if (!BedrockoidConfig.snowlogging)
            return base;
        if (BlockUtils.isSnowlogged(state))
            return SNOW_TINT;
        if (level != null && pos != null && state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
                && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER
                && BlockUtils.isSnowlogged(level.getBlockState(pos.below())))
            return SNOW_TINT;
        return base;
    }

    private static int vine(BlockPos pos, int base) {
        if (!BedrockoidConfig.snowCoversVines || pos == null)
            return base;
        Biome biome = biomeAt(pos);
        return biome != null && biome.getBaseTemperature() < 0.15f && biome.hasPrecipitation() ? SNOW_TINT : base;
    }

    private static int leaves(BlockState state, BlockAndTintGetter level, BlockPos pos, int base) {
        if (!BedrockoidConfig.snowCoversLeaves || level == null || pos == null)
            return base;
        if (BlockUtils.haveLeavesToChangeColor(state, level, pos))
            return OPAQUE | BlockUtils.blendColors(base, WHITE, 0.95f);
        Biome biome = biomeAt(pos);
        if (biome != null && biome.getBaseTemperature() < 0.1f && biome.hasPrecipitation())
            return OPAQUE | BlockUtils.blendColors(base, WHITE, hasWarmerNeighbour(pos) ? 0.4f : 0.75f);
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