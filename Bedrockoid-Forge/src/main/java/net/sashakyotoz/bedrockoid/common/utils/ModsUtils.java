package net.sashakyotoz.bedrockoid.common.utils;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ModsUtils {
    private static final Map<String, Boolean> LOADED = new ConcurrentHashMap<>();

    private ModsUtils() {
    }

    public static boolean isLoaded(String modId) {
        Boolean cached = LOADED.get(modId);
        if (cached != null)
            return cached;
        boolean loaded;
        ModList modList = ModList.get();
        if (modList != null) {
            loaded = modList.isLoaded(modId);
        } else {
            LoadingModList loadingModList = LoadingModList.get();
            loaded = loadingModList != null && loadingModList.getModFileById(modId) != null;
        }
        LOADED.put(modId, loaded);
        return loaded;
    }

    public static boolean isSnowRealMagicIn() {
        return isLoaded("snowrealmagic");
    }

    public static boolean isSnowUnderTreesIn() {
        return isLoaded("snowundertrees");
    }

    public static boolean isComfortsIn() {
        return isLoaded("comforts");
    }

    public static boolean isSodiumLoaded() {
        return isLoaded("sodium") || isLoaded("embeddium") || isLoaded("rubidium");
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
