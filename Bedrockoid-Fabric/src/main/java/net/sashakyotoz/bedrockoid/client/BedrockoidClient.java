package net.sashakyotoz.bedrockoid.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.sashakyotoz.bedrockoid.client.events.WorldRenderEventHandler;
import net.sashakyotoz.bedrockoid.common.utils.ReachPlacementUtils;

public class BedrockoidClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        WorldRenderEvents.END.register(new WorldRenderEventHandler());
        WorldRenderEvents.BLOCK_OUTLINE.register(new WorldRenderEventHandler());
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> ReachPlacementUtils.INSTANCE.renderIndicator(drawContext));
    }
}