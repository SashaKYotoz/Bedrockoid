package net.sashakyotoz.bedrockoid.common.snow;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.sashakyotoz.bedrockoid.common.snow.snow_managers.SnowManager;
import net.sashakyotoz.bedrockoid.common.snow.snow_managers.VanillaManager;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class BedrockSnowManager {
    private static SnowManager snowManager;
    private static ChunkRunner chunkRunner;
    private static BiFunction<WorldGenLevel, BlockPos, Boolean> temperatureCheck;

    public static void init() {
//        if (ModsUtils.isSnowRealMagicIn())
//            snowManager = new SnowRealMagicManager();
//        else
            snowManager = new VanillaManager();
        temperatureCheck = (level, pos) -> !level.getBiome(pos).value().warmEnoughToRain(pos,level.getSeaLevel());
        chunkRunner = (level, action) -> level.getChunkSource().chunkMap.getChunks().forEach(chunkHolder -> chunkHolder.getEntityTickingChunkFuture().getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK).ifSuccess(action));
    }

    public static boolean placeSnow(WorldGenLevel level, BlockPos pos) {
        return snowManager.placeSnow(level, pos);
    }

    public static boolean canSnow(WorldGenLevel level, BlockPos pos) {
        Holder<Biome> biome = level.getBiome(pos);

        if (biome.value().getPrecipitationAt(pos,level.getSeaLevel()) == Biome.Precipitation.SNOW) {
            BlockState stateAtPos = level.getBlockState(pos);

            if (!stateAtPos.canBeReplaced())
                return false;

            if (temperatureCheck.apply(level, pos) && isInBuildRangeAndDarkEnough(level, pos)) {
                BlockPos posBelow = pos.below();
                BlockState stateBelow = level.getBlockState(posBelow);

                return stateBelow.isFaceSturdy(level, posBelow, Direction.UP);
            }
        }

        return false;
    }

    public static boolean isSnow(WorldGenLevel level, BlockPos pos) {
        return snowManager.isSnow(level, pos);
    }

    public static BlockState getStateAfterMelting(BlockState stateNow, WorldGenLevel level, BlockPos pos) {
        return snowManager.getStateAfterMelting(stateNow, level, pos);
    }

    private static boolean isInBuildRangeAndDarkEnough(WorldGenLevel level, BlockPos pos) {
        return pos.getY() >= level.getMinY() && pos.getY() <= level.getHeight() && level.getBrightness(LightLayer.BLOCK, pos) < 10;
    }

    public static void runForChunks(ServerLevel level, Consumer<LevelChunk> action) {
        ServerChunkCache cache = level.getChunkSource();

        chunkRunner.run(level, chunk -> {
            ChunkPos chunkPos = chunk.getPos();

            if ((level.isNaturalSpawningAllowed(chunkPos) && cache.chunkMap.anyPlayerCloseEnoughForSpawning(chunkPos)) || cache.chunkMap.getDistanceManager().inBlockTickingRange(chunkPos.toLong())) {
                if (level.shouldTickBlocksAt(chunkPos.toLong()))
                    action.accept(chunk);
            }
        });
    }

    @FunctionalInterface
    private interface ChunkRunner {
        void run(ServerLevel level, Consumer<LevelChunk> action);
    }
}