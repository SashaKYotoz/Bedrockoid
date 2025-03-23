package net.sashakyotoz.bedrockoid.events;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModBusEvents {
    @SubscribeEvent
    public static void onStarted(FMLCommonSetupEvent event) {
        BedrockoidConfig.init();
    }
}