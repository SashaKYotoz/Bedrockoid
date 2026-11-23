package net.sashakyotoz.bedrockoid.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.EnchantUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TntBlock.class)
public class TntBlockMixin {
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void bedrockoid$igniteWithFireAspect(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (!BedrockoidConfig.fireAspectImprovements || !EnchantUtils.hasFireAspect(level, stack))
            return;
        if (!level.isClientSide()) {
            if (stack.getMaxStackSize() == 1)
                stack.hurtAndBreak(1, player, hand.asEquipmentSlot());
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            if (TntBlock.prime(level, pos))
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
        }
        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
