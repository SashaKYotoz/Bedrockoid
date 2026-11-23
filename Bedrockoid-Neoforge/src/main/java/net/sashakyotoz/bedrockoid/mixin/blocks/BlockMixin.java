package net.sashakyotoz.bedrockoid.mixin.blocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BedrockoidBlock;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import net.sashakyotoz.bedrockoid.common.utils.StartupFlags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Block.class, priority = 900)
public abstract class BlockMixin implements BedrockoidBlock {
    @Shadow
    private BlockState defaultBlockState;

    @Unique
    private boolean bedrockoid$extraWaterlogging;

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;createBlockStateDefinition(Lnet/minecraft/world/level/block/state/StateDefinition$Builder;)V"))
    private void bedrockoid$appendExtraProperties(Block instance, StateDefinition.Builder<Block, BlockState> builder, Operation<Void> original) {
        original.call(instance, builder);
        if (!StartupFlags.WATERLOGGING || !BlockUtils.isWaterloggingCandidate(instance))
            return;
        BlockUtils.addProperty(builder, BlockStateProperties.WATERLOGGED);
        this.bedrockoid$extraWaterlogging = true;
    }

    @Inject(method = "createBlockStateDefinition", at = @At("HEAD"))
    private void bedrockoid$addSnowLayers(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        if ((Object) this instanceof VegetationBlock && StartupFlags.SNOWLOGGING)
            BlockUtils.addProperty(builder, BlockUtils.LAYERS);
    }

    @Inject(method = "registerDefaultState", at = @At("TAIL"))
    private void bedrockoid$extraDefaults(BlockState state, CallbackInfo ci) {
        BlockState result = this.defaultBlockState;
        if (result.hasProperty(BlockUtils.LAYERS))
            result = result.setValue(BlockUtils.LAYERS, 0);
        if (this.bedrockoid$extraWaterlogging && !result.hasProperty(BlockStateProperties.WATERLOGGED))
            this.bedrockoid$extraWaterlogging = false;
        if (this.bedrockoid$extraWaterlogging)
            result = result.setValue(BlockStateProperties.WATERLOGGED, false);
        this.defaultBlockState = result;
    }

    @Inject(method = "animateTick", at = @At("HEAD"))
    private void bedrockoid$spawnSnowBelow(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if (BedrockoidConfig.snowCoversLeaves
                && BlockUtils.haveLeavesToChangeColor(state, level, pos)
                && level.getBlockState(pos.below()).isAir()
                && random.nextInt(9) == 4)
            level.addParticle(ParticleTypes.SNOWFLAKE, pos.getX() + random.nextDouble(), pos.getY(), pos.getZ() + random.nextDouble(), random.nextBoolean() ? 0.01f : -0.01f, -0.01f, 0);
    }

    @Override
    public boolean bedrockoid$hasExtraWaterlogging() {
        return this.bedrockoid$extraWaterlogging;
    }
}
