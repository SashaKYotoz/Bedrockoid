package net.sashakyotoz.bedrockoid.mixin.blocks;

import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(value = {LeveledCauldronBlock.class, AnvilBlock.class, BedBlock.class, GrindstoneBlock.class, StonecutterBlock.class,
        LecternBlock.class, HopperBlock.class, BrewingStandBlock.class, BellBlock.class, PressurePlateBlock.class, FenceGateBlock.class})
public class NotWaterloggableBlockMixin implements Waterloggable {

    @Unique
    private static final Set<Class<?>> bedrockoid$WATERLOG_TARGETS = Set.of(
            LeveledCauldronBlock.class, AnvilBlock.class, BedBlock.class, GrindstoneBlock.class, StonecutterBlock.class,
            LecternBlock.class, HopperBlock.class, BrewingStandBlock.class, BellBlock.class, PressurePlateBlock.class, FenceGateBlock.class
    );

    @Inject(method = "appendProperties", at = @At("HEAD"))
    private void onAppendProperties(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
        if (!BedrockoidConfig.blocksWaterloggability) return;
        if (((StateDefinitionAccess) builder).getProperties().containsKey(Properties.WATERLOGGED.getName())) return;

        // When a subclass of a targeted block calls super.appendProperties(),
        // this mixin fires and adds WATERLOGGED. If the subclass also implements
        // Waterloggable, it will try to add WATERLOGGED again after super returns,
        // causing a "duplicate property" crash.
        // Skip for such subclasses and let them handle registration themselves.
        Block self = (Block) (Object) this;
        Class<?> cls = self.getClass();
        if (!bedrockoid$WATERLOG_TARGETS.contains(cls)) {
            for (Class<?> iface : cls.getInterfaces()) {
                if (iface == Waterloggable.class) return;
            }
        }

        builder.add(Properties.WATERLOGGED);
    }
}
