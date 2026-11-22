package net.sashakyotoz.bedrockoid.mixin.blocks;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import net.sashakyotoz.bedrockoid.common.utils.StartupFlags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {SweetBerryBushBlock.class, SugarCaneBlock.class, SaplingBlock.class, PinkPetalsBlock.class, DoublePlantBlock.class})
public class PlantsMixin {
    @Inject(method = "createBlockStateDefinition", at = @At("HEAD"))
    private void bedrockoid$addSnowLayers(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        if (StartupFlags.SNOWLOGGING && !((StateDefinitionAccess) builder).getProperties().containsKey(BlockUtils.LAYERS.getName()))
            builder.add(BlockUtils.LAYERS);
    }
}
