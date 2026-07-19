package net.sashakyotoz.bedrockoid.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Block.class, priority = 900)
public abstract class BlockMixin {

    @Shadow
    private BlockState defaultBlockState;
    @Inject(method = "registerDefaultState", at = @At("TAIL"))
    private void addExtraStates(BlockState state, CallbackInfo ci) {
        Block block = state.getBlock();
        if (block instanceof BushBlock && block.getStateDefinition().getProperties().contains(BlockUtils.LAYERS) && BedrockoidConfig.snowlogging)
            this.defaultBlockState = state.setValue(BlockUtils.LAYERS, 0);
        if (BlockUtils.isInstanceOfAny(block) && block.getStateDefinition().getProperties().contains(BlockStateProperties.WATERLOGGED) && BedrockoidConfig.blocksWaterloggability)
            this.defaultBlockState = state.setValue(BlockStateProperties.WATERLOGGED, false);
    }

    @Inject(method = "createBlockStateDefinition", at = @At("HEAD"))
    private void onAppendProperties(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        Block self = (Block) (Object) this;
        if (self instanceof BushBlock && ModsUtils.isSnowloggingNotOverrided())
            builder.add(BlockUtils.LAYERS);
        if (self instanceof AbstractCauldronBlock){
            try {
                builder.add(BlockStateProperties.WATERLOGGED);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    @Inject(method = "animateTick", at = @At("HEAD"))
    private void spawnSnowBelow(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (BlockUtils.haveLeavesToChangeColor(state, level, pos)
                && BedrockoidConfig.snowCoversLeaves
                && level.getBlockState(pos.below()).isAir()
                && level.random.nextInt(9) == 4)
            level.addParticle(ParticleTypes.SNOWFLAKE, pos.getX(), pos.getY(), pos.getZ(), level.random.nextBoolean() ? 0.01f : -0.01f, -0.01f, 0);
    }

    @Inject(method = "getStateForPlacement", at = @At("RETURN"), cancellable = true)
    private void onGetPlacementState(BlockPlaceContext ctx, CallbackInfoReturnable<BlockState> cir) {
        BlockState state = cir.getReturnValue();
        BlockState contextState = ctx.getLevel().getBlockState(ctx.getClickedPos());
        if (state != null) {
            if (state.hasProperty(BlockStateProperties.WATERLOGGED) && BedrockoidConfig.blocksWaterloggability) {
                FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
                cir.setReturnValue(state.setValue(BlockStateProperties.WATERLOGGED, fluidState.is(FluidTags.WATER)));
            }
            if (BedrockoidConfig.snowlogging && contextState.is(Blocks.SNOW) && BlockUtils.canSnowlog(state) && contextState.getValue(BlockStateProperties.LAYERS) < 8)
                cir.setReturnValue(state.setValue(BlockUtils.LAYERS, contextState.getValue(BlockStateProperties.LAYERS)));
        }
    }
}