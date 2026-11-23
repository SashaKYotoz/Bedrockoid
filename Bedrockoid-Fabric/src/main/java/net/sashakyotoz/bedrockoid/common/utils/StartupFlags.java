package net.sashakyotoz.bedrockoid.common.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public final class StartupFlags {
    public static final boolean SNOWLOGGING;
    public static final boolean WATERLOGGING;

    static {
        boolean snowlogging = true;
        boolean waterlogging = true;
        Path path = FabricLoader.getInstance().getConfigDir().resolve("bedrockoid.json");
        if (Files.isRegularFile(path)) {
            try (Reader reader = Files.newBufferedReader(path)) {
                JsonElement element = JsonParser.parseReader(reader);
                if (element.isJsonObject()) {
                    JsonObject object = element.getAsJsonObject();
                    snowlogging = readFlag(object, "snowlogging", true);
                    waterlogging = readFlag(object, "blocksWaterloggability", true);
                }
            } catch (Exception exception) {
                snowlogging = true;
                waterlogging = true;
            }
        }
        SNOWLOGGING = snowlogging && ModsUtils.isSnowloggingNotOverrided();
        WATERLOGGING = waterlogging;
    }

    private StartupFlags() {
    }

    private static boolean readFlag(JsonObject object, String key, boolean defaultValue) {
        JsonElement value = object.get(key);
        if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean())
            return value.getAsBoolean();
        return defaultValue;
    }
}
