package net.sashakyotoz.bedrockoid.mixin.entities;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShieldItem;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (BedrockoidConfig.shieldActivatesWhenSneaking){
            if (entity.isCrouching() && !entity.isUsingItem()
                    && (entity.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ShieldItem
                    || entity.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ShieldItem) && entity instanceof Player player)
                (entity.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ShieldItem
                        ? entity.getItemInHand(InteractionHand.OFF_HAND).getItem()
                        : entity.getItemInHand(InteractionHand.MAIN_HAND).getItem()).use(entity.level(), player,
                        entity.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ShieldItem ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        }
    }

    @Inject(method = "stopUsingItem", at = @At("HEAD"), cancellable = true)
    private void onStopUsingItem(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.isCrouching() && entity.getUseItem().getItem() instanceof ShieldItem && BedrockoidConfig.shieldActivatesWhenSneaking)
            ci.cancel();
    }
}