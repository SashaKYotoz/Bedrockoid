package net.sashakyotoz.bedrockoid.mixin.entities;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Mob.class)
public abstract class MobEntityMixin {
    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void bedrockoid$dyeShulker(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!BedrockoidConfig.shulkersCanBeDyed || !((Object) this instanceof Shulker shulker))
            return;
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof DyeItem dye))
            return;
        if (shulker.getColor() == dye.getDyeColor()) {
            cir.setReturnValue(InteractionResult.PASS);
            return;
        }
        if (!player.level().isClientSide()) {
            shulker.setVariant(Optional.of(dye.getDyeColor()));
            if (!player.getAbilities().instabuild)
                stack.shrink(1);
        }
        cir.setReturnValue(InteractionResult.sidedSuccess(player.level().isClientSide()));
    }
}
