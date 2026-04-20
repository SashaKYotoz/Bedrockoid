package net.sashakyotoz.bedrockoid.events;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import net.sashakyotoz.bedrockoid.common.utils.ReachPlacementUtils;

@EventBusSubscriber(value = Dist.CLIENT)
public class BedrockoidClient {
    private static int timeFlying = 0;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        if (BedrockoidConfig.stopElytraByPressingSpace) {
            if (!ModsUtils.isBedrockifyIn() && client.player != null && client.player.isFallFlying() && timeFlying > 10 && client.options.keyJump.isDown()) {
                client.player.stopFallFlying();
                client.player.connection.send(new ServerboundPlayerCommandPacket(client.player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
            }
            if (!ModsUtils.isBedrockifyIn() && client.player != null && client.player.isFallFlying() && !client.options.keyJump.isDown())
                timeFlying++;
            else
                timeFlying = 0;
        }
    }

    @SubscribeEvent
    public static void onOverlayDisplay(RenderGuiLayerEvent.Post event) {
        if (BedrockoidConfig.reachAroundPlacement)
            ReachPlacementUtils.INSTANCE.renderIndicator(event.getGuiGraphics());
    }
}