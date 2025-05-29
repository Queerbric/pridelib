package io.github.queerbric.pride;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.io.ResourceType;

@Environment(EnvType.CLIENT)
public final class PrideClient {
	public static final String NAMESPACE = "pride";

	public static final RenderPipeline FLAG_SHAPE_TRIANGLE_RENDER_PIPELINE = RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
			.withLocation(id("flag_shape/triangle"))
			.withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			.withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES)
			.build();
	public static final RenderPipeline FLAG_SHAPE_CIRCLE_PIPELINE = RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
			.withLocation(id("flag_shape/circle"))
			.withVertexShader(id("core/flag_shape_circle"))
			.withFragmentShader(id("core/flag_shape_circle"))
			.withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
			.withVertexFormat(
					VertexFormat.builder()
							.add("position", VertexFormatElement.POSITION)
							.add("color", VertexFormatElement.COLOR)
							.add("center_pos_in", VertexFormatElement.UV0)
							.add("radius_in", VertexFormatElement.UV2)
							.build(),
					VertexFormat.Mode.QUADS
			)
			.build();

	public static Identifier id(String path) {
		return Identifier.of(NAMESPACE, path);
	}

	public static void init() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new PrideLoader());

		RenderPipelines.register(FLAG_SHAPE_TRIANGLE_RENDER_PIPELINE);
		RenderPipelines.register(FLAG_SHAPE_CIRCLE_PIPELINE);
	}
}
