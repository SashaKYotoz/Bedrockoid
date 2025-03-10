package net.sashakyotoz.bedrockoid.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import net.sashakyotoz.bedrockoid.common.utils.ReachPlacementUtils;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class BedrockoidClient {
    private static int timeFlying = 0;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(
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
                        return world != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.getDefaultColor();
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
        event.register(
                (state, world, pos, index) -> {
                    if (BlockUtils.isSnowlogged(state) && BedrockoidConfig.snowlogging)
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
                    return world != null && pos != null ? BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.getDefaultColor();
                },
                Blocks.VINE
        );
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
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
    public static void onOverlayDisplay(RenderGuiOverlayEvent event) {
        ReachPlacementUtils.INSTANCE.renderIndicator(event.getGuiGraphics());
    }
}