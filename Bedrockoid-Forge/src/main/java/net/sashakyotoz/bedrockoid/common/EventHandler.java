package net.sashakyotoz.bedrockoid.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.snow.BedrockSnowManager;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;

@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public void onStartTick(TickEvent.LevelTickEvent event) {
        Level level = event.level;
        if (level instanceof ServerLevel serverLevel && serverLevel.isRaining() && !ModsUtils.isSnowUnderTreesIn()) {

            int randomTickSpeed = serverLevel.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);

            BedrockSnowManager.runForChunks(serverLevel, chunk -> addSnowUnderTrees(serverLevel, chunk, randomTickSpeed));
        }
    }
    private static void addSnowUnderTrees(ServerLevel level, LevelChunk chunk, int randomTickSpeed) {
        ChunkPos chunkPos = chunk.getPos();
        int chunkX = chunkPos.getMinBlockX();
        int chunkZ = chunkPos.getMaxBlockZ();
        randomTickSpeed = Math.min(randomTickSpeed, 50);
        for (int i = 0; i < randomTickSpeed; i++) {
            if (level.random.nextInt(32) == 0) {
                BlockPos randomPos = level.getBlockRandomPos(chunkX, 0, chunkZ, 15);

                if (level.getBlockState(level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, randomPos).below()).is(BlockTags.LEAVES)) {
                    BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, randomPos);
                    Biome biome = level.getBiome(pos).value();
                    boolean biomeDisabled = BedrockoidConfig.disableSnowUnderTreesIn.contains(level.registryAccess().registry(Registries.BIOME).get().getKey(biome).toString());

                    if (!biomeDisabled && BedrockSnowManager.placeSnow(level, pos)) {
                        BlockPos posBelow = pos.below();
                        BlockState stateBelow = level.getBlockState(posBelow);

                        if (stateBelow.hasProperty(SnowyDirtBlock.SNOWY))
                            level.setBlock(posBelow, stateBelow.setValue(SnowyDirtBlock.SNOWY, true), 2);
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public void onStarted(FMLCommonSetupEvent event) {
        BedrockoidConfig.init();
    }
}