package net.sashakyotoz.bedrockoid.common.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.VineBlock;
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

    public static BlockState getSnowPlacementState(BlockState state, BlockPlaceContext context) {
        return getSnowloggedState(state, context.getLevel().getBlockState(context.getClickedPos()));
    }

    public static boolean canSnowlog(@Nullable BlockState state) {
        return state != null && state.getProperties() != null
                && state.hasProperty(LAYERS) && state.getFluidState().isEmpty()
                && ModsUtils.isSnowloggingNotOverrided();
    }

    public static BlockState getSnowloggedState(BlockState state, BlockState snowState) {
        if (snowState != null && canSnowlog(state) && snowState.is(Blocks.SNOW)) {
            int layers = snowState.getValue(BlockStateProperties.LAYERS);
            if (layers < 8)
                state = state.setValue(LAYERS, layers);
        }
        return state;
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

    public static boolean haveLeavesToChangeColor(BlockState state, BlockAndTintGetter getter, BlockPos pos) {
        if (getter != null && pos != null) {
            BlockState upperState = getter.getBlockState(pos.above());
            return (state.getBlock() instanceof LeavesBlock && upperState.is(Blocks.SNOW))
                    || (upperState.getBlock() instanceof LeavesBlock && getter.getBlockState(pos.above(2)).is(Blocks.SNOW));
        }
        return false;
    }
    @OnlyIn(Dist.CLIENT)
    public static boolean haveLeavesToSlightlyChangeColor(BlockState state, BlockAndTintGetter getter, BlockPos pos) {
        Minecraft minecraft = Minecraft.getInstance();
        if (pos != null && minecraft.level != null)
            return state.getBlock() instanceof LeavesBlock && minecraft.level.getBiome(pos) != null
                    && minecraft.level.getBiome(pos).value().getBaseTemperature() < 0.15f
                    && minecraft.level.getBiome(pos).value().hasPrecipitation();
        return false;
    }

    public static boolean haveToFillUpCauldron(BlockState state, ServerLevel world, BlockPos pos) {
        if (world.getFluidState(pos.above()).is(Fluids.WATER) && state.is(Blocks.WATER_CAULDRON) && state.getValue(BlockStateProperties.LEVEL_CAULDRON) != 3)
            return true;
        else if (world.getFluidState(pos.above()).is(Fluids.WATER) && state.is(Blocks.CAULDRON)) {
            world.setBlock(pos, Blocks.WATER_CAULDRON.defaultBlockState().setValue(BlockStateProperties.LEVEL_CAULDRON, 1), 3);
            return true;
        } else if (world.getFluidState(pos.above()).is(Fluids.LAVA) && state.is(Blocks.CAULDRON) && world.getGameRules().getBoolean(GameRules.RULE_LAVA_SOURCE_CONVERSION)) {
            world.setBlock(pos, Blocks.LAVA_CAULDRON.defaultBlockState(), 3);
            return true;
        }
        return false;
    }
}