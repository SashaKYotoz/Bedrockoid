package net.sashakyotoz.bedrockoid.mixin.blocks.waterlog;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import net.sashakyotoz.bedrockoid.mixin.blocks.StateDefinitionAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(value = {LayeredCauldronBlock.class, AnvilBlock.class, GrindstoneBlock.class, StonecutterBlock.class, BedBlock.class,
        LecternBlock.class, HopperBlock.class, BrewingStandBlock.class, PressurePlateBlock.class, FenceGateBlock.class, BellBlock.class})
public class NotWaterloggableBlockMixin implements SimpleWaterloggedBlock {

    @Unique
    private static final Set<Class<?>> bedrockoid$WATERLOG_TARGETS = Set.of(
            LayeredCauldronBlock.class, AnvilBlock.class, GrindstoneBlock.class, StonecutterBlock.class, BellBlock.class,
            LecternBlock.class, HopperBlock.class, BrewingStandBlock.class, PressurePlateBlock.class, FenceGateBlock.class, BedBlock.class
    );

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void onAppendProperties(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        if (!BedrockoidConfig.blocksWaterloggability) return;
        if (((StateDefinitionAccess) builder).getProperties().containsKey(BlockStateProperties.WATERLOGGED.getName()))
            return;

        Block self = (Block) (Object) this;
        Class<?> cls = self.getClass();
        if (!bedrockoid$WATERLOG_TARGETS.contains(cls)) {
            for (Class<?> iface : cls.getInterfaces()) {
                if (iface == SimpleWaterloggedBlock.class) return;
            }
        }

        try {
            builder.add(BlockStateProperties.WATERLOGGED);
        } catch (IllegalArgumentException ignored) {}
    }
}