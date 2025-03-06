package net.sashakyotoz.bedrockoid;

import eu.midnightdust.lib.config.MidnightConfig;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class BedrockoidConfig extends MidnightConfig {
    public static final String FLAGS = "flags";
    public static final String LISTS = "lists";
    @Entry(category = FLAGS) public static boolean armorStandArms = true;
    @Entry(category = FLAGS) public static boolean redstoneConnectsToPiston = true;
    @Entry(category = FLAGS) public static boolean snowlogging = true;
    @Entry(category = FLAGS) public static boolean cauldronWaterloggability = true;
    @Entry(category = FLAGS) public static boolean cauldronNaturalFilling = true;
    @Entry(category = FLAGS) public static boolean stopElytraByPressingSpace = true;
    @Entry(category = FLAGS) public static boolean wetSpongesDryOut = true;
    @Entry(category = FLAGS) public static boolean entitySharesFire = true;
    @Entry(category = FLAGS) public static boolean fallenTrees = true;
    @Entry(category = FLAGS) public static boolean reachAroundPlacement = true;
    @Entry(category = FLAGS) public static boolean sheepFurColorFix = true;
    @Entry(category = FLAGS) public static boolean frostwalkerBoost = true;
    @Entry(category = FLAGS) public static boolean mushroomTreesInSwamp = true;
    @Entry(category = FLAGS) public static boolean shulkersCanBeDyed = true;
    @Entry(category = FLAGS) public static boolean shieldActivatesWhenSneaking = true;
    @Entry(category = FLAGS) public static boolean canPlantsBeBonemeal = true;
    @Entry(category = FLAGS) public static boolean fireAspectImprovements = true;
    @Entry(category = FLAGS) public static boolean composterCollisionFix = true;
    @Entry(category = FLAGS) public static boolean snowSpawnsUnderTrees = true;
    @Entry(category = LISTS) public static List<String> disableSnowUnderTreesIn = Lists.newArrayList();
    @Entry(category = FLAGS) public static boolean snowCoversLeaves = true;
    @Entry(category = FLAGS) public static boolean snowCoversVines = true;
}