package net.sashakyotoz.bedrockoid.mixin.blocks;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {LayeredCauldronBlock.class, AnvilBlock.class, GrindstoneBlock.class, StonecutterBlock.class,
        LecternBlock.class, HopperBlock.class, BrewingStandBlock.class, BedBlock.class, PressurePlateBlock.class, FenceGateBlock.class})
public class NotWaterloggableBlockMixin implements SimpleWaterloggedBlock {

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void onAppendProperties(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        if (BedrockoidConfig.blocksWaterloggability && !((StateDefinitionAccess) builder).getProperties().containsKey(BlockStateProperties.WATERLOGGED.getName()))
            builder.add(BlockStateProperties.WATERLOGGED);
    }
}