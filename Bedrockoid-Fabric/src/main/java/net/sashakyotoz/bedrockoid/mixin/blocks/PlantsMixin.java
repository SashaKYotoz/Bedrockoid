package net.sashakyotoz.bedrockoid.mixin.blocks;

import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {SweetBerryBushBlock.class, SugarCaneBlock.class, SaplingBlock.class, FlowerbedBlock.class, TallPlantBlock.class})
public class PlantsMixin {
    @Inject(method = "appendProperties", at = @At("HEAD"))
    private void onAppendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        if (ModsUtils.isSnowloggingNotOverrided())
            builder.add(BlockUtils.LAYERS);
    }
}