package net.sashakyotoz.bedrockoid.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.effects.ReplaceDisk;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.phys.Vec3;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ReplaceDisk.class)
public abstract class ReplaceDiskMixin {
    @WrapMethod(method = "apply(Lnet/minecraft/server/level/ServerLevel;ILnet/minecraft/world/item/enchantment/EnchantedItemInUse;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;)V")
    private void bedrockoid$squareFrostWalker(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin, Operation<Void> original) {
        ReplaceDisk self = (ReplaceDisk) (Object) this;
        BlockPos center = BlockPos.containing(origin).offset(self.offset());
        RandomSource random = entity.getRandom();
        if (!BedrockoidConfig.frostwalkerBoost || !(self.blockState() instanceof SimpleStateProvider provider) || !provider.getState(random, center).is(Blocks.FROSTED_ICE)) {
            original.call(level, enchantmentLevel, item, entity, origin);
            return;
        }
        int radius = (int) self.radius().calculate(enchantmentLevel);
        int height = (int) self.height().calculate(enchantmentLevel);
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, 0, -radius), center.offset(radius, Math.max(height - 1, 0), radius))) {
            if (!self.predicate().map(predicate -> predicate.test(level, pos)).orElse(true))
                continue;
            BlockState state = provider.getState(random, pos);
            if (level.setBlockAndUpdate(pos, state))
                self.triggerGameEvent().ifPresent(event -> level.gameEvent(entity, event, pos));
        }
    }
}
