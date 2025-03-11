package net.sashakyotoz.bedrockoid.mixin.blocks;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.tick.ScheduledTickView;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class BlockStateMixin {

    @Shadow
    protected abstract BlockState asBlockState();

//    @Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"))
//    private void onUpdateShape(WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random, CallbackInfoReturnable<BlockState> cir) {
//        if (world.getBlockState(pos).contains(Properties.WATERLOGGED) && world.getBlockState(pos).get(Properties.WATERLOGGED) && BedrockoidConfig.cauldronWaterloggability)
//            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
//    }

    @Inject(method = "getFluidState", at = @At("HEAD"), cancellable = true)
    private void applyWaterloggability(CallbackInfoReturnable<FluidState> cir) {
        AbstractBlock.AbstractBlockState block = (AbstractBlock.AbstractBlockState) ((Object) this);
        if (block.getBlock() instanceof AbstractCauldronBlock && block.contains(Properties.WATERLOGGED) && BedrockoidConfig.cauldronWaterloggability)
            cir.setReturnValue(block.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : Fluids.EMPTY.getDefaultState());
    }

    @ModifyReturnValue(method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At("RETURN"))
    private VoxelShape getCollisionShape(VoxelShape original, BlockView world, BlockPos pos, ShapeContext context) {
        BlockState blockState = this.asBlockState();
        if (BlockUtils.isSnowlogged(blockState))
            return VoxelShapes.union(original, BlockUtils.getSnowEquivalent(blockState).getCollisionShape(world, pos, context));
        return original;
    }

    @ModifyReturnValue(method = "getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At("RETURN"))
    private VoxelShape getVisualShape(VoxelShape original, BlockView world, BlockPos pos, ShapeContext context) {
        BlockState blockState = this.asBlockState();
        if (BlockUtils.isSnowlogged(blockState))
            return VoxelShapes.union(original, BlockUtils.getSnowEquivalent(blockState).getOutlineShape(world, pos, context));
        return original;
    }

    @Inject(method = "randomTick", at = @At("TAIL"))
    private void randomTick(ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof WetSpongeBlock
                && BedrockoidConfig.wetSpongesDryOut
                && random.nextInt(26) == 5
                && !world.getBiome(pos).value().hasPrecipitation()
                && (world.getBiome(pos).value().getTemperature() > 0.75f)) {
            world.setBlockState(pos, Blocks.SPONGE.getDefaultState(), Block.NOTIFY_ALL);
            world.syncWorldEvent(WorldEvents.WET_SPONGE_DRIES_OUT, pos, 0);
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, (1.0F + world.getRandom().nextFloat() * 0.2F) * 0.7F);
        }
        if (random.nextInt(5) == 1 && BlockUtils.haveToFillUpCauldron(state, world, pos)) {
            if (state.getBlock() instanceof LeveledCauldronBlock && BedrockoidConfig.cauldronNaturalFilling) {
                BlockState blockState = state.cycle(Properties.LEVEL_3);
                world.setBlockState(pos, blockState);
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(blockState));
            }
        }
    }

    @Inject(method = "hasRandomTicks", at = @At("HEAD"), cancellable = true)
    private void hasRandomTicks(CallbackInfoReturnable<Boolean> cir) {
        AbstractBlock.AbstractBlockState block = (AbstractBlock.AbstractBlockState) ((Object) this);
        if ((block.getBlock() instanceof WetSpongeBlock && BedrockoidConfig.wetSpongesDryOut)
                || (block.getBlock() instanceof AbstractCauldronBlock && BedrockoidConfig.cauldronNaturalFilling))
            cir.setReturnValue(true);
    }
}