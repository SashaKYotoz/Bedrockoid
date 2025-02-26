package net.sashakyotoz.bedrockoid.common.snow;

import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.sashakyotoz.bedrockoid.common.snow.snow_managers.SnowManager;
import net.sashakyotoz.bedrockoid.common.snow.snow_managers.VanillaManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

// Code goes under Copyright (c) 2022 bl4ckscor3 from SnowUnderTrees project
public class BedrockSnowManager {
    private static final List<Identifier> biomesToAddTo = new ArrayList<>();
    private static SnowManager snowManager;
    private static ChunkRunner chunkRunner;
    private static boolean isSereneSeasonsLoaded;
    private static BiFunction<StructureWorldAccess, BlockPos, Boolean> temperatureCheck;

    public static void init() {
//        if (ModsUtils.isSnowRealMagicIn())
//            snowManager = new SnowRealMagicManager();
//        else
        snowManager = new VanillaManager();

//        if (isSereneSeasonsLoaded)
//            temperatureCheck = (level, pos) -> SereneSeasonsHandler.coldEnoughToSnow(level, level.getBiome(pos), pos, level.getSeaLevel());
//        else
        temperatureCheck = (level, pos) -> !level.getBiome(pos).value().doesNotSnow(pos);
        chunkRunner = (level, action) -> level.getChunkManager().threadedAnvilChunkStorage.entryIterator().forEach(chunkHolder -> chunkHolder.getEntityTickingFuture().getNow(ChunkHolder.UNLOADED_WORLD_CHUNK).ifLeft(action));
    }

    public static void addSnowUnderTrees(Identifier biomeName) {
        if (!biomesToAddTo.contains(biomeName))
            biomesToAddTo.add(biomeName);
    }

    public static boolean placeSnow(StructureWorldAccess level, BlockPos pos) {
        return snowManager.placeSnow(level, pos);
    }

    public static boolean canSnow(StructureWorldAccess level, BlockPos pos) {
        RegistryEntry<Biome> biome = level.getBiome(pos);

        if (biome.value().getPrecipitation(pos) == Biome.Precipitation.SNOW) {
            BlockState stateAtPos = level.getBlockState(pos);

            if (!stateAtPos.isReplaceable())
                return false;

            if (temperatureCheck.apply(level, pos) && isInBuildRangeAndDarkEnough(level, pos)) {
                BlockPos posBelow = pos.down();
                BlockState stateBelow = level.getBlockState(posBelow);

                return stateBelow.isSideSolidFullSquare(level, posBelow, Direction.UP);
            }
        }

        return false;
    }

    public static boolean isSnow(StructureWorldAccess level, BlockPos pos) {
        return snowManager.isSnow(level, pos);
    }

    public static BlockState getStateAfterMelting(BlockState stateNow, StructureWorldAccess level, BlockPos pos) {
        return snowManager.getStateAfterMelting(stateNow, level, pos);
    }

    private static boolean isInBuildRangeAndDarkEnough(StructureWorldAccess level, BlockPos pos) {
        return pos.getY() >= level.getBottomY() && pos.getY() <= level.getTopY() && level.getLightLevel(LightType.BLOCK, pos) < 10;
    }

    public static void runForChunks(ServerWorld level, Consumer<WorldChunk> action) {
        ServerChunkManager cache = level.getChunkManager();

        chunkRunner.run(level, chunk -> {
            ChunkPos chunkPos = chunk.getPos();

            if ((level.shouldTick(chunkPos) && cache.threadedAnvilChunkStorage.shouldTick(chunkPos)) || cache.threadedAnvilChunkStorage.getTicketManager().shouldTickBlocks(chunkPos.toLong())) {
                if (level.shouldTickBlocksInChunk(chunkPos.toLong()))
                    action.accept(chunk);
            }
        });
    }

    @FunctionalInterface
    private interface ChunkRunner {
        void run(ServerWorld level, Consumer<WorldChunk> action);
    }
}