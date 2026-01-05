package net.sashakyotoz.bedrockoid.mixin.blocks;

import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {LeveledCauldronBlock.class, AnvilBlock.class, BedBlock.class, GrindstoneBlock.class, StonecutterBlock.class,
        LecternBlock.class, HopperBlock.class, BrewingStandBlock.class, BellBlock.class, PressurePlateBlock.class, FenceGateBlock.class})
public class NotWaterloggableBlockMixin implements Waterloggable {
    @Inject(method = "appendProperties", at = @At("HEAD"))
    private void onAppendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        if (BedrockoidConfig.blocksWaterloggability && !((StateDefinitionAccess) builder).getProperties().containsKey(Properties.WATERLOGGED.getName()))
            builder.add(Properties.WATERLOGGED);
    }
}
