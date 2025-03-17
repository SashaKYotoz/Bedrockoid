package net.sashakyotoz.bedrockoid.events;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;

@EventBusSubscriber(value = Dist.CLIENT,bus = EventBusSubscriber.Bus.MOD)
public class BedrockoidModBusClient {

    @SubscribeEvent
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
}