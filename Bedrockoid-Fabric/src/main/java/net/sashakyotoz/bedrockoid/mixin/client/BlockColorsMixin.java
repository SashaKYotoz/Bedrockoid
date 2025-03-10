package net.sashakyotoz.bedrockoid.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.registry.Registries;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockColors.class)
public class BlockColorsMixin {
    @Shadow
    @Final
    private IdList<BlockColorProvider> providers;

    @WrapMethod(method = "getColor")
    private int wrapColor(BlockState state, BlockRenderView world, BlockPos pos, int tintIndex, Operation<Integer> original) {
        if (!ModsUtils.isSodiumIn()) {
            BlockColorProvider blockColorProvider = this.providers.get(Registries.BLOCK.getRawId(state.getBlock()));
            if (BlockUtils.canVinesBeCoveredInSnow(state, world, pos) && BedrockoidConfig.snowCoversVines)
                return 0xCCCCCC;
            if (BlockUtils.isSnowlogged(state) && BedrockoidConfig.snowlogging)
                return blockColorProvider == null ? -1 : 0xCCCCCC;
            if (BedrockoidConfig.snowCoversLeaves) {
                if (BlockUtils.haveLeavesToChangeColor(state, world, pos))
                    return blockColorProvider == null ? -1 : (BlockUtils.haveLeavesToChangeColor(state, world, pos)
                            ? 0xFFFFFF : blockColorProvider.getColor(state, world, pos, tintIndex));
                else
                    return blockColorProvider == null ? -1 : (BlockUtils.haveLeavesToSlightlyChangeColor(state, world, pos)
                            ? 0xCCCCCC : blockColorProvider.getColor(state, world, pos, tintIndex));
            } else
                return blockColorProvider == null ? -1 : blockColorProvider.getColor(state, world, pos, tintIndex);
        } else
            return original.call(state, world, pos, tintIndex);
    }
}