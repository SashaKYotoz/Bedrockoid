package net.sashakyotoz.bedrockoid.mixin.client;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.SheepFurLayer;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SheepFurLayer.class)
public abstract class SheepWoolLayerRendererMixin extends RenderLayer<Sheep, SheepModel<Sheep>> {
    public SheepWoolLayerRendererMixin(RenderLayerParent<Sheep, SheepModel<Sheep>> context) {
        super(context);
    }

    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/world/entity/animal/Sheep;FFFFFF)V", at = @At("RETURN"))
    private void renderWoolColorAfterShearing(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, Sheep sheepEntity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        if (ModsUtils.isBedrockifyIn() || !BedrockoidConfig.sheepFurColorFix)
            return;
        else {
            float s;
            float t;
            float u;
            if (sheepEntity.hasCustomName() && "jeb_".equals(sheepEntity.getName().getString())) {
                int n = sheepEntity.tickCount / 25 + sheepEntity.getId();
                int o = DyeColor.values().length;
                int p = n % o;
                int q = (n + 1) % o;
                float r = ((float) (sheepEntity.tickCount % 25) + tickDelta) / 25.0F;
                float[] fs = Sheep.getColorArray(DyeColor.byId(p));
                float[] gs = Sheep.getColorArray(DyeColor.byId(q));
                s = fs[0] * (1.0F - r) + gs[0] * r;
                t = fs[1] * (1.0F - r) + gs[1] * r;
                u = fs[2] * (1.0F - r) + gs[2] * r;
            } else {
                float[] hs = Sheep.getColorArray(sheepEntity.getColor());
                s = hs[0];
                t = hs[1];
                u = hs[2];
            }

            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.getParentModel(), Bedrockoid.makeID("textures/entity/sheep_sheared_fur.png"),
                    matrixStack, vertexConsumerProvider, light, sheepEntity, limbAngle, limbDistance, animationProgress, headYaw, headPitch, tickDelta, s, t, u);
        }
    }
}