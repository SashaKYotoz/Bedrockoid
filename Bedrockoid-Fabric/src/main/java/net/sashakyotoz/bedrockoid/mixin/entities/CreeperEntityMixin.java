package net.sashakyotoz.bedrockoid.mixin.entities;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.entity.mob.CreeperEntity;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin {
    @Shadow
    public abstract boolean shouldRenderOverlay();

    @Shadow
    private int headsDropped;

    @WrapMethod(method = "shouldDropHead")
    private boolean shouldDropHead(Operation<Boolean> original) {
        if (BedrockoidConfig.chargedCreeperDropsEveryHead)
            return this.shouldRenderOverlay();

        return this.shouldRenderOverlay() && this.headsDropped < BedrockoidConfig.maximumHeadsAmountCreeperDrop;
    }
}