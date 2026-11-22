package net.sashakyotoz.bedrockoid.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import net.sashakyotoz.bedrockoid.common.utils.ReachPlacementUtils;

@Mod.EventBusSubscriber(modid = Bedrockoid.MOD_ID, value = Dist.CLIENT)
public class BedrockoidClient {
    private static final int MIN_FLIGHT_TICKS = 10;
    private static int timeFlying = 0;
    private static boolean jumpWasDown = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        boolean jumpDown = client.options.keyJump.isDown();
        if (player == null || !BedrockoidConfig.stopElytraByPressingSpace || ModsUtils.isBedrockifyIn() || !player.isFallFlying()) {
            timeFlying = 0;
            jumpWasDown = jumpDown;
            return;
        }
        timeFlying++;
        if (jumpDown && !jumpWasDown && timeFlying > MIN_FLIGHT_TICKS) {
            player.stopFallFlying();
            player.connection.send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
            timeFlying = 0;
        }
        jumpWasDown = jumpDown;
    }

    @SubscribeEvent
    public static void onOverlayDisplay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay().id().equals(VanillaGuiOverlay.CROSSHAIR.id()))
            ReachPlacementUtils.INSTANCE.renderIndicator(event.getGuiGraphics());
    }
}