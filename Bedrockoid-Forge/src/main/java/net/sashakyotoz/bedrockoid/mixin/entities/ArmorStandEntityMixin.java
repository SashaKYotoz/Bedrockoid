package net.sashakyotoz.bedrockoid.mixin.entities;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ArmorStand.class)
public class ArmorStandEntityMixin {
    @WrapMethod(method = "isShowArms")
    private boolean showArmsByDefault(Operation<Boolean> original) {
        return original.call() || BedrockoidConfig.armorStandArms;
    }
}