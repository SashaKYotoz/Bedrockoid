package net.sashakyotoz.bedrockoid;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BedrockoidConfig {
    public static final Path CONFIG_PATH = Paths.get("config", "bedrockoid-config.toml");
    public static final String FLAGS = "flags";
    public static final String LISTS = "lists";
    public static FileConfig config;

    public static boolean armorStandArms = true;
    public static boolean redstoneConnectsToPiston = true;
    public static boolean snowlogging = true;
    public static boolean blocksWaterloggability = true;
    public static boolean cauldronNaturalFilling = true;
    public static boolean stopElytraByPressingSpace = true;
    public static boolean wetSpongesDryOut = true;
    public static boolean entitySharesFire = true;
    public static boolean fallenTrees = true;
    public static boolean reachAroundPlacement = true;
    public static boolean frostwalkerBoost = true;
    public static boolean mushroomTreesInSwamp = true;
    public static boolean shulkersCanBeDyed = true;
    public static boolean shieldActivatesWhenSneaking = true;
    public static boolean canPlantsBeBonemeal = true;
    public static boolean canSugarcaneBeBonemeal = true;
    public static boolean fireAspectImprovements = true;
    public static boolean composterCollisionFix = true;
    public static boolean snowSpawnsUnderTrees = true;
    public static List<String> disableSnowUnderTreesIn = new ArrayList<>();
    public static boolean snowCoversLeaves = true;
    public static boolean snowCoversVines = true;
    public static boolean sodiumTurnOffSnowloggability = false;

    private static boolean loaded = false;

    public static synchronized void loadConfig() {
        if (loaded)
            return;
        config = CommentedFileConfig.builder(CONFIG_PATH).autoreload().autosave().sync().build();
        config.load();
        addFlag("armorStandArms", true);
        addFlag("redstoneConnectsToPiston", true);
        addFlag("snowlogging", true);
        addFlag("blocksWaterloggability", true);
        addFlag("cauldronNaturalFilling", true);
        addFlag("stopElytraByPressingSpace", true);
        addFlag("wetSpongesDryOut", true);
        addFlag("entitySharesFire", true);
        addFlag("fallenTrees", true);
        addFlag("reachAroundPlacement", true);
        addFlag("frostwalkerBoost", true);
        addFlag("mushroomTreesInSwamp", true);
        addFlag("shulkersCanBeDyed", true);
        addFlag("shieldActivatesWhenSneaking", true);
        addFlag("canPlantsBeBonemeal", true);
        addFlag("canSugarcaneBeBonemeal", true);
        addFlag("fireAspectImprovements", true);
        addFlag("composterCollisionFix", true);
        addFlag("snowSpawnsUnderTrees", true);
        addFlag("snowCoversLeaves", true);
        addFlag("snowCoversVines", true);
        addFlag("sodiumTurnOffSnowloggability", false);
        config.add(LISTS + ".bedrockoid.disableSnowUnderTreesIn", new ArrayList<String>());
        config.save();
        loaded = true;
    }

    public static void init() {
        armorStandArms = flag("armorStandArms", true);
        redstoneConnectsToPiston = flag("redstoneConnectsToPiston", true);
        snowlogging = flag("snowlogging", true);
        blocksWaterloggability = flag("blocksWaterloggability", true);
        cauldronNaturalFilling = flag("cauldronNaturalFilling", true);
        stopElytraByPressingSpace = flag("stopElytraByPressingSpace", true);
        wetSpongesDryOut = flag("wetSpongesDryOut", true);
        entitySharesFire = flag("entitySharesFire", true);
        fallenTrees = flag("fallenTrees", true);
        reachAroundPlacement = flag("reachAroundPlacement", true);
        frostwalkerBoost = flag("frostwalkerBoost", true);
        mushroomTreesInSwamp = flag("mushroomTreesInSwamp", true);
        shulkersCanBeDyed = flag("shulkersCanBeDyed", true);
        shieldActivatesWhenSneaking = flag("shieldActivatesWhenSneaking", true);
        canPlantsBeBonemeal = flag("canPlantsBeBonemeal", true);
        canSugarcaneBeBonemeal = flag("canSugarcaneBeBonemeal", true);
        fireAspectImprovements = flag("fireAspectImprovements", true);
        composterCollisionFix = flag("composterCollisionFix", true);
        snowSpawnsUnderTrees = flag("snowSpawnsUnderTrees", true);
        snowCoversLeaves = flag("snowCoversLeaves", true);
        snowCoversVines = flag("snowCoversVines", true);
        sodiumTurnOffSnowloggability = flag("sodiumTurnOffSnowloggability", false);
        Object biomes = config.get(LISTS + ".bedrockoid.disableSnowUnderTreesIn");
        List<String> parsed = new ArrayList<>();
        if (biomes instanceof List<?> list)
            for (Object entry : list)
                parsed.add(String.valueOf(entry));
        disableSnowUnderTreesIn = parsed;
    }

    private static void addFlag(String name, Object defaultValue) {
        config.add(FLAGS + ".bedrockoid." + name, defaultValue);
    }

    private static boolean flag(String name, boolean defaultValue) {
        Object value = config.get(FLAGS + ".bedrockoid." + name);
        return value instanceof Boolean bool ? bool : defaultValue;
    }
}
