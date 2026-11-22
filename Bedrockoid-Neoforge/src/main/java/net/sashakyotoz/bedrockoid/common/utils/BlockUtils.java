package net.sashakyotoz.bedrockoid.common.utils;

import com.google.common.base.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class BlockUtils {
    public static final IntegerProperty LAYERS = IntegerProperty.create("snow_layers", 0, 8);

    private static final Class<?>[] WATERLOGGING_CANDIDATES = {
            AbstractCauldronBlock.class, AnvilBlock.class, GrindstoneBlock.class, StonecutterBlock.class, BellBlock.class,
            LecternBlock.class, HopperBlock.class, BrewingStandBlock.class, BasePressurePlateBlock.class, FenceGateBlock.class, BedBlock.class
    };

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
        if (state == null || !state.hasProperty(LAYERS))
            return false;
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER)
            return false;
        return state.getFluidState().isEmpty() && ModsUtils.isSnowloggingNotOverrided();
    }

    public static boolean isWaterloggingCandidate(Block block) {
        if (block instanceof LavaCauldronBlock)
            return false;
        for (Class<?> clazz : WATERLOGGING_CANDIDATES) {
            if (clazz.isInstance(block))
                return true;
        }
        return false;
    }

    public static boolean hasExtraWaterlogging(Block block) {
        return block instanceof BedrockoidBlock extended && extended.bedrockoid$hasExtraWaterlogging();
    }

    public static boolean isExtraWaterlogged(BlockState state) {
        return hasExtraWaterlogging(state.getBlock()) && state.getValue(BlockStateProperties.WATERLOGGED);
    }

    public static boolean isTouchingBlock(Level level, BlockPos pos, Predicate<BlockState> predicate) {
        for (Direction dir : Direction.values()) {
            BlockState state = level.getBlockState(pos.relative(dir));
            if (predicate.test(state))
                return true;
        }
        return false;
    }

    public static boolean haveLeavesToChangeColor(BlockState state, BlockGetter getter, BlockPos pos) {
        if (getter != null && pos != null) {
            BlockState upperState = getter.getBlockState(pos.above());
            return (state.getBlock() instanceof LeavesBlock && upperState.is(Blocks.SNOW))
                    || (state.getBlock() instanceof LeavesBlock && upperState.getBlock() instanceof LeavesBlock && getter.getBlockState(pos.above(2)).is(Blocks.SNOW));
        }
        return false;
    }

    public static int blendColors(int colorA, int colorB, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int) ((((colorA >> 16) & 0xFF) * (1 - t)) + (((colorB >> 16) & 0xFF) * t));
        int g = (int) ((((colorA >> 8) & 0xFF) * (1 - t)) + (((colorB >> 8) & 0xFF) * t));
        int b = (int) ((((colorA) & 0xFF) * (1 - t)) + (((colorB) & 0xFF) * t));
        return (r << 16) | (g << 8) | b;
    }

    public static void fillCauldronNaturally(BlockState state, ServerLevel level, BlockPos pos) {
        FluidState above = level.getFluidState(pos.above());
        BlockState filled = null;
        if (above.is(Fluids.WATER)) {
            if (state.is(Blocks.WATER_CAULDRON) && state.getValue(BlockStateProperties.LEVEL_CAULDRON) < 3)
                filled = state.setValue(BlockStateProperties.LEVEL_CAULDRON, state.getValue(BlockStateProperties.LEVEL_CAULDRON) + 1);
            else if (state.is(Blocks.CAULDRON))
                filled = Blocks.WATER_CAULDRON.defaultBlockState();
        } else if (above.is(Fluids.LAVA) && state.is(Blocks.CAULDRON) && level.getGameRules().getBoolean(GameRules.RULE_LAVA_SOURCE_CONVERSION)) {
            filled = Blocks.LAVA_CAULDRON.defaultBlockState();
        }
        if (filled != null) {
            level.setBlockAndUpdate(pos, filled);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(filled));
        }
    }
}