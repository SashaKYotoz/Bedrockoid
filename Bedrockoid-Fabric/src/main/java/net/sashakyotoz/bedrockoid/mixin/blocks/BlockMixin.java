package net.sashakyotoz.bedrockoid.mixin.blocks;

import net.minecraft.block.*;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
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
    private BlockState defaultState;

    @Inject(method = "setDefaultState", at = @At("TAIL"))
    private void addSnowLayers(BlockState state, CallbackInfo ci) {
        Block block = state.getBlock();
        if (block instanceof PlantBlock && block.getStateManager().getProperties().contains(BlockUtils.LAYERS) && BedrockoidConfig.snowlogging)
            this.defaultState = state.with(BlockUtils.LAYERS, 0);
        if (block instanceof AbstractCauldronBlock && block.getStateManager().getProperties().contains(Properties.WATERLOGGED) && BedrockoidConfig.cauldronWaterloggability)
            this.defaultState = state.with(Properties.WATERLOGGED, false);
    }

    @Inject(method = "appendProperties", at = @At("HEAD"))
    private void onAppendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        Block self = (Block) (Object) this;
        if (self instanceof PlantBlock && ModsUtils.isSnowloggingNotOverrided())
            builder.add(BlockUtils.LAYERS);
        if (self instanceof AbstractCauldronBlock)
            builder.add(Properties.WATERLOGGED);
    }

    @Inject(method = "randomDisplayTick", at = @At("HEAD"))
    private void spawnSnowBelow(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        if (BlockUtils.haveLeavesToChangeColor(state, world, pos)
                && BedrockoidConfig.snowCoversLeaves
                && world.getBlockState(pos.down()).isAir()
                && world.random.nextInt(9) == 4)
            world.addParticle(ParticleTypes.SNOWFLAKE, pos.getX(), pos.getY(), pos.getZ(), world.random.nextBoolean() ? 0.01f : -0.01f, -0.01f, 0);
    }

    @Inject(method = "getPlacementState", at = @At("RETURN"), cancellable = true)
    private void onGetPlacementState(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir) {
        BlockState state = cir.getReturnValue();
        if (state != null && state.contains(Properties.WATERLOGGED) && BedrockoidConfig.cauldronWaterloggability) {
            FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
            cir.setReturnValue(state.with(Properties.WATERLOGGED, fluidState.isIn(FluidTags.WATER)));
        }
    }
}