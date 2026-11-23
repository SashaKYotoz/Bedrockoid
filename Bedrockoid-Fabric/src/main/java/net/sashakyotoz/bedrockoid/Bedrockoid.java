package net.sashakyotoz.bedrockoid;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.sashakyotoz.bedrockoid.common.handlers.SnowBreakHandler;
import net.sashakyotoz.bedrockoid.common.handlers.WorldTickHandler;
import net.sashakyotoz.bedrockoid.common.snow.BedrockSnowManager;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import net.sashakyotoz.bedrockoid.common.world.features.BedrockoidFeatures;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bedrockoid implements ModInitializer {
    public static final String MOD_ID = "bedrockoid";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        MidnightConfig.init(MOD_ID, BedrockoidConfig.class);
        BedrockSnowManager.init();
        BedrockoidFeatures.register();
        ServerTickEvents.START_LEVEL_TICK.register(WorldTickHandler::onStartTick);
        PlayerBlockBreakEvents.BEFORE.register(SnowBreakHandler::beforeBlockBreak);
        DefaultItemComponentEvents.MODIFY.register(context -> context.modify(Items.GRASS_BLOCK, builder -> builder.set(DataComponents.COMPOSTABLE, BlockUtils.GRASS_BLOCK_COMPOSTABLE)));
    }

    public static Identifier makeID(String id) {
        return Identifier.fromNamespaceAndPath(MOD_ID, id);
    }

    public static <T> T log(T message) {
        LOGGER.info(String.valueOf(message));
        return message;
    }
}
