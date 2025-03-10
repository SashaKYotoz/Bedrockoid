package net.sashakyotoz.bedrockoid.mixin.client;

import net.minecraft.client.render.entity.AbstractHorseEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.AbstractHorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorseEntityRenderer.class)
public class AbstractHorseEntityRendererMixin {
    @Inject(method = "scale(Lnet/minecraft/entity/passive/AbstractHorseEntity;Lnet/minecraft/client/util/math/MatrixStack;F)V", at = @At("TAIL"))
    private void handleSize(AbstractHorseEntity abstractHorseEntity, MatrixStack matrixStack, float f, CallbackInfo ci) {
//        if (abstractHorseEntity.isBaby()) {
//            float sizeToAge = 0.9f + Math.max(0.1f, MathHelper.lerp(abstractHorseEntity.getBreedingAge(),-24000,0));
//            sizeToAge = Math.min(1.1f, sizeToAge);
//            matrixStack.scale(sizeToAge, sizeToAge, sizeToAge);
//        }
    }
}