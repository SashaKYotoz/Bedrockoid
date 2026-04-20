package net.sashakyotoz.bedrockoid.mixin.entities;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.monster.Creeper;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Creeper.class)
public abstract class CreeperMixin {
    @Shadow
    public abstract boolean isPowered();

    @Shadow
    private int droppedSkulls;

    @WrapMethod(method = "canDropMobsSkull")
    private boolean canDropMobsSkull(Operation<Boolean> original) {
        if (BedrockoidConfig.chargedCreeperDropsEveryHead)
            return this.isPowered();

        return this.isPowered() && this.droppedSkulls < BedrockoidConfig.maximumHeadsAmountCreeperDrop;
    }
}