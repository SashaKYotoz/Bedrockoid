package net.sashakyotoz.bedrockoid.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import net.sashakyotoz.bedrockoid.common.utils.ReachPlacementUtils;

public class BedrockoidClient implements ClientModInitializer {
    private int timeFlying = 0;

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> ReachPlacementUtils.INSTANCE.renderIndicator(drawContext));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (BedrockoidConfig.stopElytraByPressingSpace) {
                if (!ModsUtils.isBedrockifyIn() && client.player != null && client.player.isFallFlying() && timeFlying > 10 && client.options.jumpKey.isPressed()) {
                    client.player.stopFallFlying();
                    client.player.networkHandler.sendPacket(new ClientCommandC2SPacket(client.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
                }
                if (!ModsUtils.isBedrockifyIn() && client.player != null && client.player.isFallFlying() && !client.options.jumpKey.isPressed())
                    timeFlying++;
                else
                    timeFlying = 0;
            }
        });
    }
}