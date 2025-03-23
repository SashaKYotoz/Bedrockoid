package net.sashakyotoz.bedrockoid.mixin.entities;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.MinecartTNT;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "baseTick", at = @At("TAIL"))
    private void handleTick(CallbackInfo ci) {
        Entity entity = (Entity) ((Object) this);
        if (entity.isOnFire() && BedrockoidConfig.entitySharesFire) {
            BlockState state = entity.level().getBlockState(entity.getOnPos());
            if ((state.getBlock() instanceof CampfireBlock || state.getBlock() instanceof AbstractCandleBlock) && !state.getValue(BlockStateProperties.LIT)) {
                entity.level().setBlockAndUpdate(entity.getOnPos(), state.setValue(BlockStateProperties.LIT, true));
                entity.playSound(SoundEvents.FLINTANDSTEEL_USE);
            }
        }
    }

    @Inject(method = "interact", at = @At("HEAD"))
    private void primeTntOnRails(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Entity entity = (Entity) ((Object) this);
        ItemStack stack = player.getItemInHand(hand);
        if (entity instanceof MinecartTNT tnt && EnchantmentHelper.getTagEnchantmentLevel(Enchantments.FIRE_ASPECT, stack) > 0
                && BedrockoidConfig.fireAspectImprovements) {
            stack.hurtAndBreak(1, player, player1 -> player1.broadcastBreakEvent(hand));
            tnt.primeFuse();
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
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