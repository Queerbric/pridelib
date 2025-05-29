package io.github.queerbric.pride.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.queerbric.pride.PrideClient;
import io.github.queerbric.pride.data.PrideData;
import io.github.queerbric.pride.impl.PrideFlagShapeCircleRenderState;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.TextureSetup;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;

public record CirclePrideFlagShape(
		PrideFlagShape background,
		int color,
		float radiusRatio,
		float thicknessRatio
) implements PrideFlagShape {
	public static final MapCodec<CirclePrideFlagShape> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					PrideFlagShape.CODEC.fieldOf("background").forGetter(CirclePrideFlagShape::background),
					PrideData.COLOR_CODEC.fieldOf("color").forGetter(CirclePrideFlagShape::color),
					Codec.FLOAT.optionalFieldOf("radius_ratio", .3f)
							.forGetter(CirclePrideFlagShape::radiusRatio),
					Codec.FLOAT.optionalFieldOf("thickness_ratio", .2f)
							.forGetter(CirclePrideFlagShape::thicknessRatio)
			).apply(instance, CirclePrideFlagShape::new)
	);

	@Override
	public @NotNull Type type() {
		return PrideFlagShape.CIRCLE_TYPE;
	}

	@Override
	public void render(GuiGraphics graphics, int x, int y, int width, int height) {
		this.background.render(graphics, x, y, width, height);

		float radius = Math.min(width, height) * this.radiusRatio;
		float cx = x + (width / 2.f);
		float cy = y + (height / 2.f);

		graphics.guiRenderState.submitGuiElement(
				new PrideFlagShapeCircleRenderState(
						PrideClient.FLAG_SHAPE_CIRCLE_PIPELINE, TextureSetup.noTexture(),
						new Matrix3x2f(graphics.pose()),
						x, y, x + width, y + height,
						this.color,
						new Vector2f(cx, cy),
						(int) radius, (int) (radius - radius * (1 - this.thicknessRatio)),
						graphics.scissorStack.peek()
				)
		);
	}
}
