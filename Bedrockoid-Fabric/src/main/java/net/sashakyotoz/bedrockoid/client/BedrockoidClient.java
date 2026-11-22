package net.sashakyotoz.bedrockoid.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.world.biome.FoliageColors;
import net.minecraft.world.biome.GrassColors;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import net.sashakyotoz.bedrockoid.common.utils.ReachPlacementUtils;

public class BedrockoidClient implements ClientModInitializer {
    private static final int MIN_FLIGHT_TICKS = 10;
    private int timeFlying = 0;
    private boolean jumpWasPressed = false;

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> ReachPlacementUtils.INSTANCE.renderIndicator(drawContext));
        ClientTickEvents.END_CLIENT_TICK.register(this::handleElytra);
        registerColors();
    }

    private void handleElytra(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        boolean jumpPressed = client.options.jumpKey.isPressed();
        if (player == null || !BedrockoidConfig.stopElytraByPressingSpace || ModsUtils.isBedrockifyIn() || !player.isFallFlying()) {
            timeFlying = 0;
            jumpWasPressed = jumpPressed;
            return;
        }
        timeFlying++;
        if (jumpPressed && !jumpWasPressed && timeFlying > MIN_FLIGHT_TICKS) {
            player.stopFallFlying();
            player.networkHandler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            timeFlying = 0;
        }
        jumpWasPressed = jumpPressed;
    }

    private static void registerColors() {
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> SnowColors.leaves(state, world, pos, SnowColors.averageFoliage(world, pos)),
                Blocks.OAK_LEAVES,
                Blocks.JUNGLE_LEAVES,
                Blocks.ACACIA_LEAVES,
                Blocks.DARK_OAK_LEAVES);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> SnowColors.leaves(state, world, pos, FoliageColors.getSpruceColor()),
                Blocks.SPRUCE_LEAVES);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> SnowColors.leaves(state, world, pos, FoliageColors.getBirchColor()),
                Blocks.BIRCH_LEAVES);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> SnowColors.leaves(state, world, pos, FoliageColors.getMangroveColor()),
                Blocks.MANGROVE_LEAVES);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> SnowColors.plant(state, world, pos, SnowColors.averageGrass(world, pos, GrassColors.getDefaultColor())),
                Blocks.SHORT_GRASS,
                Blocks.FERN);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> SnowColors.plant(state, world, pos, SnowColors.averageGrass(world, SnowColors.lowerHalf(state, pos), -1)),
                Blocks.TALL_GRASS,
                Blocks.LARGE_FERN);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> SnowColors.plant(state, world, pos, world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : -1),
                Blocks.SUGAR_CANE);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> SnowColors.vine(state, world, pos),
                Blocks.VINE);
    }
}
