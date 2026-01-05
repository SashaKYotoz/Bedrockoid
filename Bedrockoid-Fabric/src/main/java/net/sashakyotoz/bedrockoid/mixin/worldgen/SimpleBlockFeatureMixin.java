package net.sashakyotoz.bedrockoid.mixin.worldgen;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.SimpleBlockFeature;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SimpleBlockFeature.class)
public class SimpleBlockFeatureMixin {
    @Inject(method = "generate", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/TallPlantBlock;placeAt(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;I)V"), cancellable = true)
    private void fixDoublePlantPlacement(FeatureContext<SimpleBlockFeatureConfig> context, CallbackInfoReturnable<Boolean> cir) {
        StructureWorldAccess world = context.getWorld();
        BlockPos pos = context.getOrigin();
        BlockState blockstate = context.getConfig().toPlace().get(context.getRandom(), pos);
        if (world.getBiome(pos).value().hasPrecipitation()
                && world.getBiome(pos).value().getTemperature() < 0.1f
                && blockstate.isOf(Blocks.LARGE_FERN) && BedrockoidConfig.snowlogging)
            cir.setReturnValue(false);
    }
}