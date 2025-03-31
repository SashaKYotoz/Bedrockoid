package net.sashakyotoz.bedrockoid.mixin.worldgen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.world.biome.Biome;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//frozenblock team copyright
//stating changes: use of yarn mappings
//source code: https://github.com/FrozenBlock/WilderWild/blob/1.21.4/src/main/java/net/frozenblock/wilderwild/mixin/snowlogging/worldgen/BiomeMixin.java
@Mixin(Biome.class)
public class BiomeMixin {
    @WrapOperation(method = "canSetSnow",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"
            )
    )
    public boolean shouldSnow(BlockState instance, Block block, Operation<Boolean> original) {
        return original.call(instance, block) || (BlockUtils.canSnowlog(instance) && BedrockoidConfig.snowlogging);
    }
}