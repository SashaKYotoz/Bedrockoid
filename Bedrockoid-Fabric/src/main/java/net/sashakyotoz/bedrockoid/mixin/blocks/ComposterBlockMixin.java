package net.sashakyotoz.bedrockoid.mixin.blocks;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ComposterBlock.class)
public abstract class ComposterBlockMixin {
    @Shadow
    @Final
    private static VoxelShape[] LEVEL_TO_COLLISION_SHAPE;

    @Shadow
    @Final
    public static IntProperty LEVEL;

    @Shadow
    private static void registerCompostableItem(float levelIncreaseChance, ItemConvertible item) {
    }

    @WrapMethod(method = "getCollisionShape")
    private VoxelShape fixCollision(BlockState state, BlockView world, BlockPos pos, ShapeContext context, Operation<VoxelShape> original) {
        if (BedrockoidConfig.composterCollisionFix)
            return LEVEL_TO_COLLISION_SHAPE[state.get(LEVEL)];
        else
            return LEVEL_TO_COLLISION_SHAPE[0];
    }

    @Inject(method = "addToComposter", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
    private static void regulatePushOfEntity(Entity user, BlockState state, WorldAccess world, BlockPos pos, ItemStack stack, CallbackInfoReturnable<BlockState> cir) {
        if (user != null && user.getBlockPos().equals(pos) && BedrockoidConfig.composterCollisionFix) {
            user.setVelocity(0, 0.15f, 0);
            user.velocityModified = true;
        }
    }

    @Inject(method = "registerDefaultCompostableItems", at = @At("TAIL"))
    private static void registerExtraCompostableItems(CallbackInfo ci) {
        registerCompostableItem(0.15f, Blocks.GRASS_BLOCK);
    }
}