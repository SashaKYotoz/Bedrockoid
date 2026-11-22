package net.sashakyotoz.bedrockoid.common.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.sashakyotoz.bedrockoid.Bedrockoid;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ReachPlacementUtils {
    public static final ReachPlacementUtils INSTANCE = new ReachPlacementUtils();
    public static final ResourceLocation PLACEMENT_ICON = Bedrockoid.makeID("textures/gui/reach_around_icon.png");
    private static final double REACH = 4.5D;

    public void renderIndicator(GuiGraphics graphics) {
        if (canReachAround())
            graphics.blit(PLACEMENT_ICON, (graphics.guiWidth() / 2) - 8, (graphics.guiHeight() / 2) - 8, 0, 0, 15, 15, 15, 15);
    }

    public boolean canReachAround() {
        if (!BedrockoidConfig.reachAroundPlacement || ModsUtils.isBedrockifyIn())
            return false;
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.level == null || client.hitResult == null)
            return false;
        if (client.hitResult.getType() != HitResult.Type.MISS)
            return false;
        LocalPlayer player = client.player;
        if (!player.isCrouching() || !player.onGround())
            return false;
        if (player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty())
            return false;
        if (!client.level.getBlockState(getFacingSteppingBlockPos(player)).canBeReplaced())
            return false;
        return getRaycastIntersection(player).isPresent();
    }

    public static BlockPos getFacingSteppingBlockPos(@NotNull Entity player) {
        return player.getOnPos().relative(player.getDirection());
    }

    private Optional<Vec3> getRaycastIntersection(@NotNull LocalPlayer player) {
        Vec3 rayStartPos = player.getEyePosition();
        Vec3 rayEndPos = player.getViewVector(1.0F).scale(REACH).add(rayStartPos);
        return new AABB(getFacingSteppingBlockPos(player)).clip(rayStartPos, rayEndPos);
    }
}
