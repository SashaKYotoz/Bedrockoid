package net.sashakyotoz.bedrockoid.mixin.blocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockColors.class)
public class BlockColorsMixin {
    @WrapOperation(method = "getColor(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;I)I",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/color/block/BlockColor;getColor(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;I)I"))
    private int handleSpruceColor(BlockColor instance, BlockState state, BlockAndTintGetter getter, BlockPos pos, int i, Operation<Integer> original) {
        if (BedrockoidConfig.snowCoversLeaves && BlockUtils.haveLeavesToChangeColor(state, getter, pos)
                || BlockUtils.haveLeavesToSlightlyChangeColor(state, getter, pos)) {
            int colour = 0;
            if (BlockUtils.haveLeavesToChangeColor(state, getter, pos))
                colour = 0xFFFFFF;
            if (BlockUtils.haveLeavesToSlightlyChangeColor(state, getter, pos))
                colour = 0xCCCCCC;
            return colour;
        }
        if (BlockUtils.isSnowlogged(state) && BedrockoidConfig.snowlogging)
            return 0xCCCCCC;
        if (BlockUtils.canVinesBeCoveredInSnow(state, getter, pos) && BedrockoidConfig.snowCoversVines)
            return 0xCCCCCC;
        return original.call(instance,state,getter,pos,i);
    }
}