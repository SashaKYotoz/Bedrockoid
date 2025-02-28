package net.sashakyotoz.bedrockoid.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.thread.ReentrantThreadExecutor;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import net.sashakyotoz.bedrockoid.common.utils.ReachPlacementUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin extends ReentrantThreadExecutor<Runnable> {
    public MinecraftClientMixin(String string) {
        super(string);
    }

    @Shadow
    public ClientPlayerEntity player;
    @Shadow
    @Nullable
    public HitResult crosshairTarget;

    @Inject(method = "doItemUse", at = @At("HEAD"))
    private void crosshairTargetHandling(CallbackInfo ci) {
        if (this.player == null || ModsUtils.isBedrockifyIn())
            return;
        if (ReachPlacementUtils.INSTANCE.canReachAround()) {
            final ClientPlayerEntity player = this.player;
            final BlockPos targetPos = ReachPlacementUtils.getFacingSteppingBlockPos(player);
            this.crosshairTarget = new BlockHitResult(new Vec3d(targetPos.getX(), player.getY(), targetPos.getZ()), player.getHorizontalFacing(), targetPos, false);
        }
    }
}