package net.sashakyotoz.bedrockoid.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;
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
            if ((BlockUtils.isSnowlogged(state)
                    || (state.contains(Properties.DOUBLE_BLOCK_HALF)
                    && world != null && pos != null
                    && BlockUtils.isSnowlogged(world.getBlockState(pos.down()))))
                    && BedrockoidConfig.snowlogging)
                return blockColorProvider == null ? -1 : 0xCCCCCC;
            if (BedrockoidConfig.snowCoversLeaves && state.getBlock() instanceof LeavesBlock)
                return BlockUtils.leavesSnowyColor(state, pos);
        } else
            return original.call(state, world, pos, tintIndex);
        return original.call(state, world, pos, tintIndex);
    }
}