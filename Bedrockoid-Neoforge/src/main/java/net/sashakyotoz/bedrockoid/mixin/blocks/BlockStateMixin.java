package net.sashakyotoz.bedrockoid.mixin.blocks;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.WetSpongeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateMixin {
    @Shadow
    protected abstract BlockState asState();

    @Inject(method = "updateShape", at = @At("HEAD"))
    private void bedrockoid$scheduleWaterTick(Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos, CallbackInfoReturnable<BlockState> cir) {
        if (BedrockoidConfig.blocksWaterloggability && BlockUtils.isExtraWaterlogged(this.asState()))
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
    }

    @Inject(method = "getFluidState", at = @At("HEAD"), cancellable = true)
    private void bedrockoid$applyWaterloggability(CallbackInfoReturnable<FluidState> cir) {
        BlockState state = this.asState();
        if (BedrockoidConfig.blocksWaterloggability && BlockUtils.hasExtraWaterlogging(state.getBlock()))
            cir.setReturnValue(state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState());
    }

    @ModifyReturnValue(method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"))
    private VoxelShape bedrockoid$snowShape(VoxelShape original, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockState state = this.asState();
        if (BlockUtils.isSnowlogged(state) && !ModsUtils.isSodiumIn())
            return Shapes.or(original, BlockUtils.getSnowEquivalent(state).getCollisionShape(level, pos, context));
        return original;
    }

    @ModifyReturnValue(method = "getVisualShape", at = @At("RETURN"))
    private VoxelShape bedrockoid$snowVisualShape(VoxelShape original, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockState state = this.asState();
        if (BlockUtils.isSnowlogged(state) && !ModsUtils.isSodiumIn())
            return Shapes.or(original, BlockUtils.getSnowEquivalent(state).getShape(level, pos, context));
        return original;
    }

    @Inject(method = "randomTick", at = @At("TAIL"))
    private void bedrockoid$randomTick(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof WetSpongeBlock) {
            if (BedrockoidConfig.wetSpongesDryOut
                    && random.nextInt(12) == 5
                    && !level.getBiome(pos).value().hasPrecipitation()
                    && level.getBiome(pos).value().getBaseTemperature() >= 0.75f
                    && !BlockUtils.isTouchingBlock(level, pos, neighbor -> neighbor != null && neighbor.getFluidState().is(FluidTags.WATER))) {
                level.setBlock(pos, Blocks.SPONGE.defaultBlockState(), 3);
                level.levelEvent(LevelEvent.PARTICLES_WATER_EVAPORATING, pos, 0);
                level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, (1.0F + level.getRandom().nextFloat() * 0.2F) * 0.7F);
            }
        } else if (block instanceof AbstractCauldronBlock && BedrockoidConfig.cauldronNaturalFilling && random.nextInt(4) == 1) {
            BlockUtils.fillCauldronNaturally(state, level, pos);
        }
    }

    @Inject(method = "isRandomlyTicking", at = @At("HEAD"), cancellable = true)
    private void bedrockoid$extraRandomTicks(CallbackInfoReturnable<Boolean> cir) {
        Block block = this.asState().getBlock();
        if ((block instanceof WetSpongeBlock && BedrockoidConfig.wetSpongesDryOut)
                || (block instanceof AbstractCauldronBlock && BedrockoidConfig.cauldronNaturalFilling))
            cir.setReturnValue(true);
    }
}
