package net.sashakyotoz.bedrockoid.mixin.blocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @WrapOperation(
            method = "place*",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;getSoundGroup()Lnet/minecraft/sound/BlockSoundGroup;"
            )
    )
    public BlockSoundGroup place(BlockState instance, Operation<BlockSoundGroup> original) {
        return (BlockUtils.isSnowlogged(instance) && BedrockoidConfig.snowlogging) ?
                original.call(BlockUtils.getSnowEquivalent(instance)) : original.call(instance);
    }

    @Inject(method = "getPlaceSound", at = @At("HEAD"), cancellable = true)
    public void getPlaceSound(BlockState state, CallbackInfoReturnable<SoundEvent> cir) {
        if (BlockUtils.isSnowlogged(state) && ModsUtils.isSnowloggingNotOverrided()) {
            cir.setReturnValue(BlockUtils.getSnowEquivalent(state).getSoundGroup().getPlaceSound());
        }
    }
}