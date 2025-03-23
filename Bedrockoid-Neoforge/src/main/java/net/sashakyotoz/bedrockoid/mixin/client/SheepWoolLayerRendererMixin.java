package net.sashakyotoz.bedrockoid.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.SheepWoolLayer;
import net.minecraft.client.renderer.entity.state.SheepRenderState;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepWoolLayer.class)
public abstract class SheepWoolLayerRendererMixin extends RenderLayer<SheepRenderState, SheepModel> {
    public SheepWoolLayerRendererMixin(RenderLayerParent<SheepRenderState, SheepModel> context) {
        super(context);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/state/SheepRenderState;FF)V", at = @At("RETURN"))
    private void renderWoolColorAfterShearing(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, SheepRenderState state, float tickDelta, float animationProgress, CallbackInfo ci) {
        if (ModsUtils.isBedrockifyIn() || !BedrockoidConfig.sheepFurColorFix)
            return;
        else {
            int color;
            if (state.customName != null && "jeb_".equals(state.customName.getString())) {
                int k = Mth.floor(state.ageInTicks);
                int l = k / 25 + state.id;
                int m = DyeColor.values().length;
                int n = l % m;
                int o = (l + 1) % m;
                float h = ((float) (k % 25) + Mth.frac(state.ageInTicks)) / 25.0F;
                int p = Sheep.getColor(DyeColor.byId(n));
                int q = Sheep.getColor(DyeColor.byId(o));
                color = ARGB.lerp(h, p, q);
            } else {
                color = Sheep.getColor(state.woolColor);
            }

            coloredCutoutModelCopyLayerRender(this.getParentModel(), Bedrockoid.makeID("textures/entity/sheep_sheared_fur.png"),
                    matrixStack, vertexConsumerProvider, light, state, color);
        }
    }
}