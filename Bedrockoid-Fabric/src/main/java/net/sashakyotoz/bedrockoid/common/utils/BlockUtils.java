package net.sashakyotoz.bedrockoid.common.utils;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.VineBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

public class BlockUtils {
    public static final IntProperty LAYERS = IntProperty.of("snow_layers", 0, 8);

    //frozenblock team copyright
    //stating changes: use of yarn mappings / extra boolean conditions
    //source code: https://github.com/FrozenBlock/WilderWild/blob/master/src/main/java/net/frozenblock/wilderwild/block/impl/SnowloggingUtils.java
    public static boolean isSnowlogged(@Nullable BlockState state) {
        return state != null
                && state.getProperties() != null
                && state.contains(LAYERS)
                && state.get(LAYERS) > 0
                && ModsUtils.isSnowloggingNotOverrided();
    }

    public static BlockState getSnowEquivalent(BlockState state) {
        return Blocks.SNOW.getDefaultState().with(Properties.LAYERS, Math.max(1, state.get(LAYERS)));
    }

    public static boolean canSnowlog(@Nullable BlockState state) {
        if (state != null && state.contains(Properties.DOUBLE_BLOCK_HALF) && state.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER)
            return false;
        return state != null && state.getProperties() != null
                && state.contains(LAYERS) && state.getFluidState().isEmpty()
                && ModsUtils.isSnowloggingNotOverrided();
    }
    //

    public static boolean canVinesBeCoveredInSnow(BlockState state, BlockRenderView world, BlockPos pos) {
        if (world != null && pos != null) {
            return state.getBlock() instanceof VineBlock && world.getBiomeFabric(pos) != null
                    && world.getBiomeFabric(pos).value().getTemperature() < 0.15f
                    && world.getBiomeFabric(pos).value().hasPrecipitation();
        }
        return false;
    }

    public static boolean haveLeavesToChangeColor(BlockState state, BlockRenderView world, BlockPos pos) {
        if (world != null && pos != null) {
            BlockState upperState = world.getBlockState(pos.up());
            return (state.getBlock() instanceof LeavesBlock && upperState.isOf(Blocks.SNOW))
                    || (upperState.getBlock() instanceof LeavesBlock && world.getBlockState(pos.up(2)).isOf(Blocks.SNOW));
        }
        return false;
    }

    public static boolean haveLeavesToSlightlyChangeColor(BlockState state, BlockRenderView world, BlockPos pos) {
        if (world != null && pos != null)
            return state.getBlock() instanceof LeavesBlock && world.getBiomeFabric(pos) != null
                    && world.getBiomeFabric(pos).value().getTemperature() < 0.15f
                    && world.getBiomeFabric(pos).value().hasPrecipitation();
        return false;
    }

    public static boolean haveToFillUpCauldron(BlockState state, ServerWorld world, BlockPos pos) {
        if (world.getFluidState(pos.up()).isOf(Fluids.WATER) && state.isOf(Blocks.WATER_CAULDRON) && state.get(Properties.LEVEL_3) != 3)
            return true;
        else if (world.getFluidState(pos.up()).isOf(Fluids.WATER) && state.isOf(Blocks.CAULDRON)) {
            world.setBlockState(pos, Blocks.WATER_CAULDRON.getDefaultState().with(Properties.LEVEL_3, 1).with(Properties.WATERLOGGED, state.get(Properties.WATERLOGGED)));
            return true;
        } else if (world.getFluidState(pos.up()).isOf(Fluids.LAVA) && state.isOf(Blocks.CAULDRON) && world.getGameRules().getBoolean(GameRules.LAVA_SOURCE_CONVERSION)) {
            world.setBlockState(pos, Blocks.LAVA_CAULDRON.getDefaultState());
            return true;
        }
        return false;
    }
}