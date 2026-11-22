package net.sashakyotoz.bedrockoid.mixin.blocks;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.tag.FluidTags;
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

    @Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"))
    private void bedrockoid$scheduleWaterTick(Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        if (BedrockoidConfig.blocksWaterloggability && BlockUtils.isExtraWaterlogged(this.asBlockState()))
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
    }

    @Inject(method = "getFluidState", at = @At("HEAD"), cancellable = true)
    private void bedrockoid$applyWaterloggability(CallbackInfoReturnable<FluidState> cir) {
        BlockState state = this.asBlockState();
        if (BedrockoidConfig.blocksWaterloggability && BlockUtils.hasExtraWaterlogging(state.getBlock()))
            cir.setReturnValue(state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : Fluids.EMPTY.getDefaultState());
    }

    @ModifyReturnValue(method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At("RETURN"))
    private VoxelShape bedrockoid$snowCollisionShape(VoxelShape original, BlockView world, BlockPos pos, ShapeContext context) {
        BlockState state = this.asBlockState();
        if (BlockUtils.isSnowlogged(state))
            return VoxelShapes.union(original, BlockUtils.getSnowEquivalent(state).getCollisionShape(world, pos, context));
        return original;
    }

    @ModifyReturnValue(method = "getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At("RETURN"))
    private VoxelShape bedrockoid$snowOutlineShape(VoxelShape original, BlockView world, BlockPos pos, ShapeContext context) {
        BlockState state = this.asBlockState();
        if (BlockUtils.isSnowlogged(state))
            return VoxelShapes.union(original, BlockUtils.getSnowEquivalent(state).getOutlineShape(world, pos, context));
        return original;
    }

    @Inject(method = "randomTick", at = @At("TAIL"))
    private void bedrockoid$randomTick(ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof WetSpongeBlock) {
            if (BedrockoidConfig.wetSpongesDryOut
                    && random.nextInt(12) == 5
                    && !world.getBiome(pos).value().hasPrecipitation()
                    && world.getBiome(pos).value().getTemperature() >= 0.75f
                    && !BlockUtils.isTouchingBlock(world, pos, neighbor -> neighbor != null && neighbor.getFluidState().isIn(FluidTags.WATER))) {
                world.setBlockState(pos, Blocks.SPONGE.getDefaultState(), Block.NOTIFY_ALL);
                world.syncWorldEvent(WorldEvents.WET_SPONGE_DRIES_OUT, pos, 0);
                world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, (1.0F + world.getRandom().nextFloat() * 0.2F) * 0.7F);
            }
        } else if (block instanceof AbstractCauldronBlock && BedrockoidConfig.cauldronNaturalFilling && random.nextInt(4) == 1) {
            BlockUtils.fillCauldronNaturally(state, world, pos);
        }
    }

    @Inject(method = "hasRandomTicks", at = @At("HEAD"), cancellable = true)
    private void bedrockoid$extraRandomTicks(CallbackInfoReturnable<Boolean> cir) {
        Block block = this.asBlockState().getBlock();
        if ((block instanceof WetSpongeBlock && BedrockoidConfig.wetSpongesDryOut)
                || (block instanceof AbstractCauldronBlock && BedrockoidConfig.cauldronNaturalFilling))
            cir.setReturnValue(true);
    }
}
