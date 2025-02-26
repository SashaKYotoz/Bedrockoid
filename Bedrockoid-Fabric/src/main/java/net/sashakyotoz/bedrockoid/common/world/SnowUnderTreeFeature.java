package net.sashakyotoz.bedrockoid.common.world;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.SnowyBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.sashakyotoz.bedrockoid.common.snow.BedrockSnowManager;

// Code used below is port of SnowUnderTreesFeature from SnowUnderTrees to fabric
// goes under Copyright (c) 2022 bl4ckscor3
public class SnowUnderTreeFeature extends Feature<DefaultFeatureConfig> {

    public static final Feature<DefaultFeatureConfig> INSTANCE = new SnowUnderTreeFeature(DefaultFeatureConfig.CODEC);

    public SnowUnderTreeFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        BlockPos pos = context.getOrigin();
        StructureWorldAccess world = context.getWorld();
        BlockPos.Mutable mPos = new BlockPos.Mutable();

        for (int xi = 0; xi < 16; xi++) {
            for (int zi = 0; zi < 16; zi++) {
                int x = pos.getX() + xi;
                int z = pos.getZ() + zi;

                mPos.set(x, world.getTopY(Heightmap.Type.MOTION_BLOCKING, x, z) - 1, z);

                if (world.getBlockState(mPos).isIn(BlockTags.LEAVES)) {
                    mPos.set(x, world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z), z);

                    if (BedrockSnowManager.placeSnow(world, mPos)) {
                        BlockState stateBelow;

                        mPos.move(Direction.DOWN);
                        stateBelow = world.getBlockState(mPos);

                        if (stateBelow.contains(SnowyBlock.SNOWY))
                            world.setBlockState(mPos, stateBelow.with(SnowyBlock.SNOWY, true), 2);
                    }
                }
            }
        }
        return true;
    }
}