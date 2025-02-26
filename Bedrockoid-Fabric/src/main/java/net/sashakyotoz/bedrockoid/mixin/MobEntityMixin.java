package net.sashakyotoz.bedrockoid.mixin;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void handleInteraction(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        MobEntity entity = (MobEntity) (Object) this;
        if (entity instanceof ShulkerEntity shulker && player.getStackInHand(hand).getItem() instanceof DyeItem item) {
            if (shulker.getColor() != item.getColor()) {
                shulker.setVariant(Optional.of(item.getColor()));
                player.getStackInHand(hand).decrement(1);
                cir.setReturnValue(ActionResult.CONSUME);
            } else
                cir.setReturnValue(ActionResult.PASS);
        }
    }
}