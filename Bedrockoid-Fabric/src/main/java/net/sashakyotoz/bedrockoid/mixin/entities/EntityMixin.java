package net.sashakyotoz.bedrockoid.mixin.entities;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "baseTick", at = @At("TAIL"))
    private void bedrockoid$shareFire(CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        World world = entity.getWorld();
        if (world.isClient() || !BedrockoidConfig.entitySharesFire || !entity.isOnFire())
            return;
        BlockPos pos = entity.getBlockPos();
        if (!bedrockoid$tryLight(entity, world, pos))
            bedrockoid$tryLight(entity, world, pos.down());
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void bedrockoid$primeTntMinecart(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!BedrockoidConfig.fireAspectImprovements || !((Object) this instanceof TntMinecartEntity minecart) || minecart.isPrimed())
            return;
        ItemStack stack = player.getStackInHand(hand);
        Optional<RegistryEntry.Reference<Enchantment>> fireAspect = player.getWorld().getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.FIRE_ASPECT.getValue());
        if (fireAspect.isEmpty() || EnchantmentHelper.getLevel(fireAspect.get(), stack) <= 0)
            return;
        if (!player.getWorld().isClient()) {
            minecart.prime();
            stack.damage(1, player, hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
        }
        cir.setReturnValue(ActionResult.success(player.getWorld().isClient()));
    }

    @WrapOperation(
            method = "spawnSprintingParticles",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/particle/ParticleType;Lnet/minecraft/block/BlockState;)Lnet/minecraft/particle/BlockStateParticleEffect;"
            )
    )
    private BlockStateParticleEffect bedrockoid$snowSprintParticle(ParticleType<BlockStateParticleEffect> type, BlockState blockState, Operation<BlockStateParticleEffect> original) {
        if (BedrockoidConfig.snowlogging && BlockUtils.isSnowlogged(blockState))
            blockState = BlockUtils.getSnowEquivalent(blockState);
        return original.call(type, blockState);
    }

    @Unique
    private static boolean bedrockoid$tryLight(Entity entity, World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (!CampfireBlock.canBeLit(state) && !CandleBlock.canBeLit(state) && !CandleCakeBlock.canBeLit(state))
            return false;
        world.setBlockState(pos, state.with(Properties.LIT, true));
        world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
        world.emitGameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
        return true;
    }
}
