package net.sashakyotoz.bedrockoid.events;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import net.sashakyotoz.bedrockoid.common.utils.ReachPlacementUtils;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class BedrockoidClient {
    private static int timeFlying = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft client = Minecraft.getInstance();
        if (BedrockoidConfig.stopElytraByPressingSpace && !ModsUtils.isBedrockifyIn()) {
            if (client.player != null && client.player.isFallFlying() && timeFlying > 10 && client.options.keyJump.isDown()) {
                client.player.getAbilities().flying = false;
                client.player.onUpdateAbilities();
                client.player.connection.send(new ServerboundPlayerCommandPacket(client.player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
            }
            if (client.player != null && client.player.isFallFlying() && !client.options.keyJump.isDown())
                timeFlying++;
            else
                timeFlying = 0;
        }
    }

    @SubscribeEvent
    public static void onOverlayDisplay(RenderGuiOverlayEvent event) {
        ReachPlacementUtils.INSTANCE.renderIndicator(event.getGuiGraphics());
    }
}