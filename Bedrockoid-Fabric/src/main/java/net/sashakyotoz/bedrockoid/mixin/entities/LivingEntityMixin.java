package net.sashakyotoz.bedrockoid.mixin.entities;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Hand;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void bedrockoid$raiseShieldWhenSneaking(CallbackInfo ci) {
        if (!BedrockoidConfig.shieldActivatesWhenSneaking || !((Object) this instanceof PlayerEntity player))
            return;
        if (!player.isAlive() || !player.isSneaking() || player.isUsingItem())
            return;
        Hand hand = bedrockoid$shieldHand(player);
        if (hand == null)
            return;
        ItemStack shield = player.getStackInHand(hand);
        if (player.getItemCooldownManager().isCoolingDown(shield.getItem()))
            return;
        shield.getItem().use(player.getWorld(), player, hand);
    }

    @Inject(method = "stopUsingItem", at = @At("HEAD"), cancellable = true)
    private void bedrockoid$keepShieldRaised(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!BedrockoidConfig.shieldActivatesWhenSneaking || !(self instanceof PlayerEntity player))
            return;
        if (!player.isAlive() || !player.isSneaking())
            return;
        ItemStack using = player.getActiveItem();
        if (!(using.getItem() instanceof ShieldItem) || player.getItemCooldownManager().isCoolingDown(using.getItem()))
            return;
        if (!ItemStack.areItemsEqual(player.getStackInHand(player.getActiveHand()), using))
            return;
        ci.cancel();
    }

    @Unique
    private static Hand bedrockoid$shieldHand(PlayerEntity player) {
        if (player.getStackInHand(Hand.OFF_HAND).getItem() instanceof ShieldItem)
            return Hand.OFF_HAND;
        if (player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof ShieldItem)
            return Hand.MAIN_HAND;
        return null;
    }
}
