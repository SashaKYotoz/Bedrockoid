package net.sashakyotoz.bedrockoid.mixin.blocks;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
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
    private static VoxelShape[] SHAPES;

    @Shadow
    @Final
    public static IntegerProperty LEVEL;

    @Shadow
    private static void add(float p_51921_, ItemLike like) {

    }

    @WrapMethod(method = "getCollisionShape")
    private VoxelShape fixCollision(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context, Operation<VoxelShape> original) {
        if (BedrockoidConfig.composterCollisionFix)
            return SHAPES[state.getValue(LEVEL)];
        else
            return SHAPES[0];
    }

    @Inject(method = "addItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static void regulatePushOfEntity(Entity user, BlockState state, LevelAccessor accessor, BlockPos pos, ItemStack stack, CallbackInfoReturnable<BlockState> cir) {
        if (user != null && user.getOnPos().equals(pos) && BedrockoidConfig.composterCollisionFix) {
            user.setDeltaMovement(0, 0.15f, 0);
            user.hurtMarked = true;
        }
    }

    @Inject(method = "bootStrap", at = @At("TAIL"))
    private static void registerExtraCompostableItems(CallbackInfo ci) {
        add(0.15f, Blocks.GRASS_BLOCK.asItem());
    }
}