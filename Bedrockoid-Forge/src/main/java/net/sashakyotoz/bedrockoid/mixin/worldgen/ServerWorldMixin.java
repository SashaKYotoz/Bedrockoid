package net.sashakyotoz.bedrockoid.mixin.worldgen;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.common.utils.BlockUtils;
import net.sashakyotoz.bedrockoid.common.utils.ModsUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerLevel.class)
public class ServerWorldMixin {
    @WrapOperation(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/world/level/block/Block;)Z", ordinal = 1))
    public boolean tickPrecipitationA(
            BlockState instance, Block block, Operation<Boolean> original,
            @Share("bedrockoidRunSnowlogging") LocalBooleanRef runSnowlogging, @Share("bedrockoidSnowloggedLayers") LocalIntRef snowloggedLayers
    ) {
        int layers = 0;
        runSnowlogging.set(BlockUtils.canSnowlog(instance) && (layers = instance.getValue(BlockUtils.LAYERS)) < 8 && ModsUtils.isSnowloggingNotOverrided() && BedrockoidConfig.snowlogging);
        snowloggedLayers.set(layers);
        return runSnowlogging.get() || original.call(instance, block);
    }

    @WrapOperation(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getValue(Lnet/minecraft/world/level/block/state/properties/Property;)Ljava/lang/Comparable;", ordinal = 0)
    )
    public Comparable<?> wilderWild$tickPrecipitationB(
            BlockState instance, Property property, Operation<Comparable> original, @Share("bedrockoidRunSnowlogging") LocalBooleanRef runSnowlogging, @Share("bedrockoidSnowloggedLayers") LocalIntRef snowloggedLayers
    ) {
        return runSnowlogging.get() ? snowloggedLayers.get() : original.call(instance, property);
    }

    @WrapOperation(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;setValue(Lnet/minecraft/world/level/block/state/properties/Property;Ljava/lang/Comparable;)Ljava/lang/Object;", ordinal = 0)
    )
    public Object wilderWild$tickPrecipitationC(
            BlockState instance, Property<?> property, Comparable<?> comparable, Operation<Object> original,
            @Share("bedrockoidRunSnowlogging") LocalBooleanRef runSnowlogging, @Share("bedrockoidSnowloggedLayers") LocalIntRef snowloggedLayers
    ) {
        return original.call(instance, runSnowlogging.get() ? BlockUtils.LAYERS : property, comparable);
    }
}