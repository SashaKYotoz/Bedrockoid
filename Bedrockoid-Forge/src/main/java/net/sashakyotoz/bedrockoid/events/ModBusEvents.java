package net.sashakyotoz.bedrockoid.events;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBusEvents {
    @SubscribeEvent
    public void onStarted(FMLCommonSetupEvent event) {
        BedrockoidConfig.init();
    }
}