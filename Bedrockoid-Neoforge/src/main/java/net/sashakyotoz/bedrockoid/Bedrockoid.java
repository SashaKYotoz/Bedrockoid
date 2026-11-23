package net.sashakyotoz.bedrockoid;

import com.mojang.logging.LogUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.sashakyotoz.bedrockoid.common.snow.BedrockSnowManager;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import net.sashakyotoz.bedrockoid.common.world.features.BedrockoidFeatures;
import net.sashakyotoz.bedrockoid.common.world.features.BiomeModifiers;
import org.slf4j.Logger;

@Mod(Bedrockoid.MOD_ID)
public class Bedrockoid {

    public static final String MOD_ID = "bedrockoid";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Bedrockoid(IEventBus modEventBus) {
        BedrockoidConfig.loadConfig();
        BedrockoidConfig.init();
        BedrockSnowManager.init();
        BedrockoidFeatures.REGISTRY.register(modEventBus);
        BiomeModifiers.BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
        modEventBus.addListener(Bedrockoid::modifyDefaultComponents);
    }

    private static void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        event.modify(Items.GRASS_BLOCK, (components, context, item) -> components.set(DataComponents.COMPOSTABLE, BlockUtils.GRASS_BLOCK_COMPOSTABLE));
    }

    public static Identifier makeID(String id) {
        return Identifier.fromNamespaceAndPath(MOD_ID, id);
    }

    public static <T> T log(T message) {
        LOGGER.info(String.valueOf(message));
        return message;
    }
}
