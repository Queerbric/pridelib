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
import org.joml.Vector2fc;

@Environment(EnvType.CLIENT)
public record PrideFlagShapeCircleRenderState(
		RenderPipeline pipeline, TextureSetup textureSetup,
		Matrix3x2f pose,
		int startX, int startY,
		int endX, int endY,
		int color,
		Vector2fc centerPos, int outerRadius, int innerRadius,
		@Nullable ScreenRectangle scissorArea, @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {
	public PrideFlagShapeCircleRenderState(
			RenderPipeline pipeline, TextureSetup textureSetup,
			Matrix3x2f pose,
			int startX, int startY,
			int endX, int endY,
			int color,
			Vector2fc centerPos, int outerRadius, int innerRadius,
			@Nullable ScreenRectangle scissorArea
	) {
		this(
				pipeline, textureSetup,
				pose,
				startX, startY,
				endX, endY,
				color,
				centerPos, outerRadius, innerRadius,
				scissorArea, getBounds(startX, startY, endX, endY, pose, scissorArea)
		);
	}

	@Override
	public void buildVertices(VertexConsumer vertexConsumer) {
		vertexConsumer.addVertexWith2DPose(this.pose(), this.startX(), this.startY())
				.setColor(this.color())
				.setUv(this.centerPos.x(), this.centerPos.y())
				.setUv2(this.outerRadius, this.innerRadius);
		vertexConsumer.addVertexWith2DPose(this.pose(), this.startX(), this.endY())
				.setColor(this.color())
				.setUv(this.centerPos.x(), this.centerPos.y())
				.setUv2(this.outerRadius, this.innerRadius);
		vertexConsumer.addVertexWith2DPose(this.pose(), this.endX(), this.endY())
				.setColor(this.color())
				.setUv(this.centerPos.x(), this.centerPos.y())
				.setUv2(this.outerRadius, this.innerRadius);
		vertexConsumer.addVertexWith2DPose(this.pose(), this.endX(), this.startY())
				.setColor(this.color())
				.setUv(this.centerPos.x(), this.centerPos.y())
				.setUv2(this.outerRadius, this.innerRadius);
	}

	@Nullable
	private static ScreenRectangle getBounds(
			int startX, int startY, int endX, int endY, Matrix3x2f pos, @Nullable ScreenRectangle scissorArea
	) {
		var defaultBounds = new ScreenRectangle(startX, startY, endX - startX, endY - startY)
				.transformMaxBounds(pos);
		return scissorArea != null ? scissorArea.intersection(defaultBounds) : defaultBounds;
	}
}
