package net.sashakyotoz.bedrockoid.mixin.blocks;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
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

    @Shadow protected abstract BlockState asState();

    @Inject(method = "updateShape", at = @At("HEAD"))
    private void onUpdateShape(LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random, CallbackInfoReturnable<BlockState> cir) {
        if (level instanceof LevelAccessor accessor && level.getBlockState(pos).hasProperty(BlockStateProperties.WATERLOGGED) && level.getBlockState(pos).getValue(BlockStateProperties.WATERLOGGED) && BedrockoidConfig.cauldronWaterloggability)
            accessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
    }

    @Inject(method = "getFluidState", at = @At("HEAD"), cancellable = true)
    private void applyWaterloggability(CallbackInfoReturnable<FluidState> cir) {
        BlockBehaviour.BlockStateBase block = (BlockBehaviour.BlockStateBase) ((Object) this);
        if (block.getBlock() instanceof AbstractCauldronBlock && block.hasProperty(BlockStateProperties.WATERLOGGED) && BedrockoidConfig.cauldronWaterloggability)
            cir.setReturnValue(block.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : Fluids.EMPTY.defaultFluidState());
    }

    @ModifyReturnValue(method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"))
    private VoxelShape getCollisionShape(VoxelShape original, BlockGetter world, BlockPos pos, CollisionContext context) {
        BlockState blockState = this.asState();
        if (BlockUtils.isSnowlogged(blockState) && !ModsUtils.isSodiumIn())
            return Shapes.or(original, BlockUtils.getSnowEquivalent(blockState).getCollisionShape(world, pos, context));
        return original;
    }

    @ModifyReturnValue(method = "getVisualShape", at = @At("RETURN"))
    private VoxelShape getVisualShape(VoxelShape original, BlockGetter world, BlockPos pos, CollisionContext context) {
        BlockState blockState = this.asState();
        if (BlockUtils.isSnowlogged(blockState) && !ModsUtils.isSodiumIn())
            return Shapes.or(original, BlockUtils.getSnowEquivalent(blockState).getShape(world, pos, context));
        return original;
    }

    @Inject(method = "randomTick", at = @At("TAIL"))
    private void randomTick(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof WetSpongeBlock
                && BedrockoidConfig.wetSpongesDryOut
                && random.nextInt(12) == 5
                && !level.getBiome(pos).value().hasPrecipitation()
                && (level.getBiome(pos).value().getBaseTemperature() > 0.75f)) {
            level.setBlock(pos, Blocks.SPONGE.defaultBlockState(), 3);
            level.levelEvent(LevelEvent.PARTICLES_WATER_EVAPORATING, pos, 0);
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, (1.0F + level.getRandom().nextFloat() * 0.2F) * 0.7F);
        }
        if (random.nextInt(4) == 1 && BlockUtils.haveToFillUpCauldron(state, level, pos)) {
            if (state.getBlock() instanceof LayeredCauldronBlock && BedrockoidConfig.cauldronNaturalFilling) {
                BlockState blockState = state.cycle(BlockStateProperties.LEVEL_CAULDRON);
                level.setBlockAndUpdate(pos, blockState);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
            }
        }
    }

    @Inject(method = "isRandomlyTicking", at = @At("HEAD"), cancellable = true)
    private void hasRandomTicks(CallbackInfoReturnable<Boolean> cir) {
        BlockBehaviour.BlockStateBase block = (BlockBehaviour.BlockStateBase) ((Object) this);
        if ((block.getBlock() instanceof WetSpongeBlock && BedrockoidConfig.wetSpongesDryOut)
                || (block.getBlock() instanceof AbstractCauldronBlock && BedrockoidConfig.cauldronNaturalFilling))
            cir.setReturnValue(true);
    }
}