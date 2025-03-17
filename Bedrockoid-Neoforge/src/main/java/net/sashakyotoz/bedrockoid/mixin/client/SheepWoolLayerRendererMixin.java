package net.sashakyotoz.bedrockoid.mixin.client;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.SheepFurLayer;
import net.minecraft.util.FastColor;
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
            int color;
            if (sheepEntity.hasCustomName() && "jeb_".equals(sheepEntity.getName().getString())) {
                int k = sheepEntity.tickCount / 25 + sheepEntity.getId();
                int l = DyeColor.values().length;
                int i1 = k % l;
                int j1 = (k + 1) % l;
                float f = ((float)(sheepEntity.tickCount % 25) + tickDelta) / 25.0F;
                int k1 = Sheep.getColor(DyeColor.byId(i1));
                int l1 = Sheep.getColor(DyeColor.byId(j1));
                color = FastColor.ARGB32.lerp(f, k1, l1);
            } else {
                color = Sheep.getColor(sheepEntity.getColor());
            }

            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.getParentModel(), Bedrockoid.makeID("textures/entity/sheep_sheared_fur.png"),
                    matrixStack, vertexConsumerProvider, light, sheepEntity, limbAngle, limbDistance, animationProgress, headYaw, headPitch, tickDelta, color);
        }
    }
}