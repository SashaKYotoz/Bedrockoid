package net.sashakyotoz.bedrockoid.events;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BedrockoidModBusClient {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(
                (state, level, pos, index) -> {
                    if (BedrockoidConfig.snowCoversLeaves)
                        return BlockUtils.leavesSnowyColor(state, pos);
                    else
                        return level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.getDefaultColor();
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
                Blocks.GRASS,
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