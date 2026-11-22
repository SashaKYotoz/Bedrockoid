package net.sashakyotoz.bedrockoid.mixin;

import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains(".client.sodium."))
            return isLoaded("sodium");
        if (mixinClassName.endsWith(".PlantsMixin"))
            return !isLoaded("snowrealmagic") && !isLoaded("wilderwild");
        if (mixinClassName.endsWith(".FlowerBlockMixin"))
            return !isLoaded("bedrockify");
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    private static boolean isLoaded(String modId) {
        LoadingModList modList = LoadingModList.get();
        return modList != null && modList.getModFileById(modId) != null;
    }
}
