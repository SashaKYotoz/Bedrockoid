package net.sashakyotoz.bedrockoid.common.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
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
    public static final Identifier PLACEMENT_ICON = Bedrockoid.makeID("reach_around_icon");
    private static final int ICON_SIZE = 15;

    public void renderIndicator(GuiGraphicsExtractor graphics) {
        if (canReachAround())
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PLACEMENT_ICON, (graphics.guiWidth() - ICON_SIZE) / 2, (graphics.guiHeight() - ICON_SIZE) / 2, ICON_SIZE, ICON_SIZE);
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
        Vec3 rayEndPos = player.getViewVector(1.0F).scale(player.blockInteractionRange()).add(rayStartPos);
        return new AABB(getFacingSteppingBlockPos(player)).clip(rayStartPos, rayEndPos);
    }
}
