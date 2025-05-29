package io.github.queerbric.pride;

import io.github.queerbric.pride.impl.PrideFlagShapeArrowRenderState;
import io.github.queerbric.pride.impl.PrideFlagShapeCircleRenderState;
import io.github.queerbric.pride.impl.PrideFlagShapeProgressRenderState;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.joml.Matrix3x2f;
import org.joml.Vector2f;

import java.util.Map;

public final class PrideFlagShapes {
	private static final Map<Identifier, PrideFlagShape> REGISTRY = new Object2ObjectOpenHashMap<>();

	public static PrideFlagShape get(Identifier id) {
		return REGISTRY.get(id);
	}

	public static void register(Identifier id, PrideFlagShape shape) {
		REGISTRY.put(id, shape);
	}

	private PrideFlagShapes() {
	}

	static {
		PrideFlagShape horizStripes;
		register(Identifier.of("pride", "horizontal_stripes"), horizStripes = (graphics, colors, x, y, w, h) -> {
			float currentY = y;
			float sh = (float) h / colors.size();
			for (int i = 0; i < colors.size(); i++) {
				int color = colors.getInt(i);
				graphics.fill(x, (int) currentY, x + w, (int) (currentY + sh), color);
				currentY += sh;
			}
		});
		register(Identifier.of("pride", "vertical_stripes"), (graphics, colors, x, y, w, h) -> {
			float currentX = x;
			float sw = (float) w / colors.size();
			for (int i = 0; i < colors.size(); i++) {
				int color = colors.getInt(i);
				graphics.fill((int) currentX, y, (int) (currentX + sw), y + h, color);
				currentX += sw;
			}
		});
		register(Identifier.of("pride", "circle"), (graphics, colors, x, y, w, h) -> {
			float radius = Math.min(w, h) * 0.3f;
			float cx = x + (w / 2.f);
			float cy = y + (h / 2.f);

			graphics.fill(x, y, x + w, y + h, colors.getFirst());

			graphics.guiRenderState.submitGuiElement(
					new PrideFlagShapeCircleRenderState(
							PrideClient.FLAG_SHAPE_CIRCLE_PIPELINE, TextureSetup.noTexture(),
							new Matrix3x2f(graphics.pose()),
							x, y, x + w, y + h,
							colors.getInt(1),
							new Vector2f(cx, cy),
							(int) radius, (int) (radius - radius * .8f),
							null
					)
			);
		});
		register(Identifier.of("pride", "arrow"), (graphics, colors, x, y, w, h) -> {
			horizStripes.render(graphics, colors.subList(1, colors.size()), x, y, w, h);

			float s = Math.min(w, h) / 2.f;
			float cy = y + (h / 2.f);

			graphics.guiRenderState.submitGuiElement(
					new PrideFlagShapeArrowRenderState(
							RenderPipelines.GUI, TextureSetup.noTexture(),
							new Matrix3x2f(graphics.pose()),
							x, cy, s,
							colors.getInt(0),
							null
					)
			);
		});
		var progressBg = new IntArrayList(new int[]{
				0xffd40606,
				0xffee9C00,
				0xffe3ff00,
				0xff06bf00,
				0xff001a98,
				0xff760089,
		});
		register(Identifier.of("pride", "progress"), (graphics, colors, x, y, w, h) -> {
			horizStripes.render(graphics, progressBg, x, y, w, h);

			float hm = Math.min(w, h) / 2.f;
			int cy = (int) (y + (h / 2.f));

			graphics.guiRenderState.submitGuiElement(
					new PrideFlagShapeProgressRenderState(
							RenderPipelines.GUI, TextureSetup.noTexture(),
							new Matrix3x2f(graphics.pose()),
							x, cy, hm,
							null
					)
			);
		});
	}
}
