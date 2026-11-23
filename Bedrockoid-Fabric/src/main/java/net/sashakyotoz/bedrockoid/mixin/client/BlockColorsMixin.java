package net.sashakyotoz.bedrockoid.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.color.block.BlockColors;
import net.sashakyotoz.bedrockoid.client.SnowTints;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockColors.class)
public abstract class BlockColorsMixin {
    @ModifyReturnValue(method = "createDefault", at = @At("RETURN"))
    private static BlockColors bedrockoid$applySnowTints(BlockColors colors) {
        SnowTints.apply(colors, (sources, block) -> colors.register(sources, block));
        return colors;
    }
}
