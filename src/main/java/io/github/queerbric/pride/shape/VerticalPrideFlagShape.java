package io.github.queerbric.pride.shape;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.queerbric.pride.data.PrideData;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public record VerticalPrideFlagShape(IntList colors) implements PrideFlagShape {
	public static final MapCodec<VerticalPrideFlagShape> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
					PrideData.COLOR_LIST_CODEC.fieldOf("colors").forGetter(VerticalPrideFlagShape::colors)
			).apply(instance, VerticalPrideFlagShape::new)
	);

	@Override
	public Type type() {
		return PrideFlagShape.VERTICAL_STRIPES_TYPE;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
		float currentX = x;
		float sw = (float) width / this.colors.size();
		for (int i = 0; i < this.colors.size(); i++) {
			int color = this.colors.getInt(i);
			graphics.fill((int) currentX, y, (int) (currentX + sw), y + height, color);
			currentX += sw;
		}
	}
}
