package net.sashakyotoz.bedrockoid.common.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ReachPlacementUtils {
    public static final ReachPlacementUtils INSTANCE = new ReachPlacementUtils();
    public static final Identifier PLACEMENT_ICON = Bedrockoid.makeID("textures/gui/reach_around_icon.png");

    public void renderIndicator(DrawContext drawContext) {
        if (canReachAround())
            drawContext.drawTexture(PLACEMENT_ICON, (drawContext.getScaledWindowWidth() / 2) - 8, (drawContext.getScaledWindowHeight() / 2) - 8, 0, 0, 15, 15, 15, 15);
    }

    public boolean canReachAround() {
        if (!BedrockoidConfig.reachAroundPlacement || ModsUtils.isBedrockifyIn())
            return false;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null || client.crosshairTarget == null)
            return false;
        if (client.crosshairTarget.getType() != HitResult.Type.MISS)
            return false;
        ClientPlayerEntity player = client.player;
        if (!player.isSneaking() || !player.isOnGround())
            return false;
        if (player.getMainHandStack().isEmpty() && player.getOffHandStack().isEmpty())
            return false;
        if (!client.world.getBlockState(getFacingSteppingBlockPos(player)).isReplaceable())
            return false;
        return getRaycastIntersection(player).isPresent();
    }

    public static BlockPos getFacingSteppingBlockPos(@NotNull Entity player) {
        return player.getSteppingPos().offset(player.getHorizontalFacing());
    }

    private Optional<Vec3d> getRaycastIntersection(@NotNull ClientPlayerEntity player) {
        Vec3d rayStartPos = player.getEyePos();
        Vec3d rayEndPos = player.getRotationVec(1.0F).multiply(player.getBlockInteractionRange()).add(rayStartPos);
        return new Box(getFacingSteppingBlockPos(player)).raycast(rayStartPos, rayEndPos);
    }
}
