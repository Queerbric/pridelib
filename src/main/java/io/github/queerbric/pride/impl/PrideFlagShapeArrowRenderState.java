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
		int x, float cy, float s, float advanceRatio, int color,
		@Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {
	public PrideFlagShapeArrowRenderState(
			RenderPipeline pipeline, TextureSetup textureSetup,
			Matrix3x2f pose,
			int x, float cy, float s, float advanceRatio, int color,
			@Nullable ScreenRectangle scissorArea
	) {
		this(
				pipeline, textureSetup,
				pose,
				x, cy, s, advanceRatio, color,
				scissorArea, getBounds(x, cy, s, pose, scissorArea)
		);
	}

	@Override
	public void buildVertices(VertexConsumer vertexConsumer) {
		vertexConsumer.addVertexWith2DPose(this.pose(), this.x, this.cy + this.s).color(this.color);
		vertexConsumer.addVertexWith2DPose(this.pose(), this.x + (this.s * this.advanceRatio), this.cy).color(this.color);
		vertexConsumer.addVertexWith2DPose(this.pose(), this.x, this.cy - this.s).color(this.color);
		// Dirty 4th vertex as GUI only accepts quads.
		vertexConsumer.addVertexWith2DPose(this.pose(), this.x, this.cy).color(this.color);
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
