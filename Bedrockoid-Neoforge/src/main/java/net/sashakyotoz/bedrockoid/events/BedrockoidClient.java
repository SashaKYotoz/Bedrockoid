package net.sashakyotoz.bedrockoid.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.entity.Pose;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.client.SnowTints;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import net.sashakyotoz.bedrockoid.common.utils.ReachPlacementUtils;

@EventBusSubscriber(modid = Bedrockoid.MOD_ID, value = Dist.CLIENT)
public class BedrockoidClient {
    private static final int MIN_FLIGHT_TICKS = 10;
    private static int timeFlying = 0;
    private static boolean jumpWasDown = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        boolean jumpDown = client.options.keyJump.isDown();
        if (player == null || !BedrockoidConfig.stopElytraByPressingSpace || ModsUtils.isBedrockifyIn() || player.getPose() != Pose.FALL_FLYING) {
            timeFlying = 0;
            jumpWasDown = jumpDown;
            return;
        }
        timeFlying++;
        if (jumpDown && !jumpWasDown && timeFlying > MIN_FLIGHT_TICKS) {
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
            player.connection.send(new ServerboundPlayerCommandPacket(player, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
            timeFlying = 0;
        }
        jumpWasDown = jumpDown;
    }

    @SubscribeEvent
    public static void onOverlayDisplay(RenderGuiLayerEvent.Post event) {
        if (event.getName().equals(VanillaGuiLayers.CROSSHAIR))
            ReachPlacementUtils.INSTANCE.renderIndicator(event.getGuiGraphics());
    }

    @SubscribeEvent
    public static void registerBlockTints(RegisterColorHandlersEvent.BlockTintSources event) {
        SnowTints.apply(event.getBlockColors(), (sources, block) -> event.register(sources, block));
    }
}
