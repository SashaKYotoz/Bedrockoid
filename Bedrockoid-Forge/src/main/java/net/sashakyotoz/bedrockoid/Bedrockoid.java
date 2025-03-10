package net.sashakyotoz.bedrockoid;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.sashakyotoz.bedrockoid.common.snow.BedrockSnowManager;
import net.sashakyotoz.bedrockoid.common.world.features.BedrockoidFeatures;
import net.sashakyotoz.bedrockoid.common.world.features.BiomeModifiers;
import org.slf4j.Logger;

@Mod(Bedrockoid.MOD_ID)
public class Bedrockoid {

    public static final String MOD_ID = "bedrockoid";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Bedrockoid() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        BedrockoidConfig.loadConfig();
        BedrockSnowManager.init();

        BedrockoidFeatures.REGISTRY.register(modEventBus);
        BiomeModifiers.BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
    }
    public static ResourceLocation makeID(String id) {
        return new ResourceLocation(MOD_ID, id);
    }

    public static <T> T log(T message) {
        LOGGER.info(String.valueOf(message));
        return message;
    }
}