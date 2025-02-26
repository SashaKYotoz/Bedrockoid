package net.sashakyotoz.bedrockoid.common;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import net.sashakyotoz.bedrockoid.Bedrockoid;

public class BlockUtils {
    public static final IntProperty LAYERS = IntProperty.of("snow_layers", 0, 8);
    public static MatrixStack matrices;

    public static void renderSnowOverlay(BlockPos pos, World world, int snowAmount) {
        BlockState snowState = Blocks.SNOW.getDefaultState().with(Properties.LAYERS, 3);
        BlockRenderManager renderManager = MinecraftClient.getInstance().getBlockRenderManager();
        BlockModelRenderer modelRenderer = renderManager.getModelRenderer();
        VertexConsumerProvider.Immediate provider = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        if (matrices != null && provider != null) {
            Bedrockoid.log(String.format("It seems to be called: %s", provider.getBuffer(RenderLayers.getBlockLayer(snowState)).toString()));
            matrices.push();
            matrices.translate(-0.5, 0.0, -0.5);
            modelRenderer.render(world, renderManager.getModel(snowState), snowState, pos, matrices,
                    provider.getBuffer(RenderLayers.getBlockLayer(snowState)),
                    false, world.random, 0,
                    OverlayTexture.DEFAULT_UV);
            matrices.pop();
        }
    }

    public static boolean haveLeavesToChangeColor(BlockState state, BlockRenderView world, BlockPos pos) {
        if (world != null && pos != null) {
            BlockState upperState = world.getBlockState(pos.up());
            return (state.getBlock() instanceof LeavesBlock && upperState.isOf(Blocks.SNOW))
                    || (upperState.getBlock() instanceof LeavesBlock && world.getBlockState(pos.up(2)).isOf(Blocks.SNOW));
        }
        return false;
    }

    public static boolean haveLeavesToSlightlyChangeColor(BlockState state, BlockRenderView world, BlockPos pos) {
        if (world != null && pos != null)
            return state.getBlock() instanceof LeavesBlock && world.getBiomeFabric(pos) != null
                    && world.getBiomeFabric(pos).value().getTemperature() < 0.15f
                    && world.getBiomeFabric(pos).value().hasPrecipitation();
        return false;
    }

    //shapes
    public static final VoxelShape[] SNOW_LAYERS_TO_SHAPE = new VoxelShape[]{
            VoxelShapes.empty(),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
    };
}