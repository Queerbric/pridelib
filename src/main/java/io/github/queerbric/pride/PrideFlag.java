package io.github.queerbric.pride;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.minecraft.util.Identifier;

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
			this.shapeId = new Identifier("pride", "horizontal_stripes");
		} else {
			this.shapeId = parseIdentifier(props.shape);
		}

		this.shape = PrideFlagShapes.get(this.shapeId);
		if (this.shape == null) {
			throw new IllegalArgumentException("Unknown pride flag shape " + this.shapeId);
		}

		IntArrayList colorsTmp = new IntArrayList(props.colors.length);
		for (String color : props.colors) {
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
	 * @param x the X-coordinate to render to
	 * @param y the Y-coordinate to render to
	 * @param width the render width of the flag
	 * @param height the render height of the flag
	 */
	public void render(float x, float y, float width, float height) {
		this.shape.render(this.colors, x, y, width, height);
	}

	private Identifier parseIdentifier(String s){
		if(s.contains(":")){
			String[] array = s.split(":",1);
			return new Identifier(array[0], array[1]);
		}
		return new Identifier("pride", s);
	}

	static class Properties {
		public String shape;
		public String[] colors;
	}
}
