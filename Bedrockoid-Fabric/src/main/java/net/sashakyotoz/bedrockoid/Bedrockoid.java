package net.sashakyotoz.bedrockoid;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.block.ComposterBlock;
import net.minecraft.util.Identifier;
import net.sashakyotoz.bedrockoid.common.events.WorldTickHandler;
import net.sashakyotoz.bedrockoid.common.snow.BedrockSnowManager;
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
        WorldTickHandler instance = new WorldTickHandler();
        ServerTickEvents.START_WORLD_TICK.register(instance);
        ServerTickEvents.END_WORLD_TICK.register(instance);
    }

    public static Identifier makeID(String id) {
        return new Identifier(MOD_ID, id);
    }

    public static <T> T log(T message) {
        LOGGER.info(String.valueOf(message));
        return message;
    }
}