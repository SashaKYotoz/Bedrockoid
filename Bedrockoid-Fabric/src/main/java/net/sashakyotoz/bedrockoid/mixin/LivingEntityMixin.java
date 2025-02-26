package net.sashakyotoz.bedrockoid.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.isSneaking() && !entity.isUsingItem()
                && (entity.getStackInHand(Hand.OFF_HAND).getItem() instanceof ShieldItem
                || entity.getStackInHand(Hand.MAIN_HAND).getItem() instanceof ShieldItem) && entity instanceof PlayerEntity player)
            (entity.getStackInHand(Hand.OFF_HAND).getItem() instanceof ShieldItem
                    ? entity.getStackInHand(Hand.OFF_HAND).getItem()
                    : entity.getStackInHand(Hand.MAIN_HAND).getItem()).use(entity.getWorld(), player,
                    entity.getStackInHand(Hand.OFF_HAND).getItem() instanceof ShieldItem ? Hand.OFF_HAND : Hand.MAIN_HAND);
    }

    @Inject(method = "stopUsingItem", at = @At("HEAD"), cancellable = true)
    private void onStopUsingItem(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.isSneaking() && entity.getActiveItem().getItem() instanceof ShieldItem)
            ci.cancel();
    }
}