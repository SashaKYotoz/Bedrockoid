package net.sashakyotoz.bedrockoid.common.handlers;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.jetbrains.annotations.Nullable;

public final class SnowBreakHandler {
    private SnowBreakHandler() {
    }

    public static boolean beforeBlockBreak(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        if (!BedrockoidConfig.snowlogging || !BlockUtils.isSnowlogged(state) || !(level instanceof ServerLevel serverLevel))
            return true;
        BlockState snowState = BlockUtils.getSnowEquivalent(state);
        serverLevel.setBlock(pos, state.setValue(BlockUtils.LAYERS, 0), 3);
        serverLevel.levelEvent(2001, pos, Block.getId(snowState));
        if (!player.isCreative()) {
            ItemStack tool = player.getMainHandItem();
            ItemStack toolCopy = tool.copy();
            boolean canHarvest = player.hasCorrectToolForDrops(snowState);
            tool.mineBlock(serverLevel, snowState, pos, player);
            if (canHarvest)
                Block.dropResources(snowState, serverLevel, pos, null, player, toolCopy);
        }
        return false;
    }
}
