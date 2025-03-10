package net.sashakyotoz.bedrockoid.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void litBlockWithAspect(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = context.getItemInHand();
        if (EnchantmentHelper.getTagEnchantmentLevel(Enchantments.FIRE_ASPECT, itemStack) > 0 && BedrockoidConfig.fireAspectImprovements) {
            Player player = context.getPlayer();
            Level world = context.getLevel();
            BlockPos blockPos = context.getClickedPos();
            BlockState blockState = world.getBlockState(blockPos);
            if (CampfireBlock.canLight(blockState) || CandleBlock.canLight(blockState) || CandleCakeBlock.canLight(blockState)) {
                world.playSound(player, blockPos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
                world.setBlock(blockPos, blockState.setValue(BlockStateProperties.LIT, Boolean.TRUE), 3 | 8);
                world.gameEvent(player, GameEvent.BLOCK_CHANGE, blockPos);
                if (player != null)
                    context.getItemInHand().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
                cir.setReturnValue(InteractionResult.sidedSuccess(world.isClientSide()));
            }
        }
    }
}