package net.sashakyotoz.bedrockoid.client.events;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;

public class WorldRenderEventHandler implements WorldRenderEvents.End,WorldRenderEvents.BlockOutline{
    @Override
    public void onEnd(WorldRenderContext context) {
        if (BlockUtils.matrices == null)
            BlockUtils.matrices = context.matrixStack();
    }

    @Override
    public boolean onBlockOutline(WorldRenderContext worldRenderContext, WorldRenderContext.BlockOutlineContext blockOutlineContext) {
        if (blockOutlineContext.blockState().getProperties().contains(BlockUtils.LAYERS))
            BlockUtils.renderSnowOverlay(blockOutlineContext.blockPos(), worldRenderContext.world(), blockOutlineContext.blockState().get(BlockUtils.LAYERS));
        return false;
    }
}
