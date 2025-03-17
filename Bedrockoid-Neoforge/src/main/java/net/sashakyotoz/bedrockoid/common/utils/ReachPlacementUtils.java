package net.sashakyotoz.bedrockoid.common.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ReachPlacementUtils {
    public static final ReachPlacementUtils INSTANCE = new ReachPlacementUtils();
    private final Minecraft client = Minecraft.getInstance();
    public static final ResourceLocation PLACEMENT_ICON = Bedrockoid.makeID("textures/gui/reach_around_icon.png");

    public void renderIndicator(GuiGraphics drawContext) {
        if (canReachAround())
            drawContext.blit(PLACEMENT_ICON, (drawContext.guiWidth() / 2) - 8, (drawContext.guiHeight() / 2) - 8, 0, 0, 15, 15, 15, 15);
    }

    public boolean canReachAround() {
        if (client.player == null || client.level == null || client.hitResult == null)
            return false;

        if (!client.hitResult.getType().equals(HitResult.Type.MISS))
            return false;

        final LocalPlayer player = client.player;
        final BlockPos targetPos = getFacingSteppingBlockPos(player);

        if (!player.isCrouching())
            return false;
        if (!player.onGround())
            return false;
        if (!client.level.getBlockState(targetPos).canBeReplaced())
            return false;

        return getRaycastIntersection(player).isPresent();
    }

    public static BlockPos getFacingSteppingBlockPos(@NotNull net.minecraft.world.entity.Entity player) {
        return player.getOnPos().relative(player.getDirection());
    }

    /**
     * Draws a vector from the player's eyes to the end of the reach distance, in the direction the player is facing. We can use this to check if the block is valid for placement.
     *
     * @return The position of the intersection between the raycast and the surface of the target block.
     * @author axialeaa
     */
    private Optional<Vec3> getRaycastIntersection(@NotNull LocalPlayer player) {
        Vec3 rayStartPos = player.getEyePosition();
        Vec3 rayEndPos = player.getViewVector(1.0F).scale(4.5f).add(rayStartPos);

        return new AABB(getFacingSteppingBlockPos(player)).clip(rayStartPos, rayEndPos);
    }
}