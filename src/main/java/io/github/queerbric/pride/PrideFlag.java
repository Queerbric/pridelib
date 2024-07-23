package io.github.queerbric.pride;

import com.mojang.blaze3d.vertex.MatrixStack;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.Identifier;

/**
 * Represents a pride flag.
 */
public class PrideFlag {
	private final String id;
	private final PrideFlagShape shape;
	private final IntList colors;
	private final Identifier shapeId;

	protected PrideFlag(String id, Properties props) {
		this.id = id;
		if (props.shape == null) {
			this.shapeId = Identifier.of("pride", "horizontal_stripes");
		} else {
			this.shapeId = props.shape.contains(":") ? Identifier.tryParse(props.shape) : Identifier.of("pride", props.shape);
		}

		this.shape = PrideFlagShapes.get(this.shapeId);
		if (this.shape == null) {
			throw new IllegalArgumentException("Unknown pride flag shape " + this.shapeId);
		}

		var colorsTmp = new IntArrayList(props.colors.length);
		for (var color : props.colors) {
			colorsTmp.add(Integer.parseInt(color.substring(1), 16) | 0xFF000000);
		}
		this.colors = IntLists.unmodifiable(colorsTmp);
	}

	public String getId() {
		return this.id;
	}

	public PrideFlagShape getShape() {
		return this.shape;
	}

	public Identifier getShapeId() {
		return this.shapeId;
	}

	public IntList getColors() {
		return this.colors;
	}

	/**
	 * Renders this flag at the specified coordinates and with the specified dimensions.
	 *
	 * @param matrices the matrix stack
	 * @param x the X-coordinate to render to
	 * @param y the Y-coordinate to render to
	 * @param width the render width of the flag
	 * @param height the render height of the flag
	 */
	@Environment(EnvType.CLIENT)
	public void render(MatrixStack matrices, float x, float y, float width, float height) {
		this.shape.render(this.colors, matrices, x, y, width, height);
	}

	protected static class Properties {
		public String shape;
		public String[] colors;
	}
}
