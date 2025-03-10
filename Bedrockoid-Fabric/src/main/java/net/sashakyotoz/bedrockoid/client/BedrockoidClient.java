package net.sashakyotoz.bedrockoid.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.world.biome.FoliageColors;
import net.minecraft.world.biome.GrassColors;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
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
        if (ModsUtils.isSodiumIn()) {
            ColorProviderRegistry.BLOCK.register(
                    (state, world, pos, index) -> {
                        if (BlockUtils.haveLeavesToChangeColor(state, world, pos)
                                || BlockUtils.haveLeavesToSlightlyChangeColor(state, world, pos)) {
                            int colour = 0;
                            if (BlockUtils.haveLeavesToChangeColor(state, world, pos))
                                colour = 0xFFFFFF;
                            if (BlockUtils.haveLeavesToSlightlyChangeColor(state, world, pos))
                                colour = 0xCCCCCC;
                            return colour;
                        } else
                            return world != null ? BiomeColors.getFoliageColor(world, pos) : FoliageColors.getDefaultColor();
                    },
                    Blocks.OAK_LEAVES,
                    Blocks.SPRUCE_LEAVES,
                    Blocks.BIRCH_LEAVES,
                    Blocks.JUNGLE_LEAVES,
                    Blocks.CHERRY_LEAVES,
                    Blocks.ACACIA_LEAVES,
                    Blocks.DARK_OAK_LEAVES,
                    Blocks.MANGROVE_LEAVES,
                    Blocks.AZALEA_LEAVES,
                    Blocks.FLOWERING_AZALEA_LEAVES
            );
            ColorProviderRegistry.BLOCK.register(
                    (state, world, pos, index) -> {
                        if (BlockUtils.isSnowlogged(state) && BedrockoidConfig.snowlogging)
                            return 0xCCCCCC;
                        return world != null ? BiomeColors.getGrassColor(world, pos) : GrassColors.getDefaultColor();
                    },
                    Blocks.GLASS,
                    Blocks.TALL_GRASS,
                    Blocks.FERN,
                    Blocks.LARGE_FERN
            );
            ColorProviderRegistry.BLOCK.register(
                    (state, world, pos, index) -> {
                        if (BlockUtils.canVinesBeCoveredInSnow(state, world, pos) && BedrockoidConfig.snowCoversVines)
                            return 0xCCCCCC;
                        return world != null ? BiomeColors.getFoliageColor(world, pos) : FoliageColors.getDefaultColor();
                    },
                    Blocks.VINE
            );
        }
    }
}