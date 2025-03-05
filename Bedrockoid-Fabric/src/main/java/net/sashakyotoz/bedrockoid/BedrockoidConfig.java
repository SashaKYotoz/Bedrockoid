package net.sashakyotoz.bedrockoid;

import eu.midnightdust.lib.config.MidnightConfig;

public class BedrockoidConfig extends MidnightConfig {
    public static final String TEXT = "text";
    @Entry(category = TEXT) public static boolean armorStandArms = true;
    @Entry(category = TEXT) public static boolean redstoneConnectsToPiston = true;
    @Entry(category = TEXT) public static boolean snowlogging = true;
    @Entry(category = TEXT) public static boolean cauldronWaterloggability = true;
    @Entry(category = TEXT) public static boolean cauldronNaturalFilling = true;
    @Entry(category = TEXT) public static boolean stopElytraByPressingSpace = true;
    @Entry(category = TEXT) public static boolean wetSpongesDryOut = true;
    @Entry(category = TEXT) public static boolean entitySharesFire = true;
    @Entry(category = TEXT) public static boolean fallenTrees = true;
    @Entry(category = TEXT) public static boolean reachAroundPlacement = true;
    @Entry(category = TEXT) public static boolean sheepFurColorFix = true;
    @Entry(category = TEXT) public static boolean frostwalkerBoost = true;
    @Entry(category = TEXT) public static boolean mushroomTreesInSwamp = true;
    @Entry(category = TEXT) public static boolean shulkersCanBeDyed = true;
    @Entry(category = TEXT) public static boolean shieldActivatesWhenSneaking = true;
    @Entry(category = TEXT) public static boolean canPlantsBeBonemeal = true;
    @Entry(category = TEXT) public static boolean fireAspectImprovements = true;
    @Entry(category = TEXT) public static boolean composterCollisionFix = true;
    @Entry(category = TEXT) public static boolean snowSpawnsUnderTrees = true;
    @Entry(category = TEXT) public static boolean snowCoversLeaves = true;
}