package net.sashakyotoz.bedrockoid.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.EnchantUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void bedrockoid$lightWithFireAspect(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (!BedrockoidConfig.fireAspectImprovements)
            return;
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        if (!EnchantUtils.hasFireAspect(level, stack))
            return;
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!CampfireBlock.canLight(state) && !CandleBlock.canLight(state) && !CandleCakeBlock.canLight(state))
            return;
        Player player = context.getPlayer();
        level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
        level.setBlock(pos, state.setValue(BlockStateProperties.LIT, Boolean.TRUE), 11);
        level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        if (player != null)
            stack.hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
