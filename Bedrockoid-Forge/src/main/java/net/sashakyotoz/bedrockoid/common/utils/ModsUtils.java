package net.sashakyotoz.bedrockoid.common.utils;


import net.minecraftforge.fml.ModList;

public class ModsUtils {
    public static boolean isSnowRealMagicIn() {
        return ModList.get() != null && ModList.get().isLoaded("snowrealmagic");
    }

    public static boolean isSnowUnderTreesIn() {
        return ModList.get() != null && ModList.get().isLoaded("snowundertrees");
    }

    public static boolean isBedrockifyIn() {
        return ModList.get() != null && ModList.get().isLoaded("bedrockify");
    }

    public static boolean isWilderWildIn() {
        return ModList.get() != null && ModList.get().isLoaded("wilderwild");
    }

    public static boolean isSnowloggingNotOverrided() {
        return !isSnowRealMagicIn() && !isWilderWildIn();
    }
}