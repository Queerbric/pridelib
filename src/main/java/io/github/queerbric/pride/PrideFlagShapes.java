package io.github.queerbric.pride;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

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
			GL11.glEnable(3042);
			GL11.glDisable(3553);
			Tessellator t = Tessellator.INSTANCE;

			for (int i = 0; i < colors.size(); i++) {
				t.method_1408(7);
				int color = colors.getInt(i);
				float r = ((color >> 16) & 0xFF) / 255f;
				float g = ((color >> 8) & 0xFF) / 255f;
				float b = ((color) & 0xFF) / 255f;
				GL11.glColor4f(r, g, b, 1);
				t.method_1398(x, y + sh, 0);
				t.method_1398(x + w, y + sh, 0);
				t.method_1398(x + w, y, 0);
				t.method_1398(x, y, 0);
				t.method_1396();
				y += sh;
			}

			GL11.glColor4f(1, 1, 1, 1);
			// Mojang when will you use your state manager system to add fast pushAttrib/popAttrib
			GL11.glEnable(3553);
			GL11.glDisable(3042);
		});
		register(new Identifier("pride", "vertical_stripes"), (colors, x, y, w, h) -> {
			float sw = w / colors.size();
			GL11.glEnable(3042);
			GL11.glDisable(3553);
			Tessellator t = Tessellator.INSTANCE;

			for (int i = 0; i < colors.size(); i++) {
				t.method_1408(7);
				int color = colors.getInt(i);
				float r = ((color >> 16) & 0xFF) / 255f;
				float g = ((color >> 8) & 0xFF) / 255f;
				float b = ((color) & 0xFF) / 255f;
				GL11.glColor4f(r, g, b, 1);
				t.method_1398(x, y + h, 0);
				t.method_1398(x + sw, y + h, 0);
				t.method_1398(x + sw, y, 0);
				t.method_1398(x, y, 0);
				t.method_1396();
				x += sw;
			}

			GL11.glColor4f(1, 1, 1, 1);
			GL11.glEnable(3553);
			GL11.glDisable(3042);
		});
		register(new Identifier("pride", "circle"), (colors, x, y, w, h) -> {
			GL11.glEnable(3042);
			GL11.glDisable(3553);
			Tessellator tess = Tessellator.INSTANCE;
			{
				int color = colors.getInt(0);
				float r = ((color >> 16) & 0xFF) / 255f;
				float g = ((color >> 8) & 0xFF) / 255f;
				float b = ((color) & 0xFF) / 255f;
				GL11.glColor4f(r, g, b, 1);
				tess.method_1408(7);
				tess.method_1398(x, y + h, 0);
				tess.method_1398(x + w, y + h, 0);
				tess.method_1398(x + w, y, 0);
				tess.method_1398(x, y, 0);
				tess.method_1396();
			}

			float br = Math.min(w, h) * 0.3f;
			float cx = x + (w / 2);
			float cy = y + (h / 2);
			for (int p = 0; p < 2; p++) {
				tess.method_1408(6); // Seems like the int-pendant to TRIANGLE_FAN is 6.
				float rd = (p == 0 ? br : br * 0.8f);
				int color = (p == 0 ? colors.getInt(1) : colors.getInt(0));
				float r = ((color >> 16) & 0xFF) / 255f;
				float g = ((color >> 8) & 0xFF) / 255f;
				float b = ((color) & 0xFF) / 255f;
				GL11.glColor4f(r, g, b, 1);
				tess.method_1398(cx, cy, 0);
				for (int i = 0; i < 65; i++) {
					float t = (i / 64f);
					final float TAU = (float) (Math.PI * 2);
					tess.method_1398(cx + (MathHelper.sin(t * TAU) * rd), cy + (MathHelper.cos(t * TAU) * rd), 0);
				}
				tess.method_1396();
			}

			GL11.glColor4f(1, 1, 1, 1);
			GL11.glEnable(3553);
			GL11.glDisable(3042);
		});
		register(new Identifier("pride", "arrow"), (colors, x, y, w, h) -> {
			float s = Math.min(w, h) / 2;
			float cy = y + (h / 2);
			horizStripes.render(colors.subList(1, colors.size()), x, y, w, h);
			GL11.glEnable(3042);
			GL11.glDisable(3553);
			Tessellator t = Tessellator.INSTANCE;
			t.method_1408(4); // Int-pendant to TRIANGLE is 4.
			int color = colors.getInt(0);
			float r = ((color >> 16) & 0xFF) / 255f;
			float g = ((color >> 8) & 0xFF) / 255f;
			float b = ((color) & 0xFF) / 255f;
			GL11.glColor4f(r, g, b, 1);
			t.method_1398(x, cy + s, 0);
			// yes, 1.5. the demisexual flag triangle appears to not be equilateral?
			t.method_1398(x + (s * 1.5f), cy, 0);
			t.method_1398(x, cy - s, 0);
			t.method_1396();
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glEnable(3553);
			GL11.glDisable(3042);
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
			Tessellator t = Tessellator.INSTANCE;
			horizStripes.render(progressBg, x, y, w, h);
			GL11.glEnable(3042);
			GL11.glDisable(3553);

			int[] triangleColors = {
					0x000000,
					0x603813,
					0x74D7EC,
					0xFFAFC7,
					0xFBF9F5,
			};
			float s = hm;
			for (int color : triangleColors) {
				t.method_1408(4);
				float r = ((color >> 16) & 0xFF) / 255f;
				float g = ((color >> 8) & 0xFF) / 255f;
				float b = ((color) & 0xFF) / 255f;
				GL11.glColor4f(r, g, b, 1);
				t.method_1398(x, cy + s, 0);
				t.method_1398(x + (s * 1.1f), cy, 0);
				t.method_1398(x, cy - s, 0);
				s -= hm / 6;
				t.method_1396();
			}

			GL11.glColor4f(1, 1, 1, 1);
			GL11.glEnable(3553);
			GL11.glDisable(3042);
		});
	}
}
