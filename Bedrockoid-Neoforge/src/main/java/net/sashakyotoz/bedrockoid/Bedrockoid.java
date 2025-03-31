package net.sashakyotoz.bedrockoid;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.sashakyotoz.bedrockoid.common.snow.BedrockSnowManager;
import net.sashakyotoz.bedrockoid.common.world.features.BedrockoidFeatures;
import net.sashakyotoz.bedrockoid.common.world.features.BiomeModifiers;
import org.slf4j.Logger;

@Mod(Bedrockoid.MOD_ID)
public class Bedrockoid {

    public static final String MOD_ID = "bedrockoid";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Bedrockoid(IEventBus modEventBus) {
        BedrockoidConfig.loadConfig();
        BedrockSnowManager.init();
        BedrockoidConfig.init();
        BedrockoidFeatures.REGISTRY.register(modEventBus);
        BiomeModifiers.BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
    }

    public static ResourceLocation makeID(String id) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
    }

    public static <T> T log(T message) {
        LOGGER.info(String.valueOf(message));
        return message;
    }
}