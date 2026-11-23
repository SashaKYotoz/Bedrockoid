package net.sashakyotoz.bedrockoid.mixin.worldgen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @WrapOperation(
            method = "tickPrecipitation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;is(Ljava/lang/Object;)Z",
                    ordinal = 0
            )
    )
    private boolean bedrockoid$checkSnowlogging(
            BlockState instance, Object block, Operation<Boolean> original,
            @Share("bedrockoidRunSnowlogging") LocalBooleanRef runSnowlogging, @Share("bedrockoidSnowloggedLayers") LocalIntRef snowloggedLayers
    ) {
        int layers = 0;
        boolean snowlog = BedrockoidConfig.snowlogging && BlockUtils.canSnowlog(instance) && (layers = instance.getValue(BlockUtils.LAYERS)) < 8;
        runSnowlogging.set(snowlog);
        snowloggedLayers.set(layers);
        return snowlog || original.call(instance, block);
    }

    @WrapOperation(
            method = "tickPrecipitation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;",
                    ordinal = 0
            )
    )
    private Comparable<?> bedrockoid$readSnowLayers(
            BlockState instance, Property<?> property, Operation<Comparable<?>> original,
            @Share("bedrockoidRunSnowlogging") LocalBooleanRef runSnowlogging, @Share("bedrockoidSnowloggedLayers") LocalIntRef snowloggedLayers
    ) {
        return runSnowlogging.get() ? snowloggedLayers.get() : original.call(instance, property);
    }

    @WrapOperation(
            method = "tickPrecipitation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;",
                    ordinal = 0
            )
    )
    private Object bedrockoid$writeSnowLayers(
            BlockState instance, Property<?> property, Comparable<?> comparable, Operation<Object> original,
            @Share("bedrockoidRunSnowlogging") LocalBooleanRef runSnowlogging, @Share("bedrockoidSnowloggedLayers") LocalIntRef snowloggedLayers
    ) {
        return original.call(instance, runSnowlogging.get() ? BlockUtils.LAYERS : property, comparable);
    }
}
