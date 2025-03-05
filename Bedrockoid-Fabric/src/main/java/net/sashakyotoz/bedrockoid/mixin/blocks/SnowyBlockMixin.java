package net.sashakyotoz.bedrockoid.mixin.blocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.block.SnowyBlock;
import net.minecraft.registry.tag.TagKey;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SnowyBlock.class)
public class SnowyBlockMixin {
    @WrapOperation(
            method = "isSnow",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"
            )
    )
    private static boolean isSnowySetting(BlockState instance, TagKey tagKey, Operation<Boolean> original) {
        return original.call(instance, tagKey) || BlockUtils.isSnowlogged(instance);
    }
}