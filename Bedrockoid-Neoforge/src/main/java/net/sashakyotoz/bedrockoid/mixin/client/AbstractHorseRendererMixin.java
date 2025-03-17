package net.sashakyotoz.bedrockoid.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorseRenderer.class)
public class AbstractHorseRendererMixin {
    @Inject(method = "scale(Lnet/minecraft/world/entity/animal/horse/AbstractHorse;Lcom/mojang/blaze3d/vertex/PoseStack;F)V", at = @At("TAIL"))
    private void handleSize(AbstractHorse abstractHorse, PoseStack matrixStack, float f, CallbackInfo ci) {
//        if (abstractHorse.isBaby()) {
//            float sizeToAge = 0.9f + Math.max(0.1f, Mth.lerp(abstractHorse.getAge(),-24000,0));
//            sizeToAge = Math.min(1.5f, sizeToAge);
//            matrixStack.scale(sizeToAge, sizeToAge, sizeToAge);
//        }
    }
}