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

@Mixin(value = {LayeredCauldronBlock.class, AnvilBlock.class, GrindstoneBlock.class, StonecutterBlock.class,
        LecternBlock.class, HopperBlock.class, BrewingStandBlock.class, PressurePlateBlock.class, FenceGateBlock.class})
public class NotWaterloggableBlockMixin implements SimpleWaterloggedBlock {

    @Unique
    private static final Set<Class<?>> bedrockoid$WATERLOG_TARGETS = Set.of(
            LayeredCauldronBlock.class, AnvilBlock.class, GrindstoneBlock.class, StonecutterBlock.class,
            LecternBlock.class, HopperBlock.class, BrewingStandBlock.class, PressurePlateBlock.class, FenceGateBlock.class
    );

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    private void onAppendProperties(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo ci) {
        if (!BedrockoidConfig.blocksWaterloggability) return;
        if (((StateDefinitionAccess) builder).getProperties().containsKey(BlockStateProperties.WATERLOGGED.getName())) return;

        // When a subclass of a targeted block (e.g. Dragon Survival's DragonPressurePlates)
        // calls super.createBlockStateDefinition(), this mixin fires and adds WATERLOGGED.
        // If the subclass also implements SimpleWaterloggedBlock, it will try to add
        // WATERLOGGED again after super returns, causing a "duplicate property" crash.
        // Skip for such subclasses and let them handle registration themselves.
        Block self = (Block) (Object) this;
        Class<?> cls = self.getClass();
        if (!bedrockoid$WATERLOG_TARGETS.contains(cls)) {
            for (Class<?> iface : cls.getInterfaces()) {
                if (iface == SimpleWaterloggedBlock.class) return;
            }
        }

        builder.add(BlockStateProperties.WATERLOGGED);
    }
}
