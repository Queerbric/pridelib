package io.github.queerbric.pride;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.queerbric.pride.impl.PrideFlagShapeCircleRenderType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.io.ResourceType;
import org.joml.Vector2fc;

@Environment(EnvType.CLIENT)
public final class PrideClient {
	public static final String NAMESPACE = "pride";

	public static final RenderPipeline FLAG_SHAPE_TRIANGLE_RENDER_PIPELINE = RenderPipeline.builder(RenderPipelines.MATRICES_COLOR_SNIPPET)
			.withLocation(id("flag_shape/triangle"))
			.withVertexShader("core/gui")
			.withFragmentShader("core/gui")
			.withBlend(BlendFunction.TRANSLUCENT)
			.withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES)
			.build();
	public static final RenderPipeline FLAG_SHAPE_CIRCLE_PIPELINE = RenderPipeline.builder(RenderPipelines.MATRICES_COLOR_SNIPPET)
			.withLocation(id("flag_shape/circle"))
			.withVertexShader(id("core/flag_shape_circle"))
			.withFragmentShader(id("core/flag_shape_circle"))
			.withUniform("radius", UniformType.VEC2)
			.withUniform("center_pos", UniformType.VEC2)
			.withBlend(BlendFunction.TRANSLUCENT)
			.withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
			.build();
	public static final RenderType FLAG_SHAPE_TRIANGLE_RENDER_TYPE = RenderType.create(
			"pride_triangle",
			RenderType.SMALL_BUFFER_SIZE,
			FLAG_SHAPE_TRIANGLE_RENDER_PIPELINE,
			RenderType.CompositeState.builder().createCompositeState(false)
	);

	public static RenderType getFlagShapeCircleRenderType(Vector2fc centerPos, Vector2fc radius) {
		return new PrideFlagShapeCircleRenderType(
				"pride_flag_shape_circle",
				RenderType.SMALL_BUFFER_SIZE,
				false,
				false,
				FLAG_SHAPE_CIRCLE_PIPELINE,
				RenderType.CompositeState.builder()
						.createCompositeState(false),
				centerPos,
				radius
		);
	}

	public static Identifier id(String path) {
		return Identifier.of(NAMESPACE, path);
	}

	public static void init() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new PrideLoader());

		RenderPipelines.register(FLAG_SHAPE_TRIANGLE_RENDER_PIPELINE);
		RenderPipelines.register(FLAG_SHAPE_CIRCLE_PIPELINE);
	}
}
