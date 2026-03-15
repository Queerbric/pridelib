package io.github.queerbric.pride;

import io.github.queerbric.pride.shape.PrideFlagShape;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Represents a pride flag.
 */
public class PrideFlag {
	private final String id;
	private final PrideFlagShape shape;

	protected PrideFlag(String id, PrideFlagShape shape) {
		this.id = id;
		this.shape = shape;
	}

	public String getId() {
		return this.id;
	}

	public PrideFlagShape getShape() {
		return this.shape;
	}

	/**
	 * Renders this flag at the specified coordinates and with the specified dimensions.
	 *
	 * @param graphics the GUI graphics
	 * @param x the X-coordinate to render to
	 * @param y the Y-coordinate to render to
	 * @param width the render width of the flag
	 * @param height the render height of the flag
	 */
	@Environment(EnvType.CLIENT)
	public void extractRenderState(GuiGraphicsExtractor graphics, int x, int y, int width, int height) {
		this.shape.extractRenderState(graphics, x, y, width, height);
	}
}
