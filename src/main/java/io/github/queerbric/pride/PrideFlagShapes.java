package io.github.queerbric.pride;

import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Map;

public class PrideFlagShapes {
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
		register(new Identifier("pride", "horizontal_stripes"), horizStripes = (colors, x, y, w, h) -> {
			float sh = h / colors.size();
			GlStateManager.disableTexture();
			Tessellator t = Tessellator.getInstance();
			BufferBuilder bb = t.getBuffer();
			bb.begin(7, VertexFormats.POSITION_COLOR);
			for (int i = 0; i < colors.size(); i++) {
				int color = colors.getInt(i);
				float r = ((color >> 16) & 0xFF) / 255f;
				float g = ((color >> 8) & 0xFF) / 255f;
				float b = ((color) & 0xFF) / 255f;
				bb.vertex(x, y + sh, 0).color(r, g, b, 1).next();
				bb.vertex(x + w, y + sh, 0).color(r, g, b, 1).next();
				bb.vertex(x + w, y, 0).color(r, g, b, 1).next();
				bb.vertex(x, y, 0).color(r, g, b, 1).next();
				y += sh;
			}
			t.draw();
			// Mojang when will you use your state manager system to add fast pushAttrib/popAttrib
			GlStateManager.enableTexture();
		});
		register(new Identifier("pride", "vertical_stripes"), (colors, x, y, w, h) -> {
			float sw = w / colors.size();
			GlStateManager.disableTexture();
			Tessellator t = Tessellator.getInstance();
			BufferBuilder bb = t.getBuffer();
			bb.begin(7, VertexFormats.POSITION_COLOR);
			for (int i = 0; i < colors.size(); i++) {
				int color = colors.getInt(i);
				float r = ((color >> 16) & 0xFF) / 255f;
				float g = ((color >> 8) & 0xFF) / 255f;
				float b = ((color) & 0xFF) / 255f;
				bb.vertex(x, y + h, 0).color(r, g, b, 1).next();
				bb.vertex(x + sw, y + h, 0).color(r, g, b, 1).next();
				bb.vertex(x + sw, y, 0).color(r, g, b, 1).next();
				bb.vertex(x, y, 0).color(r, g, b, 1).next();
				x += sw;
			}
			t.draw();
			GlStateManager.enableTexture();
		});
		register(new Identifier("pride", "circle"), (colors, x, y, w, h) -> {
			GlStateManager.disableTexture();
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder bb = tess.getBuffer();
			{
				int color = colors.getInt(0);
				float r = ((color >> 16) & 0xFF) / 255f;
				float g = ((color >> 8) & 0xFF) / 255f;
				float b = ((color) & 0xFF) / 255f;
				bb.begin(7, VertexFormats.POSITION_COLOR);
				bb.vertex(x, y + h, 0).color(r, g, b, 1).next();
				bb.vertex(x + w, y + h, 0).color(r, g, b, 1).next();
				bb.vertex(x + w, y, 0).color(r, g, b, 1).next();
				bb.vertex(x, y, 0).color(r, g, b, 1).next();
				tess.draw();
			}
			bb.begin(6, VertexFormats.POSITION_COLOR); // Seems like the int-pendant to TRIANGLE_FAN is 6.
			float br = Math.min(w, h) * 0.3f;
			float cx = x + (w / 2);
			float cy = y + (h / 2);
			for (int p = 0; p < 2; p++) {
				float rd = (p == 0 ? br : br * 0.8f);
				int color = (p == 0 ? colors.getInt(1) : colors.getInt(0));
				float r = ((color >> 16) & 0xFF) / 255f;
				float g = ((color >> 8) & 0xFF) / 255f;
				float b = ((color) & 0xFF) / 255f;
				bb.vertex(cx, cy, 0).color(r, g, b, 1).next();
				for (int i = 0; i < 65; i++) {
					float t = (i / 64f);
					final float TAU = (float) (Math.PI * 2);
					bb.vertex(cx + (MathHelper.sin(t * TAU) * rd), cy + (MathHelper.cos(t * TAU) * rd), 0).color(r, g, b, 1).next();
				}
			}
			tess.draw();
			GlStateManager.enableTexture();
		});
		register(new Identifier("pride", "arrow"), (colors, x, y, w, h) -> {
			float s = Math.min(w, h) / 2;
			float cy = y + (h / 2);
			horizStripes.render(colors.subList(1, colors.size()), x, y, w, h);
			GlStateManager.disableTexture();
			Tessellator t = Tessellator.getInstance();
			BufferBuilder bb = t.getBuffer();
			bb.begin(4, VertexFormats.POSITION_COLOR); // Int-pendant to TRIANGLE is 4.
			int color = colors.getInt(0);
			float r = ((color >> 16) & 0xFF) / 255f;
			float g = ((color >> 8) & 0xFF) / 255f;
			float b = ((color) & 0xFF) / 255f;
			bb.vertex(x, cy + s, 0).color(r, g, b, 1).next();
			// yes, 1.5. the demisexual flag triangle appears to not be equilateral?
			bb.vertex(x + (s * 1.5f), cy, 0).color(r, g, b, 1).next();
			bb.vertex(x, cy - s, 0).color(r, g, b, 1).next();
			t.draw();
			GlStateManager.enableTexture();
		});
		IntArrayList progressBg = new IntArrayList(new int[]{
				0xD40606,
				0xEE9C00,
				0xE3FF00,
				0x06BF00,
				0x001A98,
				0x760089,
		});
		register(new Identifier("pride", "progress"), (colors, x, y, w, h) -> {
			float hm = Math.min(w, h) / 2;
			float cy = y + (h / 2);
			Tessellator t = Tessellator.getInstance();
			BufferBuilder bb = t.getBuffer();
			horizStripes.render(progressBg, x, y, w, h);
			GlStateManager.disableTexture();
			bb.begin(4, VertexFormats.POSITION_COLOR);
			int[] triangleColors = {
					0x000000,
					0x603813,
					0x74D7EC,
					0xFFAFC7,
					0xFBF9F5,
			};
			float s = hm;
			for (int color : triangleColors) {
				float r = ((color >> 16) & 0xFF) / 255f;
				float g = ((color >> 8) & 0xFF) / 255f;
				float b = ((color) & 0xFF) / 255f;
				bb.vertex(x, cy + s, 0).color(r, g, b, 1).next();
				bb.vertex(x + (s * 1.1f), cy, 0).color(r, g, b, 1).next();
				bb.vertex(x, cy - s, 0).color(r, g, b, 1).next();
				s -= hm / 6;
			}
			t.draw();
			GlStateManager.enableTexture();
		});
	}
}
