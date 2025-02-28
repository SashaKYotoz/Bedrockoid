package net.sashakyotoz.bedrockoid.mixin.blocks;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
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
    private static void registerCompostableItem(float levelIncreaseChance, ItemConvertible item) {}

    @WrapMethod(method = "getCollisionShape")
    private VoxelShape fixCollision(BlockState state, BlockView world, BlockPos pos, ShapeContext context, Operation<VoxelShape> original) {
        return LEVEL_TO_COLLISION_SHAPE[state.get(LEVEL)];
    }

    @Inject(method = "onUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/ComposterBlock;addToComposter(Lnet/minecraft/entity/Entity;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/block/BlockState;"))
    private void regulatePushOfEntity(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (player.getBlockPos().equals(pos))
            Block.pushEntitiesUpBeforeBlockChange(state, world.getBlockState(pos.up()), world, pos);
    }
    @Inject(method = "registerDefaultCompostableItems",at = @At("TAIL"))
    private static void registerExtraCompostableItems(CallbackInfo ci){
        registerCompostableItem(0.1f, Blocks.GRASS_BLOCK);
    }
}