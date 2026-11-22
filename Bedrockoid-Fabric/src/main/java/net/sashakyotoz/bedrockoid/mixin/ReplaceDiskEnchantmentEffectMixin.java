package net.sashakyotoz.bedrockoid.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.entity.ReplaceDiskEnchantmentEffect;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.sashakyotoz.bedrockoid.BedrockoidConfig;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ReplaceDiskEnchantmentEffect.class)
public abstract class ReplaceDiskEnchantmentEffectMixin {
    @WrapMethod(method = "apply(Lnet/minecraft/server/world/ServerWorld;ILnet/minecraft/enchantment/EnchantmentEffectContext;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Vec3d;)V")
    private void bedrockoid$squareFrostWalker(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d origin, Operation<Void> original) {
        ReplaceDiskEnchantmentEffect self = (ReplaceDiskEnchantmentEffect) (Object) this;
        BlockPos center = BlockPos.ofFloored(origin).add(self.offset());
        Random random = user.getRandom();
        if (!BedrockoidConfig.frostwalkerBoost || !(self.blockState() instanceof SimpleBlockStateProvider provider) || !provider.get(random, center).isOf(Blocks.FROSTED_ICE)) {
            original.call(world, level, context, user, origin);
            return;
        }
        int radius = (int) self.radius().getValue(level);
        int height = (int) self.height().getValue(level);
        for (BlockPos pos : BlockPos.iterate(center.add(-radius, 0, -radius), center.add(radius, Math.max(height - 1, 0), radius))) {
            if (!self.predicate().map(predicate -> predicate.test(world, pos)).orElse(true))
                continue;
            BlockState state = provider.get(random, pos);
            BlockPos immutable = pos.toImmutable();
            if (world.setBlockState(immutable, state))
                self.triggerGameEvent().ifPresent(event -> world.emitGameEvent(user, event, immutable));
        }
    }
}
