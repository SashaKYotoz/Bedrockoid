package net.sashakyotoz.bedrockoid.mixin.entities;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
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
        if (!BedrockoidConfig.shieldActivatesWhenSneaking || !((Object) this instanceof Player player))
            return;
        if (!player.isAlive() || !player.isCrouching() || player.isUsingItem())
            return;
        InteractionHand hand = bedrockoid$shieldHand(player);
        if (hand == null)
            return;
        ItemStack shield = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(shield.getItem()))
            return;
        shield.getItem().use(player.level(), player, hand);
    }

    @Inject(method = "stopUsingItem", at = @At("HEAD"), cancellable = true)
    private void bedrockoid$keepShieldRaised(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!BedrockoidConfig.shieldActivatesWhenSneaking || !(self instanceof Player player))
            return;
        if (!player.isAlive() || !player.isCrouching())
            return;
        ItemStack using = player.getUseItem();
        if (!(using.getItem() instanceof ShieldItem) || player.getCooldowns().isOnCooldown(using.getItem()))
            return;
        if (!ItemStack.isSameItem(player.getItemInHand(player.getUsedItemHand()), using))
            return;
        ci.cancel();
    }

    @Unique
    private static InteractionHand bedrockoid$shieldHand(Player player) {
        if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ShieldItem)
            return InteractionHand.OFF_HAND;
        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ShieldItem)
            return InteractionHand.MAIN_HAND;
        return null;
    }
}
