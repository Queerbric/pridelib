package io.github.queerbric.pride;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * Represents a pride flag.
 */
public class PrideFlag {
	private final String id;
	private final PrideFlagShape shape;
	private final IntList colors;

	protected PrideFlag(String id, Properties props) {
		this.id = id;
		Identifier shapeId;
		if (props.shape == null) {
			shapeId = new Identifier("pride", "horizontal_stripes");
		} else {
			shapeId = props.shape.contains(":") ? Identifier.tryParse(props.shape) : new Identifier("pride", props.shape);
		}

		shape = PrideFlagShapes.get(shapeId);
		if (shape == null) {
			throw new IllegalArgumentException("Unknown pride flag shape " + shapeId);
		}

		var colorsTmp = new IntArrayList(props.colors.length);
		for (var color : props.colors) {
			colorsTmp.add(Integer.parseInt(color.substring(1), 16) | 0xFF000000);
		}
		colors = IntLists.unmodifiable(colorsTmp);
	}

	public String getId() {
		return id;
	}

	public PrideFlagShape getShape() {
		return shape;
	}

	public IntList getColors() {
		return colors;
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
	public void render(MatrixStack matrices, float x, float y, float width, float height) {
		shape.render(colors, matrices, x, y, width, height);
	}

	static class Properties {
		public String shape;
		public String[] colors;
	}
}
