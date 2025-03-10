package net.sashakyotoz.bedrockoid;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import org.apache.commons.compress.utils.Lists;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BedrockoidConfig {
    public static FileConfig config;
    public static final String FLAGS = "flags";
    public static final String LISTS = "lists";

    public static boolean armorStandArms = true;
    public static boolean redstoneConnectsToPiston = true;
    public static boolean snowlogging = true;
    public static boolean cauldronWaterloggability = true;
    public static boolean cauldronNaturalFilling = true;
    public static boolean stopElytraByPressingSpace = true;
    public static boolean wetSpongesDryOut = true;
    public static boolean entitySharesFire = true;
    public static boolean fallenTrees = true;
    public static boolean reachAroundPlacement = true;
    public static boolean sheepFurColorFix = true;
    public static boolean frostwalkerBoost = true;
    public static boolean shulkersCanBeDyed = true;
    public static boolean shieldActivatesWhenSneaking = true;
    public static boolean canPlantsBeBonemeal = true;
    public static boolean fireAspectImprovements = true;
    public static boolean composterCollisionFix = true;
    public static boolean snowSpawnsUnderTrees = true;
    public static List<String> disableSnowUnderTreesIn = Lists.newArrayList();
    public static boolean snowCoversLeaves = true;
    public static boolean snowCoversVines = true;

    public static void loadConfig() {
        Path configPath = Paths.get("config/bedrockoid-config.toml");
        config = CommentedFileConfig.builder(configPath).autoreload().autosave().sync().build();
        config.load();
        config.add(FLAGS + ".bedrockoid.armorStandArms", true);
        config.add(FLAGS + ".bedrockoid.redstoneConnectsToPiston", true);
        config.add(FLAGS + ".bedrockoid.snowlogging", true);
        config.add(FLAGS + ".bedrockoid.cauldronWaterloggability", true);
        config.add(FLAGS + ".bedrockoid.cauldronNaturalFilling", true);
        config.add(FLAGS + ".bedrockoid.stopElytraByPressingSpace", true);
        config.add(FLAGS + ".bedrockoid.wetSpongesDryOut", true);
        config.add(FLAGS + ".bedrockoid.entitySharesFire", true);
        config.add(FLAGS + ".bedrockoid.fallenTrees", true);
        config.add(FLAGS + ".bedrockoid.reachAroundPlacement", true);
        config.add(FLAGS + ".bedrockoid.sheepFurColorFix", true);
        config.add(FLAGS + ".bedrockoid.frostwalkerBoost", true);
        config.add(FLAGS + ".bedrockoid.shulkersCanBeDyed", true);
        config.add(FLAGS + ".bedrockoid.shieldActivatesWhenSneaking", true);
        config.add(FLAGS + ".bedrockoid.canPlantsBeBonemeal", true);
        config.add(FLAGS + ".bedrockoid.fireAspectImprovements", true);
        config.add(FLAGS + ".bedrockoid.composterCollisionFix", true);
        config.add(FLAGS + ".bedrockoid.snowSpawnsUnderTrees", true);
        config.add(FLAGS + ".bedrockoid.snowCoversLeaves", true);
        config.add(FLAGS + ".bedrockoid.snowCoversVines", true);
        config.add(LISTS + ".bedrockoid.disableSnowUnderTreesIn", Lists.newArrayList());
        config.save();
    }

    public static void init() {
        armorStandArms = config.get(FLAGS + ".bedrockoid.armorStandArms");
        redstoneConnectsToPiston = config.get(FLAGS + ".bedrockoid.redstoneConnectsToPiston");
        snowlogging = config.get(FLAGS + ".bedrockoid.snowlogging");
        cauldronWaterloggability = config.get(FLAGS + ".bedrockoid.cauldronWaterloggability");
        cauldronNaturalFilling = config.get(FLAGS + ".bedrockoid.cauldronNaturalFilling");
        stopElytraByPressingSpace = config.get(FLAGS + ".bedrockoid.stopElytraByPressingSpace");
        wetSpongesDryOut = config.get(FLAGS + ".bedrockoid.wetSpongesDryOut");
        entitySharesFire = config.get(FLAGS + ".bedrockoid.entitySharesFire");
        fallenTrees = config.get(FLAGS + ".bedrockoid.fallenTrees");
        reachAroundPlacement = config.get(FLAGS + ".bedrockoid.reachAroundPlacement");
        sheepFurColorFix = config.get(FLAGS + ".bedrockoid.sheepFurColorFix");
        frostwalkerBoost = config.get(FLAGS + ".bedrockoid.frostwalkerBoost");
        shulkersCanBeDyed = config.get(FLAGS + ".bedrockoid.shulkersCanBeDyed");
        shieldActivatesWhenSneaking = config.get(FLAGS + ".bedrockoid.shieldActivatesWhenSneaking");
        canPlantsBeBonemeal = config.get(FLAGS + ".bedrockoid.canPlantsBeBonemeal");
        fireAspectImprovements = config.get(FLAGS + ".bedrockoid.fireAspectImprovements");
        composterCollisionFix = config.get(FLAGS + ".bedrockoid.composterCollisionFix");
        snowSpawnsUnderTrees = config.get(FLAGS + ".bedrockoid.snowSpawnsUnderTrees");
        snowCoversLeaves = config.get(FLAGS + ".bedrockoid.snowCoversLeaves");
        snowCoversVines = config.get(FLAGS + ".bedrockoid.snowCoversVines");
        disableSnowUnderTreesIn = config.get(LISTS + ".bedrockoid.disableSnowUnderTreesIn");
    }
}