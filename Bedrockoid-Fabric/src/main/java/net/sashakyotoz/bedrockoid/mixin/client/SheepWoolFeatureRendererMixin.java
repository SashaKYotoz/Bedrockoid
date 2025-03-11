package net.sashakyotoz.bedrockoid.mixin.client;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.SheepWoolFeatureRenderer;
import net.minecraft.client.render.entity.model.SheepEntityModel;
import net.minecraft.client.render.entity.state.SheepEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepWoolFeatureRenderer.class)
public abstract class SheepWoolFeatureRendererMixin extends FeatureRenderer<SheepEntityRenderState, SheepEntityModel> {
    public SheepWoolFeatureRendererMixin(FeatureRendererContext<SheepEntityRenderState, SheepEntityModel> context) {
        super(context);
    }

    @Inject(method = "render*", at = @At("RETURN"))
    private void renderWoolColorAfterShearing(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, SheepEntityRenderState sheepState, float tickDelta, float animationProgress, CallbackInfo ci) {
        if (ModsUtils.isBedrockifyIn() || !BedrockoidConfig.sheepFurColorFix)
            return;
        else {
            int color;
            if (sheepState.customName != null && "jeb_".equals(sheepState.customName.getString())) {
                int k = MathHelper.floor(sheepState.age);
                int l = k / 25 + sheepState.id;
                int m = DyeColor.values().length;
                int n = l % m;
                int o = (l + 1) % m;
                float h = ((float)(k % 25) + MathHelper.fractionalPart(sheepState.age)) / 25.0F;
                int p = SheepEntity.getRgbColor(DyeColor.byId(n));
                int q = SheepEntity.getRgbColor(DyeColor.byId(o));
                color = ColorHelper.lerp(h, p, q);
            } else {
                color = SheepEntity.getRgbColor(sheepState.color);
            }

            render(this.getContextModel(), Bedrockoid.makeID("textures/entity/sheep_sheared_fur.png"),
                    matrixStack, vertexConsumerProvider, light, sheepState, color);
        }
    }
}