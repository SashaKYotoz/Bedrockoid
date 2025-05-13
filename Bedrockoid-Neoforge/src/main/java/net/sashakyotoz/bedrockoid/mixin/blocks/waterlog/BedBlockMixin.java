package net.sashakyotoz.bedrockoid.mixin.blocks.waterlog;

import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.mixin.blocks.StateDefinitionAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BedBlock.class)
public class BedBlockMixin implements SimpleWaterloggedBlock {
    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void onAppendProperties(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        if (BedrockoidConfig.blocksWaterloggability && !((StateDefinitionAccess) builder).getProperties().containsKey(BlockStateProperties.WATERLOGGED.getName()))
            builder.add(BlockStateProperties.WATERLOGGED);
    }
}