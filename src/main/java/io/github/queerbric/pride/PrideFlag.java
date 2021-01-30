package io.github.queerbric.pride;

import net.minecraft.util.math.MathHelper;

public class PrideFlag {
	private String id;
	private String shape;
	private int[] colors;

	public PrideFlag(String id, Builder builder) {
		this.id = id;
		if (builder.shape == null) {
			shape = "stripes";
		} else {
			this.shape = builder.shape;
		}
		colors = new int[builder.colors.length];
		for (int i = 0; i < builder.colors.length; i++) {
			String bc = builder.colors[i];
			colors[i] = MathHelper.packRgb(Integer.parseInt(bc.substring(1, 3), 16), Integer.parseInt(bc.substring(3, 5), 16),
				Integer.parseInt(bc.substring(5, 7), 16));
		}
	}

	public String getId() {
		return id;
	}

	public String getShape() {
		return shape;
	}
	
	public int[] getColors() {
		return colors;
	}
	
	class Builder {
		public String shape;
		public String[] colors;
	}
}
