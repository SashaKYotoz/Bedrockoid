package net.sashakyotoz.bedrockoid.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import net.sashakyotoz.bedrockoid.common.utils.ReachPlacementUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin extends ReentrantBlockableEventLoop<Runnable> {
    public MinecraftClientMixin(String string) {
        super(string);
    }

    @Shadow
    public LocalPlayer player;

    @Shadow @Nullable public HitResult hitResult;

    @Inject(method = "startUseItem", at = @At("HEAD"))
    private void crosshairTargetHandling(CallbackInfo ci) {
        if (this.player == null || ModsUtils.isBedrockifyIn() || !BedrockoidConfig.reachAroundPlacement)
            return;
        if (ReachPlacementUtils.INSTANCE.canReachAround()) {
            final LocalPlayer player = this.player;
            final BlockPos targetPos = ReachPlacementUtils.getFacingSteppingBlockPos(player);
            this.hitResult = new BlockHitResult(new Vec3(targetPos.getX(), player.getY(), targetPos.getZ()), player.getDirection(), targetPos, false);
        }
    }
}