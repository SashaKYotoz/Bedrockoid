package net.sashakyotoz.bedrockoid.mixin.blocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PlantBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
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

import java.util.Map;

@Mixin(value = Block.class, priority = 900)
public abstract class BlockMixin implements BedrockoidBlock {
    @Shadow
    private BlockState defaultState;

    @Unique
    private boolean bedrockoid$extraWaterlogging;

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;appendProperties(Lnet/minecraft/state/StateManager$Builder;)V"))
    private void bedrockoid$appendExtraProperties(Block instance, StateManager.Builder<Block, BlockState> builder, Operation<Void> original) {
        original.call(instance, builder);
        if (!StartupFlags.WATERLOGGING || !BlockUtils.isWaterloggingCandidate(instance))
            return;
        Map<String, Property<?>> properties = ((StateDefinitionAccess) builder).getProperties();
        Property<?> existing = properties.get(Properties.WATERLOGGED.getName());
        if (existing == null) {
            builder.add(Properties.WATERLOGGED);
            this.bedrockoid$extraWaterlogging = true;
        } else {
            this.bedrockoid$extraWaterlogging = existing == Properties.WATERLOGGED;
        }
    }

    @Inject(method = "appendProperties", at = @At("HEAD"))
    private void bedrockoid$addSnowLayers(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        if ((Object) this instanceof PlantBlock && StartupFlags.SNOWLOGGING
                && !((StateDefinitionAccess) builder).getProperties().containsKey(BlockUtils.LAYERS.getName()))
            builder.add(BlockUtils.LAYERS);
    }

    @Inject(method = "setDefaultState", at = @At("TAIL"))
    private void bedrockoid$extraDefaults(BlockState state, CallbackInfo ci) {
        BlockState result = this.defaultState;
        if (result.contains(BlockUtils.LAYERS))
            result = result.with(BlockUtils.LAYERS, 0);
        if (this.bedrockoid$extraWaterlogging)
            result = result.with(Properties.WATERLOGGED, false);
        this.defaultState = result;
    }

    @Inject(method = "randomDisplayTick", at = @At("HEAD"))
    private void bedrockoid$spawnSnowBelow(BlockState state, World world, BlockPos pos, Random random, CallbackInfo ci) {
        if (BedrockoidConfig.snowCoversLeaves
                && BlockUtils.haveLeavesToChangeColor(state, world, pos)
                && world.getBlockState(pos.down()).isAir()
                && random.nextInt(9) == 4)
            world.addParticle(ParticleTypes.SNOWFLAKE, pos.getX() + random.nextDouble(), pos.getY(), pos.getZ() + random.nextDouble(), random.nextBoolean() ? 0.01f : -0.01f, -0.01f, 0);
    }

    @Override
    public boolean bedrockoid$hasExtraWaterlogging() {
        return this.bedrockoid$extraWaterlogging;
    }
}
