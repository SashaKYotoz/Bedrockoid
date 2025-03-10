package net.sashakyotoz.bedrockoid.mixin.entities;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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
            BlockState state = entity.level().getBlockState(entity.getOnPos());
            if ((state.getBlock() instanceof CampfireBlock || state.getBlock() instanceof AbstractCandleBlock) && !state.getValue(BlockStateProperties.LIT)) {
                entity.level().setBlockAndUpdate(entity.getOnPos(),state.setValue(BlockStateProperties.LIT,true));
                entity.playSound(SoundEvents.FLINTANDSTEEL_USE);
            }
        }
    }
    @WrapOperation(
            method = "spawnSprintParticle",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/core/particles/ParticleType;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/core/particles/BlockParticleOption;")
    )
    public BlockParticleOption spawnSprintParticle(ParticleType type, BlockState blockState, Operation<BlockParticleOption> original) {
        if (BlockUtils.isSnowlogged(blockState) && BedrockoidConfig.snowlogging)
            blockState = BlockUtils.getSnowEquivalent(blockState);
        return original.call(type, blockState);
    }
}