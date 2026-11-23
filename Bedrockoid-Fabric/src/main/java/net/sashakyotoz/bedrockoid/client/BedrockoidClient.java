package net.sashakyotoz.bedrockoid.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.entity.Pose;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import net.sashakyotoz.bedrockoid.common.utils.ReachPlacementUtils;

public class BedrockoidClient implements ClientModInitializer {
    private static final int MIN_FLIGHT_TICKS = 10;
    private int timeFlying = 0;
    private boolean jumpWasDown = false;

    @Override
    public void onInitializeClient() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.CROSSHAIR, Bedrockoid.makeID("reach_around_indicator"),
                (graphics, deltaTracker) -> ReachPlacementUtils.INSTANCE.renderIndicator(graphics));
        ClientTickEvents.END_CLIENT_TICK.register(this::handleElytra);
    }

    private void handleElytra(Minecraft client) {
        LocalPlayer player = client.player;
        boolean jumpDown = client.options.keyJump.isDown();
        if (player == null || !BedrockoidConfig.stopElytraByPressingSpace || ModsUtils.isBedrockifyIn() || player.getPose() != Pose.FALL_FLYING) {
            this.timeFlying = 0;
            this.jumpWasDown = jumpDown;
            return;
        }
        this.timeFlying++;
        if (jumpDown && !this.jumpWasDown && this.timeFlying > MIN_FLIGHT_TICKS) {
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
            player.connection.send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
            this.timeFlying = 0;
        }
        this.jumpWasDown = jumpDown;
    }
}