package net.sashakyotoz.bedrockoid.mixin.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepWoolFeatureRenderer.class)
public abstract class SheepWoolFeatureRendererMixin extends FeatureRenderer<SheepEntity, SheepEntityModel<SheepEntity>> {
    public SheepWoolFeatureRendererMixin(FeatureRendererContext<SheepEntity, SheepEntityModel<SheepEntity>> context) {
        super(context);
    }

    @Inject(method = "render*", at = @At("RETURN"))
    private void bedrockify$renderWoolColorAfterShearing(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, SheepEntity sheepEntity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        if (ModsUtils.isBedrockifyIn())
            return;
        else {
            float s;
            float t;
            float u;
            if (sheepEntity.hasCustomName() && "jeb_".equals(sheepEntity.getName().getString())) {
                int n = sheepEntity.age / 25 + sheepEntity.getId();
                int o = DyeColor.values().length;
                int p = n % o;
                int q = (n + 1) % o;
                float r = ((float) (sheepEntity.age % 25) + tickDelta) / 25.0F;
                float[] fs = SheepEntity.getRgbColor(DyeColor.byId(p));
                float[] gs = SheepEntity.getRgbColor(DyeColor.byId(q));
                s = fs[0] * (1.0F - r) + gs[0] * r;
                t = fs[1] * (1.0F - r) + gs[1] * r;
                u = fs[2] * (1.0F - r) + gs[2] * r;
            } else {
                float[] hs = SheepEntity.getRgbColor(sheepEntity.getColor());
                s = hs[0];
                t = hs[1];
                u = hs[2];
            }

            render(this.getContextModel(), this.getContextModel(), Bedrockoid.makeID("textures/entity/sheep_sheared_fur.png"),
                    matrixStack, vertexConsumerProvider, light, sheepEntity, limbAngle, limbDistance, animationProgress, headYaw, headPitch, tickDelta, s, t, u);
        }
    }
}