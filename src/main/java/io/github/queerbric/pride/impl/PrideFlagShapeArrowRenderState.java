package io.github.queerbric.pride.impl;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

@Environment(EnvType.CLIENT)
public record PrideFlagShapeArrowRenderState(
		RenderPipeline pipeline, TextureSetup textureSetup,
		Matrix3x2f pose,
		int x, float cy, float s, int color,
		@Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {
	public PrideFlagShapeArrowRenderState(
			RenderPipeline pipeline, TextureSetup textureSetup,
			Matrix3x2f pose,
			int x, float cy, float s, int color,
			@Nullable ScreenRectangle scissorArea
	) {
		this(
				pipeline, textureSetup,
				pose,
				x, cy, s, color,
				scissorArea, getBounds(x, cy, s, pose, scissorArea)
		);
	}

	@Override
	public void buildVertices(VertexConsumer vertexConsumer, float z) {
		vertexConsumer.addVertexWith2DPose(this.pose(), this.x, this.cy + this.s, z).color(this.color);
		// yes, 1.5. the demisexual flag triangle appears to not be equilateral?
		vertexConsumer.addVertexWith2DPose(this.pose(), this.x + (this.s * 1.5f), this.cy, z).color(this.color);
		vertexConsumer.addVertexWith2DPose(this.pose(), this.x, this.cy - this.s, z).color(this.color);
		// Dirty 4th vertex as GUI only accepts quads.
		vertexConsumer.addVertexWith2DPose(this.pose(), this.x, this.cy, z).color(this.color);
	}

	@Nullable
	private static ScreenRectangle getBounds(
			int x, float cy, float s, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea
	) {
		var defaultBounds = new ScreenRectangle(x, (int) (cy - s), (int) (s * 1.5f), (int) (s * 2))
				.transformMaxBounds(pose);
		return scissorArea != null ? scissorArea.intersection(defaultBounds) : defaultBounds;
	}
}
