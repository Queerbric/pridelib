package io.github.queerbric.pride;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;

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
		register(new Identifier("pride", "horizontal_stripes"), horizStripes = (colors, matrices, x, y, w, h) -> {
			float sh = h / colors.size();
			RenderSystem.disableTexture();
			Matrix4f mat = matrices.peek().getModel();
			Tessellator t = Tessellator.getInstance();
			BufferBuilder bb = t.getBuffer();
			bb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			for (int i = 0; i < colors.size(); i++) {
				int color = colors.getInt(i);
				float r = ((color >> 16) & 0xFF) / 255f;
				float g = ((color >> 8) & 0xFF) / 255f;
				float b = ((color >> 0) & 0xFF) / 255f;
				bb.vertex(mat, x, y + sh, 0).color(r, g, b, 1).next();
				bb.vertex(mat, x + w, y + sh, 0).color(r, g, b, 1).next();
				bb.vertex(mat, x + w, y, 0).color(r, g, b, 1).next();
				bb.vertex(mat, x, y, 0).color(r, g, b, 1).next();
				y += sh;
			}
			t.draw();
			// Mojang when will you use your state manager system to add fast pushAttrib/popAttrib
			RenderSystem.enableTexture();
		});
		register(new Identifier("pride", "vertical_stripes"), (colors, matrices, x, y, w, h) -> {
			float sw = w / colors.size();
			RenderSystem.disableTexture();
			Matrix4f mat = matrices.peek().getModel();
			Tessellator t = Tessellator.getInstance();
			BufferBuilder bb = t.getBuffer();
			bb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			for (int i = 0; i < colors.size(); i++) {
				int color = colors.getInt(i);
				float r = ((color >> 16) & 0xFF) / 255f;
				float g = ((color >> 8) & 0xFF) / 255f;
				float b = ((color >> 0) & 0xFF) / 255f;
				bb.vertex(mat, x, y + h, 0).color(r, g, b, 1).next();
				bb.vertex(mat, x + sw, y + h, 0).color(r, g, b, 1).next();
				bb.vertex(mat, x + sw, y, 0).color(r, g, b, 1).next();
				bb.vertex(mat, x, y, 0).color(r, g, b, 1).next();
				x += sw;
			}
			t.draw();
			RenderSystem.enableTexture();
		});
		register(new Identifier("pride", "circle"), (colors, matrices, x, y, w, h) -> {
			RenderSystem.disableTexture();
			Matrix4f mat = matrices.peek().getModel();
			Tessellator tess = Tessellator.getInstance();
			BufferBuilder bb = tess.getBuffer();
			{
				int color = colors.getInt(0);
				float r = ((color >> 16) & 0xFF) / 255f;
				float g = ((color >> 8) & 0xFF) / 255f;
				float b = ((color >> 0) & 0xFF) / 255f;
				bb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
				bb.vertex(mat, x, y + h, 0).color(r, g, b, 1).next();
				bb.vertex(mat, x + w, y + h, 0).color(r, g, b, 1).next();
				bb.vertex(mat, x + w, y, 0).color(r, g, b, 1).next();
				bb.vertex(mat, x, y, 0).color(r, g, b, 1).next();
				tess.draw();
			}
			bb.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
			float br = Math.min(w, h) * 0.3f;
			float cx = x + (w / 2);
			float cy = y + (h / 2);
			for (int p = 0; p < 2; p++) {
				float rd = (p == 0 ? br : br * 0.8f);
				int color = (p == 0 ? colors.getInt(1) : colors.getInt(0));
				float r = ((color >> 16) & 0xFF) / 255f;
				float g = ((color >> 8) & 0xFF) / 255f;
				float b = ((color >> 0) & 0xFF) / 255f;
				bb.vertex(mat, cx, cy, 0).color(r, g, b, 1).next();
				for (int i = 0; i < 65; i++) {
					float t = (i / 64f);
					final float TAU = (float) (Math.PI * 2);
					bb.vertex(mat, cx + (MathHelper.sin(t * TAU) * rd), cy + (MathHelper.cos(t * TAU) * rd), 0).color(r, g, b, 1).next();
				}
			}
			tess.draw();
			RenderSystem.enableTexture();
		});
		register(new Identifier("pride", "arrow"), (colors, matrices, x, y, w, h) -> {
			float s = Math.min(w, h) / 2;
			float cy = y + (h / 2);
			horizStripes.render(colors.subList(1, colors.size()), matrices, x, y, w, h);
			RenderSystem.disableTexture();
			Matrix4f mat = matrices.peek().getModel();
			Tessellator t = Tessellator.getInstance();
			BufferBuilder bb = t.getBuffer();
			bb.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
			int color = colors.getInt(0);
			float r = ((color >> 16) & 0xFF) / 255f;
			float g = ((color >> 8) & 0xFF) / 255f;
			float b = ((color >> 0) & 0xFF) / 255f;
			bb.vertex(mat, x, cy + s, 0).color(r, g, b, 1).next();
			// yes, 1.5. the demisexual flag triangle appears to not be equilateral?
			bb.vertex(mat, x + (s * 1.5f), cy, 0).color(r, g, b, 1).next();
			bb.vertex(mat, x, cy - s, 0).color(r, g, b, 1).next();
			t.draw();
			RenderSystem.enableTexture();
		});
		var progressBg = new IntArrayList(new int[]{
				0xD40606,
				0xEE9C00,
				0xE3FF00,
				0x06BF00,
				0x001A98,
				0x760089,
		});
		register(new Identifier("pride", "progress"), (colors, matrices, x, y, w, h) -> {
			float hm = Math.min(w, h) / 2;
			float cy = y + (h / 2);
			Matrix4f mat = matrices.peek().getModel();
			Tessellator t = Tessellator.getInstance();
			BufferBuilder bb = t.getBuffer();
			horizStripes.render(progressBg, matrices, x, y, w, h);
			RenderSystem.disableTexture();
			bb.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
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
				float b = ((color >> 0) & 0xFF) / 255f;
				bb.vertex(mat, x, cy + s, 0).color(r, g, b, 1).next();
				bb.vertex(mat, x + (s * 1.1f), cy, 0).color(r, g, b, 1).next();
				bb.vertex(mat, x, cy - s, 0).color(r, g, b, 1).next();
				s -= hm / 6;
			}
			t.draw();
			RenderSystem.enableTexture();
		});
	}
}
