package net.sashakyotoz.bedrockoid.common.utils;

import com.google.common.base.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class BlockUtils {
    public static final IntegerProperty LAYERS = IntegerProperty.create("snow_layers", 0, 8);

    public static boolean isSnowlogged(@Nullable BlockState state) {
        return state != null
                && state.hasProperty(LAYERS)
                && state.getValue(LAYERS) > 0
                && ModsUtils.isSnowloggingNotOverrided();
    }

    public static BlockState getSnowEquivalent(BlockState state) {
        return Blocks.SNOW.defaultBlockState().setValue(BlockStateProperties.LAYERS, Math.max(1, state.getValue(LAYERS)));
    }

    public static boolean canSnowlog(@Nullable BlockState state) {
        return state != null && state.getProperties() != null
                && state.hasProperty(LAYERS) && state.getFluidState().isEmpty()
                && ModsUtils.isSnowloggingNotOverrided();
    }

    public static boolean isTouchingBlock(Level level, BlockPos pos, Predicate<BlockState> predicate) {
        for (Direction dir : Direction.values()) {
            BlockState state = level.getBlockState(pos.relative(dir));
            if (predicate.test(state)) return true;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean canVinesBeCoveredInSnow(BlockState state, BlockAndTintGetter getter, BlockPos pos) {
        Minecraft minecraft = Minecraft.getInstance();
        if (pos != null && minecraft.level != null) {
            return state.getBlock() instanceof VineBlock && minecraft.level.getBiome(pos) != null
                    && minecraft.level.getBiome(pos).value().getBaseTemperature() < 0.15f
                    && minecraft.level.getBiome(pos).value().hasPrecipitation();
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean haveLeavesToChangeColor(BlockState state, BlockAndTintGetter getter, BlockPos pos) {
        if (getter != null && pos != null) {
            BlockState upperState = getter.getBlockState(pos.above());
            return (state.getBlock() instanceof LeavesBlock && upperState.is(Blocks.SNOW))
                    || (upperState.getBlock() instanceof LeavesBlock && getter.getBlockState(pos.above(2)).is(Blocks.SNOW));
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static int leavesSnowyColor(BlockState state, BlockPos pos) {
        ClientLevel level = Minecraft.getInstance().level;
        if (state.getBlock() instanceof LeavesBlock && level != null && pos != null) {
            BlockState upperState = level.getBlockState(pos.above());
            if (upperState.is(Blocks.SNOW) || (upperState.getBlock() instanceof LeavesBlock && level.getBlockState(pos.above(2)).is(Blocks.SNOW)))
                return BlockUtils.blendColors(BiomeColors.getAverageFoliageColor(level, pos), 0xFFFFFF, 0.95f);
            if (level.getBiome(pos).value().getBaseTemperature() < 0.1f && level.getBiome(pos).value().hasPrecipitation()) {
                return getNeighborBlocks(level, pos) ? BlockUtils.blendColors(BiomeColors.getAverageFoliageColor(level, pos), 0xFFFFFF, 0.4f)
                        : BlockUtils.blendColors(BiomeColors.getAverageFoliageColor(level, pos), 0xFFFFFF, 0.75f);
            }
        }
        return level != null && pos != null ? BiomeColors.getAverageFoliageColor(level, pos) : FoliageColor.getDefaultColor();
    }

    private static boolean getNeighborBlocks(ClientLevel level, BlockPos pos) {
        return level.getBiome(pos.north()).value().getBaseTemperature() > 0.1f
                || level.getBiome(pos.south()).value().getBaseTemperature() > 0.1f
                || level.getBiome(pos.east()).value().getBaseTemperature() > 0.1f
                || level.getBiome(pos.west()).value().getBaseTemperature() > 0.1f;
    }

    public static int blendColors(int colorA, int colorB, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int) ((((colorA >> 16) & 0xFF) * (1 - t)) + (((colorB >> 16) & 0xFF) * t));
        int g = (int) ((((colorA >> 8) & 0xFF) * (1 - t)) + (((colorB >> 8) & 0xFF) * t));
        int b = (int) ((((colorA) & 0xFF) * (1 - t)) + (((colorB) & 0xFF) * t));
        return (r << 16) | (g << 8) | b;
    }

    public static boolean haveToFillUpCauldron(BlockState state, ServerLevel world, BlockPos pos) {
        if (world.getFluidState(pos.above()).is(Fluids.WATER) && state.is(Blocks.WATER_CAULDRON) && state.getValue(BlockStateProperties.LEVEL_CAULDRON) != 3)
            return true;
        else if (world.getFluidState(pos.above()).is(Fluids.WATER) && state.is(Blocks.CAULDRON)) {
            world.setBlock(pos, Blocks.WATER_CAULDRON.defaultBlockState()
                    .setValue(BlockStateProperties.LEVEL_CAULDRON, 1).setValue(BlockStateProperties.WATERLOGGED, state.getValue(BlockStateProperties.WATERLOGGED)), 3);
            return true;
        } else if (world.getFluidState(pos.above()).is(Fluids.LAVA) && state.is(Blocks.CAULDRON) && world.getGameRules().getBoolean(GameRules.RULE_LAVA_SOURCE_CONVERSION)) {
            world.setBlock(pos, Blocks.LAVA_CAULDRON.defaultBlockState(), 3);
            return true;
        }
        return false;
    }

    private static final Class<?>[] WATERLOGGED_MISSING_BLOCKS = {
            LayeredCauldronBlock.class, AnvilBlock.class, GrindstoneBlock.class, StonecutterBlock.class, BellBlock.class,
            LecternBlock.class, HopperBlock.class, BrewingStandBlock.class, PressurePlateBlock.class, FenceGateBlock.class, BedBlock.class
    };

    public static boolean isInstanceOfAny(Block block) {
        for (Class<?> clazz : WATERLOGGED_MISSING_BLOCKS) {
            if (clazz.isInstance(block))
                return true;
        }
        return false;
    }

}