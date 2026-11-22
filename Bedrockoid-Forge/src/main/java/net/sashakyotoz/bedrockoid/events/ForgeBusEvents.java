package net.sashakyotoz.bedrockoid.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.snow.BedrockSnowManager;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;

@Mod.EventBusSubscriber(modid = Bedrockoid.MOD_ID)
public class ForgeBusEvents {
    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.START || !(event.level instanceof ServerLevel serverLevel))
            return;
        if (!BedrockoidConfig.snowSpawnsUnderTrees || !serverLevel.isRaining() || ModsUtils.isSnowUnderTreesIn())
            return;
        int randomTickSpeed = serverLevel.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
        BedrockSnowManager.runForChunks(serverLevel, chunk -> addSnowUnderTrees(serverLevel, chunk, randomTickSpeed));
    }

    private static void addSnowUnderTrees(ServerLevel level, LevelChunk chunk, int randomTickSpeed) {
        ChunkPos chunkPos = chunk.getPos();
        int chunkX = chunkPos.getMinBlockX();
        int chunkZ = chunkPos.getMinBlockZ();
        int attempts = Math.min(randomTickSpeed, 50);
        for (int i = 0; i < attempts; i++) {
            if (level.random.nextInt(32) != 0)
                continue;
            BlockPos randomPos = level.getBlockRandomPos(chunkX, 0, chunkZ, 15);
            if (!level.getBlockState(level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, randomPos).below()).is(BlockTags.LEAVES))
                continue;
            BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, randomPos);
            String biomeId = level.getBiome(pos).unwrapKey().map(key -> key.location().toString()).orElse("");
            if (BedrockoidConfig.disableSnowUnderTreesIn.contains(biomeId))
                continue;
            if (BedrockSnowManager.placeSnow(level, pos)) {
                BlockPos posBelow = pos.below();
                BlockState stateBelow = level.getBlockState(posBelow);
                if (stateBelow.hasProperty(SnowyDirtBlock.SNOWY))
                    level.setBlock(posBelow, stateBelow.setValue(SnowyDirtBlock.SNOWY, true), 2);
            }
        }
    }
}