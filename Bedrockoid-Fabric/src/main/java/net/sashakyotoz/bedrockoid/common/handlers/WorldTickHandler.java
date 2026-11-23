package net.sashakyotoz.bedrockoid.common.handlers;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.Heightmap;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.snow.BedrockSnowManager;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;

public class WorldTickHandler {

    public static void onStartTick(ServerLevel level) {
        if (!BedrockoidConfig.snowSpawnsUnderTrees || !level.isRaining() || ModsUtils.isSnowUnderTreesIn())
            return;
        int randomTickSpeed = level.getGameRules().get(GameRules.RANDOM_TICK_SPEED);
        BedrockSnowManager.runForChunks(level, chunk -> addSnowUnderTrees(level, chunk, randomTickSpeed));
    }

    private static void addSnowUnderTrees(ServerLevel level, LevelChunk chunk, int randomTickSpeed) {
        int chunkX = chunk.getPos().x() << 4;
        int chunkZ = chunk.getPos().z() << 4;
        int attempts = Math.min(randomTickSpeed, 50);
        for (int i = 0; i < attempts; i++) {
            if (level.getRandom().nextInt(32) != 0)
                continue;
            BlockPos randomPos = level.getBlockRandomPos(chunkX, 0, chunkZ, 15);
            if (!level.getBlockState(level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, randomPos).below()).is(BlockTags.LEAVES))
                continue;
            BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, randomPos);
            String biomeId = level.getBiome(pos).unwrapKey().map(key -> key.identifier().toString()).orElse("");
            if (BedrockoidConfig.disableSnowUnderTreesIn.contains(biomeId))
                continue;
            if (BedrockSnowManager.placeSnow(level, pos)) {
                BlockPos posBelow = pos.below();
                BlockState stateBelow = level.getBlockState(posBelow);
                if (stateBelow.hasProperty(BlockStateProperties.SNOWY))
                    level.setBlock(posBelow, stateBelow.setValue(BlockStateProperties.SNOWY, true), 2);
            }
        }
    }
}