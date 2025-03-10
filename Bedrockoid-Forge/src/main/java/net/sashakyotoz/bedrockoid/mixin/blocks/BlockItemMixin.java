package net.sashakyotoz.bedrockoid.mixin.blocks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @WrapOperation(
            method = "place",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getSoundType(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/level/block/SoundType;"
            )
    )
    public SoundType place(BlockState instance, LevelReader reader, BlockPos pos, Entity entity, Operation<SoundType> original) {
        return (BlockUtils.isSnowlogged(instance) && BedrockoidConfig.snowlogging) ?
                original.call(BlockUtils.getSnowEquivalent(instance),reader,pos,entity) : original.call(instance,reader,pos,entity);
    }

//    @Inject(method = "getPlaceSound(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/sounds/SoundEvent;", at = @At("HEAD"), cancellable = true)
//    public void getPlaceSound(BlockState state, Level world, BlockPos pos, Player entity, CallbackInfoReturnable<SoundEvent> cir) {
//        if (BlockUtils.isSnowlogged(state) && ModsUtils.isSnowloggingNotOverrided()) {
//            cir.setReturnValue(BlockUtils.getSnowEquivalent(state).getSoundType().getPlaceSound());
//        }
//    }
}