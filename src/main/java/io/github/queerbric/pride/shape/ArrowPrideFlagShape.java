package io.github.queerbric.pride.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.queerbric.pride.data.PrideData;
import io.github.queerbric.pride.impl.PrideFlagShapeArrowRenderState;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import org.joml.Matrix3x2f;

public record ArrowPrideFlagShape(
		PrideFlagShape background,
		IntList colors,
		float advanceRatio
) implements PrideFlagShape {
	public static final MapCodec<ArrowPrideFlagShape> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					PrideFlagShape.CODEC.fieldOf("background").forGetter(ArrowPrideFlagShape::background),
					PrideData.COLOR_LIST_CODEC.fieldOf("colors").forGetter(ArrowPrideFlagShape::colors),
					Codec.FLOAT.optionalFieldOf("advance_ratio", 1.5f)
							.forGetter(ArrowPrideFlagShape::advanceRatio)
			).apply(instance, ArrowPrideFlagShape::new)
	);

	@Override
	public Type type() {
		return PrideFlagShape.ARROW_TYPE;
	}

	@Override
	public void render(GuiGraphics graphics, int x, int y, int width, int height) {
		this.background.render(graphics, x, y, width, height);

		float hm = Math.min(width, height) / 2.f;
		float cy = y + (height / 2.f);

		float s = hm;
		for (int color : this.colors) {
			graphics.guiRenderState.submitGuiElement(
					new PrideFlagShapeArrowRenderState(
							RenderPipelines.GUI, TextureSetup.noTexture(),
							new Matrix3x2f(graphics.pose()),
							x, cy, s, this.advanceRatio,
							color,
							graphics.scissorStack.peek()
					)
			);

			s -= hm / this.colors.size();
		}
	}
}
