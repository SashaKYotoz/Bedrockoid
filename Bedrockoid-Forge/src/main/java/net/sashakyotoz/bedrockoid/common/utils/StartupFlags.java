package net.sashakyotoz.bedrockoid.common.utils;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;

import java.nio.file.Files;

public final class StartupFlags {
    public static final boolean SNOWLOGGING;
    public static final boolean WATERLOGGING;

    static {
        boolean snowlogging = true;
        boolean waterlogging = true;
        if (Files.isRegularFile(BedrockoidConfig.CONFIG_PATH)) {
            try (CommentedFileConfig config = CommentedFileConfig.of(BedrockoidConfig.CONFIG_PATH)) {
                config.load();
                Object snow = config.get(BedrockoidConfig.FLAGS + ".bedrockoid.snowlogging");
                Object water = config.get(BedrockoidConfig.FLAGS + ".bedrockoid.blocksWaterloggability");
                if (snow instanceof Boolean value)
                    snowlogging = value;
                if (water instanceof Boolean value)
                    waterlogging = value;
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
}
