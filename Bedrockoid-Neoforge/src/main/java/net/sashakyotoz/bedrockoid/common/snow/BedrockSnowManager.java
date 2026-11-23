package net.sashakyotoz.bedrockoid.common.snow;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.sashakyotoz.bedrockoid.common.snow.snow_managers.SnowManager;
import net.sashakyotoz.bedrockoid.common.snow.snow_managers.VanillaManager;

import java.util.function.Consumer;

public class BedrockSnowManager {
    private static final int CHUNK_RADIUS = 10;
    private static SnowManager snowManager = new VanillaManager();

    public static void init() {
        snowManager = new VanillaManager();
    }

    public static boolean placeSnow(WorldGenLevel level, BlockPos pos) {
        return snowManager.placeSnow(level, pos);
    }

    public static boolean canSnow(WorldGenLevel level, BlockPos pos) {
        Holder<Biome> biome = level.getBiome(pos);
        if (biome.value().getPrecipitationAt(pos, level.getSeaLevel()) != Biome.Precipitation.SNOW)
            return false;
        BlockState stateAtPos = level.getBlockState(pos);
        if (!stateAtPos.canBeReplaced())
            return false;
        if (biome.value().warmEnoughToRain(pos, level.getSeaLevel()) || !isInBuildRangeAndDarkEnough(level, pos))
            return false;
        BlockPos posBelow = pos.below();
        return level.getBlockState(posBelow).isFaceSturdy(level, posBelow, Direction.UP);
    }

    public static boolean isSnow(WorldGenLevel level, BlockPos pos) {
        return snowManager.isSnow(level, pos);
    }

    public static BlockState getStateAfterMelting(BlockState stateNow, WorldGenLevel level, BlockPos pos) {
        return snowManager.getStateAfterMelting(stateNow, level, pos);
    }

    private static boolean isInBuildRangeAndDarkEnough(WorldGenLevel level, BlockPos pos) {
        return pos.getY() >= level.getMinY() && pos.getY() <= level.getMaxY() && level.getBrightness(LightLayer.BLOCK, pos) < 10;
    }

    public static void runForChunks(ServerLevel level, Consumer<LevelChunk> action) {
        LongSet visited = new LongOpenHashSet();
        for (ServerPlayer player : level.players()) {
            ChunkPos center = player.chunkPosition();
            for (int dx = -CHUNK_RADIUS; dx <= CHUNK_RADIUS; dx++) {
                for (int dz = -CHUNK_RADIUS; dz <= CHUNK_RADIUS; dz++) {
                    int chunkX = center.x() + dx;
                    int chunkZ = center.z() + dz;
                    if (!visited.add(ChunkPos.pack(chunkX, chunkZ)))
                        continue;
                    LevelChunk chunk = level.getChunkSource().getChunkNow(chunkX, chunkZ);
                    if (chunk != null && level.shouldTickBlocksAt(new BlockPos(chunkX << 4, 0, chunkZ << 4)))
                        action.accept(chunk);
                }
            }
        }
    }
}
