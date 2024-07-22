package io.github.queerbric.pride;

import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.unmapped.C_fpcijbbg;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;

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
		register(Identifier.of("pride", "horizontal_stripes"), horizStripes = (colors, matrices, x, y, w, h) -> {
			float sh = h / colors.size();
			MatrixStack.Entry mat = matrices.peek();
			Tessellator t = Tessellator.getInstance();
			BufferBuilder bb = t.method_60827(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			for (int i = 0; i < colors.size(); i++) {
				int color = colors.getInt(i);
				float r = ((color >> 16) & 0xFF) / 255f;
				float g = ((color >> 8) & 0xFF) / 255f;
				float b = ((color >> 0) & 0xFF) / 255f;
				bb.method_56824(mat, x, y + sh, 0).method_22915(r, g, b, 1);
				bb.method_56824(mat, x + w, y + sh, 0).method_22915(r, g, b, 1);
				bb.method_56824(mat, x + w, y, 0).method_22915(r, g, b, 1);
				bb.method_56824(mat, x, y, 0).method_22915(r, g, b, 1);
				y += sh;
			}
			drawAndClear(bb, t);
		});
		register(Identifier.of("pride", "vertical_stripes"), (colors, matrices, x, y, w, h) -> {
			float sw = w / colors.size();
			MatrixStack.Entry mat = matrices.peek();
			Tessellator t = Tessellator.getInstance();
			BufferBuilder bb = t.method_60827(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			for (int i = 0; i < colors.size(); i++) {
				int color = colors.getInt(i);
				float r = ((color >> 16) & 0xFF) / 255f;
				float g = ((color >> 8) & 0xFF) / 255f;
				float b = ((color >> 0) & 0xFF) / 255f;
				bb.method_56824(mat, x, y + h, 0).method_22915(r, g, b, 1);
				bb.method_56824(mat, x + sw, y + h, 0).method_22915(r, g, b, 1);
				bb.method_56824(mat, x + sw, y, 0).method_22915(r, g, b, 1);
				bb.method_56824(mat, x, y, 0).method_22915(r, g, b, 1);
				x += sw;
			}
			drawAndClear(bb, t);
		});
		register(Identifier.of("pride", "circle"), (colors, matrices, x, y, w, h) -> {
			MatrixStack.Entry mat = matrices.peek();
			Tessellator tess = Tessellator.getInstance();
			{
				BufferBuilder bb = tess.method_60827(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
				int color = colors.getInt(0);
				float r = ((color >> 16) & 0xFF) / 255f;
				float g = ((color >> 8) & 0xFF) / 255f;
				float b = ((color >> 0) & 0xFF) / 255f;
				bb.method_56824(mat, x, y + h, 0).method_22915(r, g, b, 1);
				bb.method_56824(mat, x + w, y + h, 0).method_22915(r, g, b, 1);
				bb.method_56824(mat, x + w, y, 0).method_22915(r, g, b, 1);
				bb.method_56824(mat, x, y, 0).method_22915(r, g, b, 1);
				drawAndClear(bb, tess);
			}
			BufferBuilder bb = tess.method_60827(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
			float br = Math.min(w, h) * 0.3f;
			float cx = x + (w / 2);
			float cy = y + (h / 2);
			for (int p = 0; p < 2; p++) {
				float rd = (p == 0 ? br : br * 0.8f);
				int color = (p == 0 ? colors.getInt(1) : colors.getInt(0));
				float r = ((color >> 16) & 0xFF) / 255f;
				float g = ((color >> 8) & 0xFF) / 255f;
				float b = ((color >> 0) & 0xFF) / 255f;
				bb.method_56824(mat, cx, cy, 0).method_22915(r, g, b, 1);
				for (int i = 0; i < 65; i++) {
					float t = (i / 64f);
					final float TAU = (float) (Math.PI * 2);
					bb.method_56824(mat, cx + (MathHelper.sin(t * TAU) * rd), cy + (MathHelper.cos(t * TAU) * rd), 0).method_22915(r, g, b, 1);
				}
			}
			drawAndClear(bb, tess);
		});
		register(Identifier.of("pride", "arrow"), (colors, matrices, x, y, w, h) -> {
			float s = Math.min(w, h) / 2;
			float cy = y + (h / 2);
			horizStripes.render(colors.subList(1, colors.size()), matrices, x, y, w, h);
			MatrixStack.Entry mat = matrices.peek();
			Tessellator t = Tessellator.getInstance();
			BufferBuilder bb = t.method_60827(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
			int color = colors.getInt(0);
			float r = ((color >> 16) & 0xFF) / 255f;
			float g = ((color >> 8) & 0xFF) / 255f;
			float b = ((color >> 0) & 0xFF) / 255f;
			bb.method_56824(mat, x, cy + s, 0).method_22915(r, g, b, 1);
			// yes, 1.5. the demisexual flag triangle appears to not be equilateral?
			bb.method_56824(mat, x + (s * 1.5f), cy, 0).method_22915(r, g, b, 1);
			bb.method_56824(mat, x, cy - s, 0).method_22915(r, g, b, 1);
			drawAndClear(bb, t);
		});
		var progressBg = new IntArrayList(new int[]{
				0xD40606,
				0xEE9C00,
				0xE3FF00,
				0x06BF00,
				0x001A98,
				0x760089,
		});
		register(Identifier.of("pride", "progress"), (colors, matrices, x, y, w, h) -> {
			float hm = Math.min(w, h) / 2;
			float cy = y + (h / 2);
			MatrixStack.Entry mat = matrices.peek();
			Tessellator t = Tessellator.getInstance();
			BufferBuilder bb = t.method_60827(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);

			horizStripes.render(progressBg, matrices, x, y, w, h);
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
				bb.method_56824(mat, x, cy + s, 0).method_22915(r, g, b, 1);
				bb.method_56824(mat, x + (s * 1.1f), cy, 0).method_22915(r, g, b, 1);
				bb.method_56824(mat, x, cy - s, 0).method_22915(r, g, b, 1);
				s -= hm / 6;
			}
			drawAndClear(bb, t);
		});
	}

	private static void drawAndClear(BufferBuilder builder, Tessellator tessellator) {
		C_fpcijbbg builtBuffer = builder.method_60794();
		if (builtBuffer != null) {
			BufferRenderer.drawWithShader(builtBuffer);
		}
		tessellator.method_60828();
	}
}
