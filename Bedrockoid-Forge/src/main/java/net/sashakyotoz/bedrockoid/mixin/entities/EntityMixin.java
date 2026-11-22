package net.sashakyotoz.bedrockoid.mixin.entities;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "baseTick", at = @At("TAIL"))
    private void bedrockoid$shareFire(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        Level level = entity.level();
        if (level.isClientSide() || !BedrockoidConfig.entitySharesFire || !entity.isOnFire())
            return;
        BlockPos pos = entity.blockPosition();
        if (!bedrockoid$tryLight(entity, level, pos))
            bedrockoid$tryLight(entity, level, pos.below());
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void bedrockoid$primeTntMinecart(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!BedrockoidConfig.fireAspectImprovements || !((Object) this instanceof MinecartTNT minecart) || minecart.isPrimed())
            return;
        ItemStack stack = player.getItemInHand(hand);
        if (EnchantmentHelper.getTagEnchantmentLevel(Enchantments.FIRE_ASPECT, stack) <= 0)
            return;
        if (!player.level().isClientSide()) {
            minecart.primeFuse();
            stack.hurtAndBreak(1, player, user -> user.broadcastBreakEvent(hand));
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
        }
        cir.setReturnValue(InteractionResult.sidedSuccess(player.level().isClientSide()));
    }

    @WrapOperation(
            method = "spawnSprintParticle",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/core/particles/ParticleType;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/core/particles/BlockParticleOption;")
    )
    private BlockParticleOption bedrockoid$snowSprintParticle(ParticleType<BlockParticleOption> type, BlockState blockState, Operation<BlockParticleOption> original) {
        if (BedrockoidConfig.snowlogging && BlockUtils.isSnowlogged(blockState))
            blockState = BlockUtils.getSnowEquivalent(blockState);
        return original.call(type, blockState);
    }

    @Unique
    private static boolean bedrockoid$tryLight(Entity entity, Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!CampfireBlock.canLight(state) && !CandleBlock.canLight(state) && !CandleCakeBlock.canLight(state))
            return false;
        level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.LIT, true));
        level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
        level.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
        return true;
    }
}