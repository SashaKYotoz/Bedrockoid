package net.sashakyotoz.bedrockoid.events;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import net.sashakyotoz.bedrockoid.client.SnowColors;

@EventBusSubscriber(modid = Bedrockoid.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class BedrockoidModBusClient {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, index) -> SnowColors.leaves(state, level, pos, SnowColors.averageFoliage(level, pos)),
                Blocks.OAK_LEAVES,
                Blocks.JUNGLE_LEAVES,
                Blocks.ACACIA_LEAVES,
                Blocks.DARK_OAK_LEAVES);
        event.register((state, level, pos, index) -> SnowColors.leaves(state, level, pos, FoliageColor.getEvergreenColor()),
                Blocks.SPRUCE_LEAVES);
        event.register((state, level, pos, index) -> SnowColors.leaves(state, level, pos, FoliageColor.getBirchColor()),
                Blocks.BIRCH_LEAVES);
        event.register((state, level, pos, index) -> SnowColors.leaves(state, level, pos, FoliageColor.getMangroveColor()),
                Blocks.MANGROVE_LEAVES);
        event.register((state, level, pos, index) -> SnowColors.plant(state, level, pos, SnowColors.averageGrass(level, pos, GrassColor.getDefaultColor())),
                Blocks.SHORT_GRASS,
                Blocks.FERN);
        event.register((state, level, pos, index) -> SnowColors.plant(state, level, pos, SnowColors.averageGrass(level, SnowColors.lowerHalf(state, pos), -1)),
                Blocks.TALL_GRASS,
                Blocks.LARGE_FERN);
        event.register((state, level, pos, index) -> SnowColors.plant(state, level, pos, level != null && pos != null ? BiomeColors.getAverageGrassColor(level, pos) : -1),
                Blocks.SUGAR_CANE);
        event.register((state, level, pos, index) -> SnowColors.vine(state, level, pos), Blocks.VINE);
    }
}
