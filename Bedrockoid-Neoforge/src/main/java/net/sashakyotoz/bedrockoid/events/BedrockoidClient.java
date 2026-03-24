package net.sashakyotoz.bedrockoid.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
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
        ReachPlacementUtils.INSTANCE.renderIndicator(event.getGuiGraphics());
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(
                (state, level, pos, index) -> {
                    if (BedrockoidConfig.snowCoversLeaves)
                        return BlockUtils.leavesSnowyColor(state, pos);
                    else
                        return level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.FOLIAGE_DEFAULT;
                },
                Blocks.OAK_LEAVES,
                Blocks.SPRUCE_LEAVES,
                Blocks.BIRCH_LEAVES,
                Blocks.JUNGLE_LEAVES,
                Blocks.ACACIA_LEAVES,
                Blocks.DARK_OAK_LEAVES,
                Blocks.MANGROVE_LEAVES,
                Blocks.AZALEA_LEAVES,
                Blocks.FLOWERING_AZALEA_LEAVES
        );
        event.register(
                (state, world, pos, index) -> {
                    if (BlockUtils.isSnowlogged(state) || (world != null && pos != null
                            && BlockUtils.isSnowlogged(world.getBlockState(pos.below()))) && BedrockoidConfig.snowlogging)
                        return 0xCCCCCC;
                    return world != null ? BiomeColors.getAverageGrassColor(world, pos) : GrassColor.getDefaultColor();
                },
                Blocks.GLASS,
                Blocks.TALL_GRASS,
                Blocks.FERN,
                Blocks.LARGE_FERN
        );
        event.register(
                (state, world, pos, index) -> {
                    if (BlockUtils.canVinesBeCoveredInSnow(state, world, pos) && BedrockoidConfig.snowCoversVines)
                        return 0xCCCCCC;
                    return world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.FOLIAGE_DEFAULT;
                },
                Blocks.VINE
        );
    }
}