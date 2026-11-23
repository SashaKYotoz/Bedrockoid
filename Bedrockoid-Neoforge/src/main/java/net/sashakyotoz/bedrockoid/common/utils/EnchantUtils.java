package net.sashakyotoz.bedrockoid.common.utils;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.Optional;

public final class EnchantUtils {
    private EnchantUtils() {
    }

    public static boolean hasFireAspect(Level level, ItemStack stack) {
        if (stack.isEmpty())
            return false;
        Optional<Holder.Reference<Enchantment>> fireAspect = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).get(Enchantments.FIRE_ASPECT);
        return fireAspect.isPresent() && EnchantmentHelper.getItemEnchantmentLevel(fireAspect.get(), stack) > 0;
    }
}
