package net.sashakyotoz.bedrockoid.common.utils;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
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
        ModList modList = ModList.get();
        if (modList != null) {
            boolean loaded = modList.isLoaded(modId);
            LOADED.put(modId, loaded);
            return loaded;
        }
        return isInLoadingList(modId);
    }

    private static boolean isInLoadingList(String modId) {
        try {
            return FMLLoader.getCurrent().getLoadingModList().getModFiles().stream()
                    .flatMap(file -> file.getMods().stream())
                    .anyMatch(mod -> modId.equals(mod.getModId()));
        } catch (RuntimeException exception) {
            return false;
        }
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
