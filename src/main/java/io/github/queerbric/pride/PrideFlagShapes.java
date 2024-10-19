package io.github.queerbric.pride;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

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
			var client = Minecraft.getInstance();
			float radius = Math.min(w, h) * 0.3f;
			float cx = x + (w / 2.f);
			float cy = y + (h / 2.f);

			graphics.fill(x, y, x + w, y + h, colors.getFirst());

			var program = client.getShaderManager().getProgram(PrideClient.FLAG_SHAPE_CIRCLE_SHADER);
			var radiusUniform = program.getUniform("radius");
			if (radiusUniform != null) radiusUniform.set(radius, radius - radius * .8f);
			var centerPointUniform = program.getUniform("center_pos");
			if (centerPointUniform != null) centerPointUniform.set(cx, cy);

			graphics.fill(PrideClient.FLAG_SHAPE_CIRCLE_RENDER_TYPE, x, y, x + w, y + h, colors.getInt(1));
		});
		register(Identifier.of("pride", "arrow"), (graphics, colors, x, y, w, h) -> {
			horizStripes.render(graphics, colors.subList(1, colors.size()), x, y, w, h);

			graphics.drawSpecial(bufferSource -> {
				float s = Math.min(w, h) / 2.f;
				float cy = y + (h / 2.f);
				var buffer = bufferSource.getBuffer(PrideClient.FLAG_SHAPE_TRIANGLE_RENDER_TYPE);
				int color = colors.getInt(0);
				buffer.addVertex(x, cy + s, 0).color(color);
				// yes, 1.5. the demisexual flag triangle appears to not be equilateral?
				buffer.addVertex(x + (s * 1.5f), cy, 0).color(color);
				buffer.addVertex(x, cy - s, 0).color(color);
			});
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

			graphics.drawSpecial(bufferSource -> {
				float hm = Math.min(w, h) / 2.f;
				int cy = (int) (y + (h / 2.f));

				var buffer = bufferSource.getBuffer(PrideClient.FLAG_SHAPE_TRIANGLE_RENDER_TYPE);

				int[] triangleColors = {
						0xff000000,
						0xff603813,
						0xff74d7ec,
						0xffffafc7,
						0xfffbf9f5,
				};
				float s = hm;
				for (int color : triangleColors) {
					buffer.addVertex(x, cy + s, 0).color(color);
					buffer.addVertex(x + (s * 1.1f), cy, 0).color(color);
					buffer.addVertex(x, cy - s, 0).color(color);
					s -= hm / 6;
				}
			});
		});
	}
}
