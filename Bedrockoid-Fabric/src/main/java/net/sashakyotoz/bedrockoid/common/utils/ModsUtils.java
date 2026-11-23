package net.sashakyotoz.bedrockoid.common.utils;

import net.fabricmc.loader.api.FabricLoader;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;

public final class ModsUtils {
    private ModsUtils() {
    }

    public static boolean isLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static boolean isSnowRealMagicIn() {
        return isLoaded("snowrealmagic");
    }

    public static boolean isSnowUnderTreesIn() {
        return isLoaded("snowundertrees");
    }

    public static boolean isSodiumLoaded() {
        return isLoaded("sodium");
    }

    public static boolean isSodiumIn() {
        return isSodiumLoaded() && BedrockoidConfig.sodiumTurnOffSnowloggability;
    }

    public static boolean isBedrockifyIn() {
        return isLoaded("bedrockify");
    }

    public static boolean isWilderWildIn() {
        return isLoaded("wilderwild");
    }

    public static boolean isSnowloggingNotOverrided() {
        return !isSnowRealMagicIn() && !isWilderWildIn();
    }
}
