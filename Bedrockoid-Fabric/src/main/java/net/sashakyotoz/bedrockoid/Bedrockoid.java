package net.sashakyotoz.bedrockoid;

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
        BedrockSnowManager.init();
        BedrockoidFeatures.register();
        ServerTickEvents.START_WORLD_TICK.register(new WorldTickHandler());
        ServerTickEvents.END_WORLD_TICK.register(new WorldTickHandler());
        ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.put(Blocks.GRASS_BLOCK, 0.1f);
    }

    public static Identifier makeID(String id) {
        return new Identifier(MOD_ID, id);
    }

    public static <T> T log(T message) {
        LOGGER.info(String.valueOf(message));
        return message;
    }
}