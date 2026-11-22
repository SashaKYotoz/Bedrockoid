package net.sashakyotoz.bedrockoid.mixin.blocks;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @ModifyReturnValue(method = "getPlacementState", at = @At("RETURN"))
    private BlockState bedrockoid$extraPlacementState(BlockState state, ItemPlacementContext context) {
        if (state == null)
            return null;
        BlockPos pos = context.getBlockPos();
        if (BedrockoidConfig.blocksWaterloggability && BlockUtils.hasExtraWaterlogging(state.getBlock()))
            state = state.with(Properties.WATERLOGGED, context.getWorld().getFluidState(pos).getFluid() == Fluids.WATER);
        BlockState replaced = context.getWorld().getBlockState(pos);
        if (BedrockoidConfig.snowlogging && replaced.isOf(Blocks.SNOW) && BlockUtils.canSnowlog(state) && replaced.get(Properties.LAYERS) < 8)
            state = state.with(BlockUtils.LAYERS, replaced.get(Properties.LAYERS));
        return state;
    }

    @WrapOperation(
            method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;getSoundGroup()Lnet/minecraft/sound/BlockSoundGroup;"
            )
    )
    private BlockSoundGroup bedrockoid$snowSoundGroup(BlockState instance, Operation<BlockSoundGroup> original) {
        if (BedrockoidConfig.snowlogging && BlockUtils.isSnowlogged(instance))
            return original.call(BlockUtils.getSnowEquivalent(instance));
        return original.call(instance);
    }

    @Inject(method = "getPlaceSound", at = @At("HEAD"), cancellable = true)
    private void bedrockoid$snowPlaceSound(BlockState state, CallbackInfoReturnable<SoundEvent> cir) {
        if (BedrockoidConfig.snowlogging && BlockUtils.isSnowlogged(state))
            cir.setReturnValue(BlockUtils.getSnowEquivalent(state).getSoundGroup().getPlaceSound());
    }
}
