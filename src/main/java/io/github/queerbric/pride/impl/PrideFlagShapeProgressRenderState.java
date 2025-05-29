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
public record PrideFlagShapeProgressRenderState(
		RenderPipeline pipeline, TextureSetup textureSetup,
		Matrix3x2f pose,
		int x, int cy, float hm,
		@Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {
	private static final int[] TRIANGLE_COLORS = {
			0xff000000,
			0xff603813,
			0xff74d7ec,
			0xffffafc7,
			0xfffbf9f5,
	};

	public PrideFlagShapeProgressRenderState(
			RenderPipeline pipeline, TextureSetup textureSetup,
			Matrix3x2f pose,
			int x, int cy, float hm,
			@Nullable ScreenRectangle scissorArea
	) {
		this(
				pipeline, textureSetup,
				pose,
				x, cy, hm,
				scissorArea, getBounds(x, cy, hm, pose, scissorArea)
		);
	}

	@Override
	public void buildVertices(VertexConsumer vertexConsumer, float z) {
		float s = this.hm;
		for (int color : TRIANGLE_COLORS) {
			vertexConsumer.addVertexWith2DPose(this.pose(), this.x, this.cy + s, z).color(color);
			vertexConsumer.addVertexWith2DPose(this.pose(), this.x + (s * 1.1f), this.cy, z).color(color);
			vertexConsumer.addVertexWith2DPose(this.pose(), this.x, this.cy - s, z).color(color);
			// Dirty 4th vertex as GUI only accepts quads.
			vertexConsumer.addVertexWith2DPose(this.pose(), this.x, this.cy, z).color(color);
			s -= this.hm / 6;
		}
	}

	@Nullable
	private static ScreenRectangle getBounds(
			int x, int cy, float hm, Matrix3x2f pose, @Nullable ScreenRectangle scissorArea
	) {
		var defaultBounds = new ScreenRectangle(x, (int) (cy - hm), (int) (hm * 1.5f), (int) (hm * 2))
				.transformMaxBounds(pose);
		return scissorArea != null ? scissorArea.intersection(defaultBounds) : defaultBounds;
	}
}
