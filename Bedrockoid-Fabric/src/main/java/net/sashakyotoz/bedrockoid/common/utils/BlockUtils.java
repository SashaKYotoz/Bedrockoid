package net.sashakyotoz.bedrockoid.common.utils;

import com.google.common.base.Predicate;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class BlockUtils {
    public static final IntProperty LAYERS = IntProperty.of("snow_layers", 0, 8);

    private static final Class<?>[] WATERLOGGING_CANDIDATES = {
            AbstractCauldronBlock.class, AnvilBlock.class, GrindstoneBlock.class, StonecutterBlock.class, BellBlock.class,
            LecternBlock.class, HopperBlock.class, BrewingStandBlock.class, AbstractPressurePlateBlock.class, FenceGateBlock.class, BedBlock.class
    };

    public static boolean isSnowlogged(@Nullable BlockState state) {
        return state != null
                && state.contains(LAYERS)
                && state.get(LAYERS) > 0
                && ModsUtils.isSnowloggingNotOverrided();
    }

    public static BlockState getSnowEquivalent(BlockState state) {
        return Blocks.SNOW.getDefaultState().with(Properties.LAYERS, Math.max(1, state.get(LAYERS)));
    }

    public static boolean canSnowlog(@Nullable BlockState state) {
        if (state == null || !state.contains(LAYERS))
            return false;
        if (state.contains(Properties.DOUBLE_BLOCK_HALF) && state.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER)
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
        return hasExtraWaterlogging(state.getBlock()) && state.get(Properties.WATERLOGGED);
    }

    public static boolean isTouchingBlock(World world, BlockPos pos, Predicate<BlockState> predicate) {
        for (Direction dir : Direction.values()) {
            BlockState state = world.getBlockState(pos.offset(dir));
            if (predicate.test(state))
                return true;
        }
        return false;
    }

    public static boolean haveLeavesToChangeColor(BlockState state, BlockView world, BlockPos pos) {
        if (world != null && pos != null) {
            BlockState upperState = world.getBlockState(pos.up());
            return (state.getBlock() instanceof LeavesBlock && upperState.isOf(Blocks.SNOW))
                    || (state.getBlock() instanceof LeavesBlock && upperState.getBlock() instanceof LeavesBlock && world.getBlockState(pos.up(2)).isOf(Blocks.SNOW));
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

    public static void fillCauldronNaturally(BlockState state, ServerWorld world, BlockPos pos) {
        FluidState above = world.getFluidState(pos.up());
        BlockState filled = null;
        if (above.isOf(Fluids.WATER)) {
            if (state.isOf(Blocks.WATER_CAULDRON) && state.get(Properties.LEVEL_3) < 3)
                filled = state.with(Properties.LEVEL_3, state.get(Properties.LEVEL_3) + 1);
            else if (state.isOf(Blocks.CAULDRON))
                filled = Blocks.WATER_CAULDRON.getDefaultState();
        } else if (above.isOf(Fluids.LAVA) && state.isOf(Blocks.CAULDRON) && world.getGameRules().getBoolean(GameRules.LAVA_SOURCE_CONVERSION)) {
            filled = Blocks.LAVA_CAULDRON.getDefaultState();
        }
        if (filled != null) {
            world.setBlockState(pos, filled);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(filled));
        }
    }
}
