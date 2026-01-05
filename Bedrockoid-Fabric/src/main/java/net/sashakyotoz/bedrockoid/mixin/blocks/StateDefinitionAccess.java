package net.sashakyotoz.bedrockoid.mixin.blocks;

import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(StateManager.Builder.class)
public interface StateDefinitionAccess {
    @Accessor("namedProperties")
    Map<String, Property<?>> getProperties();
}