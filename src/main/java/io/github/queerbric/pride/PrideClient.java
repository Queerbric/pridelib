package io.github.queerbric.pride;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.io.ResourceType;

@Environment(EnvType.CLIENT)
public class PrideClient implements ClientModInitializer {
	public static final ShaderProgram FLAG_SHAPE_CIRCLE_SHADER = new ShaderProgram(
			Identifier.of("pride", "core/flag_shape_circle"),
			DefaultVertexFormat.POSITION_COLOR,
			ShaderDefines.EMPTY
	);
	public static final RenderType FLAG_SHAPE_TRIANGLE_RENDER_TYPE = RenderType.create(
			"pride_triangle",
			DefaultVertexFormat.POSITION_COLOR,
			VertexFormat.Mode.TRIANGLES,
			786432,
			RenderType.CompositeState.builder()
					.setShaderState(new RenderStateShard.ShaderStateShard(CoreShaders.POSITION_COLOR))
					.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
					.setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
					.createCompositeState(false)
	);
	public static final RenderType FLAG_SHAPE_CIRCLE_RENDER_TYPE = RenderType.create(
			"pride_flag_shape_circle",
			DefaultVertexFormat.POSITION_COLOR,
			VertexFormat.Mode.QUADS,
			786432,
			RenderType.CompositeState.builder()
					.setShaderState(new RenderStateShard.ShaderStateShard(FLAG_SHAPE_CIRCLE_SHADER))
					.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
					.setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
					.createCompositeState(false)
	);

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new PrideLoader());
		CoreShaders.getProgramsToPreload().add(FLAG_SHAPE_CIRCLE_SHADER);
	}
}
