package net.sashakyotoz.bedrockoid.common.utils;

import com.google.common.base.Predicate;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.biome.FoliageColors;
import org.jetbrains.annotations.Nullable;

public class BlockUtils {
    public static final IntProperty LAYERS = IntProperty.of("snow_layers", 0, 8);

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

    public static BlockState getSnowPlacementState(BlockState state, ItemPlacementContext context) {
        return getSnowloggedState(state, context.getWorld().getBlockState(context.getBlockPos()));
    }

    public static boolean canSnowlog(@Nullable BlockState state) {
        return state != null && state.getProperties() != null
                && state.contains(LAYERS) && state.getFluidState().isEmpty()
                && ModsUtils.isSnowloggingNotOverrided();
    }

    public static boolean isTouchingBlock(World world, BlockPos pos, Predicate<BlockState> predicate) {
        for (Direction dir : Direction.values()) {
            BlockState state = world.getBlockState(pos.offset(dir));
            if (predicate.test(state)) return true;
        }
        return false;
    }

    public static BlockState getSnowloggedState(BlockState state, BlockState snowState) {
        if (snowState != null && canSnowlog(state) && snowState.isOf(Blocks.SNOW)) {
            int layers = snowState.get(Properties.LAYERS);
            if (layers < 8)
                state = state.with(LAYERS, layers);
        }
        return state;
    }

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

    public static int leavesSnowyColor(BlockState state, BlockPos pos) {
        ClientWorld level = MinecraftClient.getInstance().world;
        if (state.getBlock() instanceof LeavesBlock && level != null && pos != null) {
            BlockState upperState = level.getBlockState(pos.up());
            if (upperState.isOf(Blocks.SNOW) || (upperState.getBlock() instanceof LeavesBlock && level.getBlockState(pos.up(2)).isOf(Blocks.SNOW)))
                return BlockUtils.blendColors(BiomeColors.getFoliageColor(level, pos), 0xFFFFFF, 0.95f);
            if (level.getBiome(pos).value().getTemperature() < 0.1f && level.getBiome(pos).value().hasPrecipitation()) {
                return getNeighborBlocks(level, pos) ? BlockUtils.blendColors(BiomeColors.getFoliageColor(level, pos), 0xFFFFFF, 0.4f)
                        : BlockUtils.blendColors(BiomeColors.getFoliageColor(level, pos), 0xFFFFFF, 0.75f);
            }
        }
        return level != null && pos != null ? BiomeColors.getFoliageColor(level, pos) : FoliageColors.DEFAULT;
    }

    private static boolean getNeighborBlocks(ClientWorld world, BlockPos pos) {
        return world.getBiome(pos.north()).value().getTemperature() > 0.1f
                || world.getBiome(pos.south()).value().getTemperature() > 0.1f
                || world.getBiome(pos.east()).value().getTemperature() > 0.1f
                || world.getBiome(pos.west()).value().getTemperature() > 0.1f;
    }

    public static int blendColors(int colorA, int colorB, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int) ((((colorA >> 16) & 0xFF) * (1 - t)) + (((colorB >> 16) & 0xFF) * t));
        int g = (int) ((((colorA >> 8) & 0xFF) * (1 - t)) + (((colorB >> 8) & 0xFF) * t));
        int b = (int) ((((colorA) & 0xFF) * (1 - t)) + (((colorB) & 0xFF) * t));
        return (r << 16) | (g << 8) | b;
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

    private static final Class<?>[] WATERLOGGED_MISSING_BLOCKS = {
            LeveledCauldronBlock.class,
            AbstractCauldronBlock.class,
            AnvilBlock.class,
            BellBlock.class,
            BedBlock.class,
            PressurePlateBlock.class,
            FenceGateBlock.class,
            LecternBlock.class,
            HopperBlock.class,
            BrewingStandBlock.class,
            GrindstoneBlock.class,
            StonecutterBlock.class
    };

    public static boolean isInstanceOfAny(Block block) {
        for (Class<?> clazz : WATERLOGGED_MISSING_BLOCKS) {
            if (clazz.isInstance(block))
                return true;
        }
        return false;
    }
}