package net.sashakyotoz.bedrockoid.mixin.entities;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Mob.class)
public class MobEntityMixin {
    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void handleInteraction(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Mob entity = (Mob) (Object) this;
        if (BedrockoidConfig.shulkersCanBeDyed && entity instanceof Shulker shulker && player.getItemInHand(hand).getItem() instanceof DyeItem item) {
            if (shulker.getColor() != item.getDyeColor()) {
                shulker.setVariant(Optional.of(item.getDyeColor()));
                player.getItemInHand(hand).shrink(1);
                cir.setReturnValue(InteractionResult.CONSUME);
            } else
                cir.setReturnValue(InteractionResult.PASS);
        }
    }
}