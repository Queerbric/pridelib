package io.github.queerbric.pride.shape;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.queerbric.pride.data.PrideData;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.gui.GuiGraphics;

public record HorizontalPrideFlagShape(IntList colors) implements PrideFlagShape {
	public static final MapCodec<HorizontalPrideFlagShape> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					PrideData.COLOR_LIST_CODEC.fieldOf("colors").forGetter(HorizontalPrideFlagShape::colors)
			).apply(instance, HorizontalPrideFlagShape::new)
	);

	@Override
	public Type type() {
		return PrideFlagShape.HORIZONTAL_STRIPES_TYPE;
	}

	@Override
	public void render(GuiGraphics graphics, int x, int y, int width, int height) {
		float currentY = y;
		float sh = (float) height / this.colors.size();
		for (int i = 0; i < this.colors.size(); i++) {
			int color = this.colors.getInt(i);
			graphics.fill(x, (int) currentY, x + width, (int) (currentY + sh), color);
			currentY += sh;
		}
	}
}
