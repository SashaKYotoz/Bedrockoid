package net.sashakyotoz.bedrockoid.common.events;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.SnowyBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.sashakyotoz.bedrockoid.common.ModsUtils;
import net.sashakyotoz.bedrockoid.common.snow.BedrockSnowManager;

public class WorldTickHandler implements ServerTickEvents.StartWorldTick, ServerTickEvents.EndWorldTick {
    @Override
    public void onStartTick(ServerWorld serverWorld) {
        if (serverWorld.isRaining()) {
//            if (BedrockSnowManager.isSereneSeasonsLoaded() && !SereneSeasonsHandler.generateSnowAndIce())
//                return;

            int randomTickSpeed = serverWorld.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED);

            BedrockSnowManager.runForChunks(serverWorld, chunk -> addSnowUnderTrees(serverWorld, chunk, randomTickSpeed));
        }
    }

    private static void addSnowUnderTrees(ServerWorld world, WorldChunk chunk, int randomTickSpeed) {
        ChunkPos chunkPos = chunk.getPos();
        int chunkX = chunkPos.getStartX();
        int chunkZ = chunkPos.getStartZ();

        for (int i = 0; i < randomTickSpeed; i++) {
            if (world.random.nextInt(48) == 0) {
                BlockPos randomPos = world.getRandomPosInChunk(chunkX, 0, chunkZ, 15);

                if (world.getBlockState(world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, randomPos).down()).isIn(BlockTags.LEAVES)) {
                    BlockPos pos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, randomPos);
                    Biome biome = world.getBiome(pos).value();
//                    boolean biomeDisabled = Configuration.CONFIG.filteredBiomes.get().contains(world.registryAccess().lookupOrThrow(Registries.BIOME).getKey(biome).toString());

                    if (BedrockSnowManager.placeSnow(world, pos)) {
                        BlockPos posBelow = pos.down();
                        BlockState stateBelow = world.getBlockState(posBelow);

                        if (stateBelow.contains(SnowyBlock.SNOWY))
                            world.setBlockState(posBelow, stateBelow.with(SnowyBlock.SNOWY, true), 2);
                    }
                }
            }
        }
    }

    @Override
    public void onEndTick(ServerWorld serverWorld) {
//        if (ModsUtils.isSereneSeasonsIn())
//            SereneSeasonsHandler.tryMeltSnowUnderTrees((ServerLevel) event.getLevel());
    }
}
