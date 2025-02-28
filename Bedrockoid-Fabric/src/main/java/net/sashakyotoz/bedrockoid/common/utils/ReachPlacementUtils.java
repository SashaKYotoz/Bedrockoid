package net.sashakyotoz.bedrockoid.common.utils;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.MapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Handles rendering and positioning for placement of block
 *
 * @author juancarloscp52
 */
public class ReachPlacementUtils {
    public static final ReachPlacementUtils INSTANCE = new ReachPlacementUtils();
    private final MinecraftClient client = MinecraftClient.getInstance();

    public void renderIndicator(DrawContext drawContext) {
        if (client.isInSingleplayer() && this.canReachAround())
            drawContext.fill((client.getWindow().getScaledWidth() / 2) - 5, (client.getWindow().getScaledHeight() / 2) + 5, (client.getWindow().getScaledWidth() / 2) + 4, (client.getWindow().getScaledHeight() / 2) + 6, 0xFFFFFF);
    }

    public boolean canReachAround() {
        if (client.player == null || client.world == null || client.crosshairTarget == null)
            return false;

        if (!client.crosshairTarget.getType().equals(HitResult.Type.MISS))
            return false;

        final ClientPlayerEntity player = client.player;
        final BlockPos targetPos = getFacingSteppingBlockPos(player);

        if (!player.isSneaking())
            return false;
        if (!player.isOnGround())
            return false;
        if (!client.world.getBlockState(targetPos).isReplaceable())
            return false;

        return getRaycastIntersection(player).isPresent();
    }
    public static BlockPos getFacingSteppingBlockPos(@NotNull Entity player) {
        return player.getSteppingPos().offset(player.getHorizontalFacing());
    }

    /**
     * Draws a vector from the player's eyes to the end of the reach distance, in the direction the player is facing. We can use this to check if the block is valid for placement.
     * @return The position of the intersection between the raycast and the surface of the target block.
     * @author axialeaa
     */
    private Optional<Vec3d> getRaycastIntersection(@NotNull ClientPlayerEntity player) {
        Vec3d rayStartPos = player.getEyePos();
        Vec3d rayEndPos = player.getRotationVec(1.0F).multiply(4.5f).add(rayStartPos);

        return new Box(getFacingSteppingBlockPos(player)).raycast(rayStartPos, rayEndPos);
    }
}