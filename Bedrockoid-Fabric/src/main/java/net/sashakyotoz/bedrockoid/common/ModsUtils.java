package net.sashakyotoz.bedrockoid.common;

import net.fabricmc.loader.api.FabricLoader;

public class ModsUtils {
    public static boolean isSnowRealMagicIn() {
        return FabricLoader.getInstance().isModLoaded("snowrealmagic");
    }
    public static boolean isSnowUnderTreesIn() {
        return FabricLoader.getInstance().isModLoaded("snowundertrees");
    }
    public static boolean isSereneSeasonsIn() {
        return FabricLoader.getInstance().isModLoaded("sereneseasons");
    }
}