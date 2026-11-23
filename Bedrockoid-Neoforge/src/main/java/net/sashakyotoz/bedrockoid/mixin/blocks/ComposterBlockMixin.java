package net.sashakyotoz.bedrockoid.mixin.blocks;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ComposterBlock.class)
public abstract class ComposterBlockMixin {
    @Shadow
    @Final
    private static VoxelShape[] SHAPES;

    @Shadow
    @Final
    public static IntegerProperty LEVEL;

    @WrapMethod(method = "getCollisionShape")
    private VoxelShape bedrockoid$fixCollision(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context, Operation<VoxelShape> original) {
        if (BedrockoidConfig.composterCollisionFix)
            return SHAPES[state.getValue(LEVEL)];
        return original.call(state, level, pos, context);
    }

    @Inject(method = "addItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/LevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static void bedrockoid$liftEntity(Entity user, BlockState state, LevelAccessor level, BlockPos pos, ItemStack stack, CallbackInfoReturnable<BlockState> cir) {
        if (user != null && user.getOnPos().equals(pos) && BedrockoidConfig.composterCollisionFix) {
            user.setDeltaMovement(0, 0.15f, 0);
            user.hurtMarked = true;
        }
    }
}
