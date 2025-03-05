package net.sashakyotoz.bedrockoid.mixin.entities;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.AbstractCandleBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "baseTick", at = @At("TAIL"))
    private void handleTick(CallbackInfo ci) {
        Entity entity = (Entity) ((Object) this);
        if (entity.isOnFire() && BedrockoidConfig.entitySharesFire) {
            BlockState state = entity.getWorld().getBlockState(entity.getBlockPos());
            if ((state.getBlock() instanceof CampfireBlock || state.getBlock() instanceof AbstractCandleBlock) && !state.get(Properties.LIT)) {
                entity.getWorld().setBlockState(entity.getBlockPos(),state.with(Properties.LIT,true));
                entity.playSoundIfNotSilent(SoundEvents.ITEM_FLINTANDSTEEL_USE);
            }
        }
    }
    @WrapOperation(
            method = "spawnSprintingParticles",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/particle/ParticleType;Lnet/minecraft/block/BlockState;)Lnet/minecraft/particle/BlockStateParticleEffect;"
            )
    )
    public BlockStateParticleEffect spawnSprintParticle(ParticleType type, BlockState blockState, Operation<BlockStateParticleEffect> original) {
        if (BlockUtils.isSnowlogged(blockState) && BedrockoidConfig.snowlogging)
            blockState = BlockUtils.getSnowEquivalent(blockState);
        return original.call(type, blockState);
    }
}