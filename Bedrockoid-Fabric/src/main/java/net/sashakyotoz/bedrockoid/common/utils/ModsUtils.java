package net.sashakyotoz.bedrockoid.common.utils;

import net.fabricmc.loader.api.FabricLoader;

public class ModsUtils {
    public static boolean isSnowRealMagicIn() {
        return FabricLoader.getInstance().isModLoaded("snowrealmagic");
    }

    public static boolean isSnowUnderTreesIn() {
        return FabricLoader.getInstance().isModLoaded("snowundertrees");
    }

    public static boolean isBedrockifyIn() {
        return FabricLoader.getInstance().isModLoaded("bedrockify");
    }

    public static boolean isWilderWildIn() {
        return FabricLoader.getInstance().isModLoaded("wilderwild");
    }

    public static boolean isSnowloggingNotOverrided() {
        return !isSnowRealMagicIn() && !isWilderWildIn();
    }
}