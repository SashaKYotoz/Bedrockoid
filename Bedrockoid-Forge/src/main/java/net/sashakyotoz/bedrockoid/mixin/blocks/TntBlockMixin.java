package net.sashakyotoz.bedrockoid.mixin.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TntBlock.class)
public class TntBlockMixin {

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"), cancellable = true)
    private void fireTnt(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        if (EnchantmentHelper.getTagEnchantmentLevel(Enchantments.FIRE_ASPECT, stack) > 0 && BedrockoidConfig.fireAspectImprovements) {
            if (stack.getMaxStackSize() == 1)
                stack.hurtAndBreak(1, player, playerx -> playerx.broadcastBreakEvent(hand));
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            TntBlock.explode(world, pos);
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3 | 8);
            cir.setReturnValue(InteractionResult.sidedSuccess(world.isClientSide()));
        }
    }
}