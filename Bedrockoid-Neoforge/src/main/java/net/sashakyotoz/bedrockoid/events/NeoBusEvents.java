package net.sashakyotoz.bedrockoid.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.snow.BedrockSnowManager;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;

@EventBusSubscriber(modid = Bedrockoid.MOD_ID)
public class NeoBusEvents {
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Pre event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel))
            return;
        if (!BedrockoidConfig.snowSpawnsUnderTrees || !serverLevel.isRaining() || ModsUtils.isSnowUnderTreesIn())
            return;
        int randomTickSpeed = serverLevel.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
        BedrockSnowManager.runForChunks(serverLevel, chunk -> addSnowUnderTrees(serverLevel, chunk, randomTickSpeed));
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!BedrockoidConfig.snowlogging || !(event.getLevel() instanceof ServerLevel level))
            return;
        BlockState state = event.getState();
        if (!BlockUtils.isSnowlogged(state))
            return;
        Player player = event.getPlayer();
        BlockPos pos = event.getPos();
        BlockState snowState = BlockUtils.getSnowEquivalent(state);
        level.setBlock(pos, state.setValue(BlockUtils.LAYERS, 0), 3);
        level.levelEvent(2001, pos, Block.getId(snowState));
        if (!player.isCreative()) {
            ItemStack tool = player.getMainHandItem();
            ItemStack toolCopy = tool.copy();
            boolean canHarvest = snowState.canHarvestBlock(level, pos, player);
            tool.mineBlock(level, snowState, pos, player);
            if (canHarvest)
                Block.dropResources(snowState, level, pos, null, player, toolCopy);
        }
        event.setCanceled(true);
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
