package net.sashakyotoz.bedrockoid.mixin.entities;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {
    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void bedrockoid$dyeShulker(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!BedrockoidConfig.shulkersCanBeDyed || !((Object) this instanceof ShulkerEntity shulker))
            return;
        ItemStack stack = player.getStackInHand(hand);
        if (!(stack.getItem() instanceof DyeItem dye))
            return;
        if (shulker.getColor() == dye.getColor()) {
            cir.setReturnValue(ActionResult.PASS);
            return;
        }
        if (!player.getWorld().isClient()) {
            shulker.setVariant(Optional.of(dye.getColor()));
            if (!player.getAbilities().creativeMode)
                stack.decrement(1);
        }
        cir.setReturnValue(ActionResult.success(player.getWorld().isClient()));
    }
}
