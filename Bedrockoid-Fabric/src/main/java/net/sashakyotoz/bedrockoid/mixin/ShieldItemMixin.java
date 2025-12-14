package net.sashakyotoz.bedrockoid.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShieldItem.class)
public class ShieldItemMixin {
    @WrapMethod(method = "use")
    private TypedActionResult<ItemStack> useShieldOnlyWhenSneaking(World world, PlayerEntity user, Hand hand, Operation<TypedActionResult<ItemStack>> original) {
        if (BedrockoidConfig.shieldActivatesWhenSneaking && BedrockoidConfig.shieldActivatesOnlyWhenSneaking && !user.isSneaking())
            return TypedActionResult.fail(user.getStackInHand(hand));
        return original.call(world, user, hand);
    }
}